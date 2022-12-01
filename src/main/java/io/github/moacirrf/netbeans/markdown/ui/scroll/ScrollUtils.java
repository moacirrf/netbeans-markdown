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

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

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
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public static JScrollPane getScrollPaneOf(JEditorPane editorPane) {
        return (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, editorPane);
    }

    private ScrollUtils() {
    }

}
