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

import javax.swing.text.ViewFactory;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class MarkdownEditorKit extends HTMLEditorKit {

    private final StyleSheet styleSheet = new ThemeResolver().resolve();
    private final ViewFactory viewFactory = new LocalViewFactory();

    @Override
    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    @Override
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
