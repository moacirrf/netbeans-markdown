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
import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    private static boolean isScrolledToMaximum(JScrollPane scrollPane) {
        var viewPort = scrollPane.getViewport();
        return (viewPort.getViewSize().height - viewPort.getExtentSize().getHeight()) == viewPort.getViewPosition().y;
    }

    private static void setScrollToMaximum(JScrollPane scrollPane) {
        var viewPort = scrollPane.getViewport();
        var viewPosition = viewPort.getViewPosition();
        var viewSize = viewPort.getViewSize();

        viewPosition.y = viewSize.height;
        scrollPane.getViewport().setViewPosition(viewPosition);
    }

    private static String writeTofile(String doc) {
        try {
            Files.writeString(Path.of("/tmp/teste.html"), doc, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return doc;
    }

    public static void syncronizeScrolls(JEditorPane leftEditorPane, JScrollPane leftJScrollPane, JEditorPane rightEditor, JScrollPane rightJScrollPane) {
        writeTofile(rightEditor.getText());
        if (isScrolledToMaximum(leftJScrollPane)) {
            setScrollToMaximum(rightJScrollPane);
        } else {
            try {
                var leftTextVisible = JEditorPaneImpl.getVisibleText(leftEditorPane, leftJScrollPane);
                if (leftTextVisible.length() > 0) {
                    scrollPreview(leftEditorPane.getText(), leftTextVisible, (JEditorPaneImpl) rightEditor);
                } else {
                    rightJScrollPane.getViewport().setViewPosition(new Point(leftJScrollPane.getViewport().getViewPosition()));
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static void scrollPreview(String completeText, String visibleText, JEditorPaneImpl rightEdit) throws BadLocationException {
        var list = ScrollableModel.from(Jsoup.parse(rightEdit.getText()), completeText);
        Collections.sort(list);
        visibleText = visibleText.trim();
        for (ScrollableModel item : list) {
            int numberChars = item.getMdEnd() - item.getMdBegin();
            if (visibleText.substring(0, numberChars).contains(item.getMdText())) {
                rightEdit.scrollToId(item.id());
            }
        }
    }

    private ScrollPreviewUtils() {
    }
}
