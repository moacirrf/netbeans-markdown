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

import javax.swing.UIManager;
import javax.swing.text.html.StyleSheet;

public final class ThemeResolver {

    public static final String DEFAULT_CSS = "common.css";
    public static final String LIGHT_CSS = "light.css";
    public static final String DARK_CSS = "dark.css";
    public static final String GENERIC_CSS = "generic.css";

    public StyleSheet resolve() {
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.importStyleSheet(this.getClass().getResource(DEFAULT_CSS));
        String theme = UIManager.getLookAndFeel().getName().toLowerCase();

        if (theme.contains("dark")) {
            styleSheet.importStyleSheet(this.getClass().getResource(DARK_CSS));
        } else if (theme.contains("flatlaf light")) {
            styleSheet.importStyleSheet(this.getClass().getResource(LIGHT_CSS));
        } else {
            styleSheet.importStyleSheet(this.getClass().getResource(GENERIC_CSS));
        }

        return styleSheet;
    }
}
