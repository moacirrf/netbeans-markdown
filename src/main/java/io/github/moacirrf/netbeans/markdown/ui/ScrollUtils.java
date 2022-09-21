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
package io.github.moacirrf.netbeans.markdown.ui;

import io.github.moacirrf.netbeans.markdown.html.HtmlBuilder;
import io.github.moacirrf.netbeans.markdown.ui.preview.MarkdownPreviewScrollPane;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ScrollUtils {

    public static boolean isScrolledToMaximum(JScrollPane scrollPane) {
        var viewPort = scrollPane.getViewport();
        return (viewPort.getViewSize().height - viewPort.getExtentSize().getHeight()) == viewPort.getViewPosition().y;
    }

    public static void setScrollToMaximum(JScrollPane scrollPane) {
        var viewPort = scrollPane.getViewport();
        var viewPosition = viewPort.getViewPosition();
        var viewSize = viewPort.getViewSize();

        viewPosition.y = viewSize.height;
        scrollPane.getViewport().setViewPosition(viewPosition);
    }

    public static void syncronizeScrolls(JEditorPane leftEditorPane, JScrollPane leftJScrollPane, MarkdownPreviewScrollPane rightJScrollPane) {
        try {
            var begin = leftEditorPane.viewToModel2D(new Point(0, leftJScrollPane.getViewport().getViewPosition().y));
            if (begin > 0) {
                var texto = leftEditorPane.getText(begin, leftEditorPane.getText().length() - begin);
                var html = HtmlBuilder.getInstance()
                        .build(texto.lines().filter(p -> p != null && !p.trim().isBlank())
                                .findFirst().orElse(""));

                var index = getOffSetDocument(html, (HTMLDocument) rightJScrollPane.getEditorPane().getDocument());
                if (StringUtils.isNoneBlank(html)) {
                    if (index > 0) {
                        Rectangle rec = rightJScrollPane.getEditorPane().modelToView(index);
                        Rectangle vis = rightJScrollPane.getVisibleRect();
                        rec.height = vis.height;
                        rightJScrollPane.scrollRectToVisible(rec);
                    }
                }
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void syncronizeScrolls2(JScrollPane leftJScrollPane, MarkdownPreviewScrollPane rightJScrollPane) {
        var rightViewPort = rightJScrollPane.getViewport();
        var leftViewPort = leftJScrollPane.getViewport();
        rightViewPort.setViewPosition((Point) leftViewPort.getViewPosition().clone());

        if (ScrollUtils.isScrolledToMaximum(leftJScrollPane) && leftJScrollPane.getVerticalScrollBar().isVisible()) {
            ScrollUtils.setScrollToMaximum(rightJScrollPane);
        }
    }

    private static int getOffSetDocument(String text, HTMLDocument document) {
        var innerText = Jsoup.parse(text).getElementsByTag("body").text();
        Element element = document.getDefaultRootElement();
        int counts = element.getElementCount();
        for (int index = 0; index <= counts; index++) {
            if (element.getElement(index) != null) {
                try {
                    HTMLDocument doc = (HTMLDocument) element.getElement(index).getDocument();
                    if (doc != null && doc.getLength() > 0) {
                        if (doc.getText(0, doc.getLength()).equals(innerText)) {
                            return element.getStartOffset();
                        }
                        return getOffSetDocument(text, doc);
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return -1;
    }

    private ScrollUtils() {
    }
}
