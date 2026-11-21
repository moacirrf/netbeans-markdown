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
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import static javax.swing.event.HyperlinkEvent.EventType.ENTERED;
import static javax.swing.event.HyperlinkEvent.EventType.EXITED;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
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

    private final transient CodeBlockCopyController codeBlockCopyController;
    private final transient Consumer<String> clipboardWriter;
    private JPopupMenu contextMenu;
    private JMenuItem copyMenuItem;
    private JMenuItem copyLinkMenuItem;
    private JMenuItem selectAllMenuItem;
    private String pendingLinkUrl;

    public JEditorPaneImpl() {
        this(JEditorPaneImpl::copyToSystemClipboard);
    }

    JEditorPaneImpl(Consumer<String> clipboardWriter) {
        this.clipboardWriter = clipboardWriter;
        this.codeBlockCopyController = new CodeBlockCopyController(this);
        initCopyAndContextMenu();
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

    private void initCopyAndContextMenu() {
        int menuMask = getMenuShortcutMask();
        KeyStroke copyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, menuMask);
        KeyStroke selectAllStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A, menuMask);

        getActionMap().put(DefaultEditorKit.copyAction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copy();
            }
        });
        getActionMap().put(DefaultEditorKit.selectAllAction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }
        });

        var inputMap = getInputMap(WHEN_FOCUSED);
        inputMap.put(copyStroke, DefaultEditorKit.copyAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK), DefaultEditorKit.copyAction);
        inputMap.put(selectAllStroke, DefaultEditorKit.selectAllAction);

        copyMenuItem = new JMenuItem(new AbstractAction("Copy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                copy();
            }
        });
        copyMenuItem.setAccelerator(copyStroke);

        copyLinkMenuItem = new JMenuItem(new AbstractAction("Copy Link") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pendingLinkUrl != null && !pendingLinkUrl.isEmpty()) {
                    clipboardWriter.accept(pendingLinkUrl);
                }
            }
        });
        copyLinkMenuItem.setVisible(false);

        selectAllMenuItem = new JMenuItem(new AbstractAction("Select All") {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }
        });
        selectAllMenuItem.setAccelerator(selectAllStroke);

        contextMenu = new JPopupMenu();
        contextMenu.add(copyMenuItem);
        contextMenu.add(copyLinkMenuItem);
        contextMenu.add(selectAllMenuItem);
    }

    @Override
    public void copy() {
        String text = getCleanSelectedText();
        if (text != null && !text.isEmpty()) {
            clipboardWriter.accept(text);
        }
    }

    String getCleanSelectedText() {
        String selected = getSelectedText();
        if (selected == null || selected.isEmpty()) {
            return selected;
        }
        // Swing HTMLEditorKit includes a synthetic leading newline from <head> when selecting from offset 0.
        if (getSelectionStart() == 0 && selected.startsWith("\n")) {
            return selected.substring(1);
        }
        return selected;
    }

    boolean hasSelectedText() {
        return getSelectionEnd() > getSelectionStart();
    }

    JPopupMenu getContextMenu() {
        return getContextMenu(null);
    }

    JPopupMenu getContextMenu(Point point) {
        updateContextMenuState(point);
        return contextMenu;
    }

    private void updateContextMenuState(Point point) {
        if (copyMenuItem != null) {
            copyMenuItem.setEnabled(hasSelectedText());
        }
        pendingLinkUrl = (point != null) ? findLinkAt(point) : null;
        if (copyLinkMenuItem != null) {
            copyLinkMenuItem.setVisible(pendingLinkUrl != null && !pendingLinkUrl.isEmpty());
        }
        if (selectAllMenuItem != null && getDocument() != null) {
            selectAllMenuItem.setEnabled(getDocument().getLength() > 0);
        }
    }

    String findLinkAt(Point point) {
        if (point == null || !(getDocument() instanceof HTMLDocument)) {
            return null;
        }
        HTMLDocument doc = (HTMLDocument) getDocument();
        javax.swing.text.Position.Bias[] biasRet = new javax.swing.text.Position.Bias[1];
        int pos = getUI().viewToModel2D(this, point, biasRet);
        if (pos < 0) {
            return null;
        }
        try {
            var charRect = modelToView2D(pos);
            if (charRect != null && (point.y < charRect.getMinY() || point.y > charRect.getMaxY()
                    || point.x > charRect.getMaxX() + 6)) {
                return null;
            }
        } catch (BadLocationException ex) {
            return null;
        }
        if (biasRet[0] == javax.swing.text.Position.Bias.Backward && pos > 0) {
            pos--;
        }
        Element elem = doc.getCharacterElement(pos);
        if (elem == null || elem.getAttributes() == null) {
            return null;
        }
        Object linkAttrs = elem.getAttributes().getAttribute(HTML.Tag.A);
        if (linkAttrs instanceof javax.swing.text.AttributeSet) {
            Object href = ((javax.swing.text.AttributeSet) linkAttrs).getAttribute(HTML.Attribute.HREF);
            if (href != null && StringUtils.isNotBlank(href.toString())) {
                return href.toString();
            }
        }
        return null;
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            requestFocusInWindow();
            updateContextMenuState(e.getPoint());
            if (contextMenu != null) {
                contextMenu.show(this, e.getX(), e.getY());
            }
            e.consume();
            return;
        }
        if (codeBlockCopyController != null && codeBlockCopyController.handleMouseEvent(e)) {
            return;
        }
        super.processMouseEvent(e);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        super.processMouseMotionEvent(e);
        if (codeBlockCopyController != null) {
            codeBlockCopyController.handleMouseMotionEvent(e);
        }
    }

    private static int getMenuShortcutMask() {
        try {
            return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        } catch (HeadlessException ex) {
            return InputEvent.CTRL_DOWN_MASK;
        }
    }

    private static void copyToSystemClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
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
