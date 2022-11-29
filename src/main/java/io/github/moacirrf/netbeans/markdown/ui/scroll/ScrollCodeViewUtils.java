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
import static io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils.isScrolledToMaximum;
import static io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils.setScrollToMaximum;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ScrollCodeViewUtils {

    private static String writeTofile(String doc) {
        try {
            Files.writeString(Path.of("/tmp/teste.html"), doc, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return doc;
    }

    public static void syncronizeScrolls(JEditorPane leftEditor, JEditorPane rightEditor) {
        JScrollPane scrollPane = ScrollUtils.getScrollPaneOf(rightEditor);
        var text = JEditorPaneImpl.getVisibleText(rightEditor, scrollPane);
        writeTofile(text);
        Document document = Jsoup.parse(text);
        String id = document.getElementsByTag("body").get(0).child(0).attr("id");
        if (StringUtils.isNotBlank(id)) {
            try {
                int pos = Integer.parseInt(id.split("-")[0]);
                Rectangle positionToScrol = leftEditor.modelToView(pos);
                Rectangle actualPosition = leftEditor.getVisibleRect();
                positionToScrol.height = actualPosition.height;
//                leftEditor.setCaretPosition(pos);
                leftEditor.scrollRectToVisible(positionToScrol);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private ScrollCodeViewUtils() {
    }
}
