/*
 * Copyright (C) 2022 Moacir da Roza Flores <moacirrf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.moacirrf.netbeans.markdown.ui.preview;

import io.github.moacirrf.netbeans.markdown.Context;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import static javax.swing.event.HyperlinkEvent.EventType.ENTERED;
import static javax.swing.event.HyperlinkEvent.EventType.EXITED;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.commons.lang3.StringUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class JEditorPaneImpl extends JEditorPane {

    public JEditorPaneImpl() {
        this.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getInputEvent() instanceof MouseEvent) {
                if (ACTIVATED.equals(e.getEventType())) {
                    if (e.getURL() != null) {
                        HtmlBrowser.URLDisplayer.getDefault().showURL(e.getURL());
                    } else {
                        openFile(e.getDescription());
                    }
                } else if (ENTERED.equals(e.getEventType())) {
                    setToolTipText(getTitle(e.getSourceElement()));
                } else if (EXITED.equals(e.getEventType())) {
                    setToolTipText(null);
                }
            }
        });
    }

    private void openFile(String file) {
        var arquivo = new File(file);
        if (!arquivo.exists()) {
            arquivo = new File(Context.OPENED_FILE.getParent().getPath(), file.replace("./", ""));
        }

        if (arquivo.exists()) {
            try {
                var dataObject = DataObject.find(FileUtil.toFileObject(arquivo));
                if (dataObject != null) {
                    var editorCookie = dataObject.getLookup().lookup(EditorCookie.class);
                    if (editorCookie != null) {
                        editorCookie.open();
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

    }

    private String getTitle(Element e) {
        if (e != null && e.getAttributes() != null) {
            var simpleAttributeSet = (SimpleAttributeSet) e.getAttributes().getAttribute(HTML.getTag(HTML.Tag.A.toString()));
            if (simpleAttributeSet != null) {
                String title = (String) simpleAttributeSet.getAttribute(HTML.getAttributeKey(HTML.Attribute.TITLE.toString()));
                if (StringUtils.isNotBlank(title)) {
                    return title;
                }
            }
        }

        return null;
    }

    /**
     * Get visible text of JEditorPane.
     *
     * @param jEditorPane The JEditorPane that you want get the visibleText
     * @param scrollPane The JScrollPane of the JEditorPane
     * @return return the visible part of text.
     */
    public static String getVisibleText(JEditorPane jEditorPane, JScrollPane scrollPane) {
        Point point = scrollPane.getViewport().getViewPosition();
        Point pointMax = (Point) point.clone();
        pointMax.move(pointMax.x, pointMax.y + scrollPane.getViewport().getHeight());
        int begin = jEditorPane.viewToModel2D(point);
        int end = jEditorPane.viewToModel2D(pointMax);
        if (begin >= 0) {
            try (var outputStream = new ByteArrayOutputStream()) {
                jEditorPane.getEditorKit().write(outputStream, jEditorPane.getDocument(), begin, end - begin);
                return outputStream.toString();
            } catch (BadLocationException | IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return "";
    }

    /**
     * Only works if the EditorKit of the EditorPane is a subclass of
     * HTMLEditorKit
     *
     * @param id Id of an HTML tag.
     * @throws IllegalArgumentException if EditorKit is not a sublass of
     * HTMLEditorkit
     */
    public void scrollToId(String id) throws IllegalArgumentException {
        if (!(getEditorKit() instanceof HTMLEditorKit)) {
            throw new IllegalArgumentException("EditorKit must be a subclass of HTMLEditorkit ");
        }
        Document d = getDocument();
        if (d instanceof HTMLDocument) {
            HTMLDocument doc = (HTMLDocument) d;
            Element iter = doc.getElement(id);
            // found a matching reference in the document.
            try {
                int pos = iter.getStartOffset();
                Rectangle r = modelToView(pos);
                if (r != null) {
                    // the view is visible, scroll it to the
                    // center of the current visible area.
                    Rectangle vis = getVisibleRect();
                    //r.y -= (vis.height / 2);
                    r.height = vis.height;
                    scrollRectToVisible(r);
                    setCaretPosition(pos);
                }
            } catch (BadLocationException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
    }

}
