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
package io.github.moacirrf.netbeans.markdown.ui.scroll;

import io.github.moacirrf.netbeans.markdown.ui.preview.JEditorPaneImpl;
import static io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils.getScrollPaneOf;
import static io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils.isScrolledToMaximum;
import static io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils.setScrollToMaximum;
import java.awt.Point;
import java.util.Collections;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import org.jsoup.Jsoup;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ScrollPreviewUtils {

    public static void syncronizeScrolls(JEditorPane leftEditorPane, JEditorPane rightEditor) {
        JScrollPane leftJScrollPane = getScrollPaneOf(leftEditorPane);
        JScrollPane rightJScrollPane = getScrollPaneOf(rightEditor);
        if (leftJScrollPane != null) {
            if (isScrolledToMaximum(leftJScrollPane)) {
                setScrollToMaximum(rightJScrollPane);
            } else {
                try {
                    var leftTextVisible = JEditorPaneImpl.getVisibleText(leftEditorPane, leftJScrollPane);
                    if (leftTextVisible.length() > 0) {
                        scrollByTextContent(leftEditorPane.getText(), leftTextVisible, (JEditorPaneImpl) rightEditor);
                    } else {
                        rightJScrollPane.getViewport().setViewPosition(new Point(leftJScrollPane.getViewport().getViewPosition()));
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private static void scrollByTextContent(String completeText, String visibleText, JEditorPaneImpl rightEdit) throws BadLocationException {
        var list = ScrollableModel.from(Jsoup.parse(rightEdit.getText()), completeText);
        Collections.sort(list);
        visibleText = visibleText.trim();
        try {
            for (ScrollableModel item : list) {
                int numberChars = item.getMdEnd() - item.getMdBegin();
                if (visibleText.length() < numberChars) {
                    numberChars = visibleText.length() - 1;
                }
                var subText = visibleText.substring(0, numberChars);
                if (subText != null && item.getMdText() != null && subText.trim().contains(item.getMdText().trim())) {
                    rightEdit.scrollToId(item.id());
                }
            }
        } catch (StringIndexOutOfBoundsException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private ScrollPreviewUtils() {
    }
}
