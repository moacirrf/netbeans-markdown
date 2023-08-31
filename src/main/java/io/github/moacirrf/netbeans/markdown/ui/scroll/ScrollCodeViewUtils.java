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

import io.github.moacirrf.netbeans.markdown.Context;
import io.github.moacirrf.netbeans.markdown.ui.preview.JEditorPaneImpl;
import java.awt.Rectangle;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ScrollCodeViewUtils {

    public static void syncronizeScrolls(JEditorPane leftEditor, JEditorPane rightEditor) {
        if (Context.SCROLL_SYNC) {
            JScrollPane scrollPane = ScrollUtils.getScrollPaneOf(rightEditor);
            var text = JEditorPaneImpl.getVisibleText(rightEditor, scrollPane);
            Document document = Jsoup.parse(text);
            if ((!document.getElementsByTag("body").isEmpty()) && document.getElementsByTag("body").get(0).childNodeSize() > 0) {
                Element childElement = document.getElementsByTag("body").get(0).child(0);
                String id = findIdWithText(childElement);

                if (StringUtils.isBlank(id) && childElement.childNodeSize() > 0) { //search a child with id
                    id = childElement.child(0).attr("id");
                }
                if (StringUtils.isNotBlank(id)) {
                    try {
                        int pos = Integer.parseInt(id.split("-")[0]);
                        Rectangle positionToScrol = leftEditor.modelToView(pos);
                        Rectangle actualPosition = leftEditor.getVisibleRect();
                        positionToScrol.height = actualPosition.height;
                        leftEditor.scrollRectToVisible(positionToScrol);
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    private static String findIdWithText(Element element) {
        var id = element.attr("id");
        if (StringUtils.isBlank(id) || element.childrenSize() > 0 || StringUtils.isBlank(element.val())) {
            for (Element el : element.children()) {
                return findIdWithText(el);
            }
        }
        return id;
    }

    private ScrollCodeViewUtils() {
    }
}
