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
package io.github.moacirrf.netbeans.markdown;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.apache.commons.text.StringSubstitutor;
import static org.openide.util.ImageUtilities.loadImageIcon;

/**
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class Icons {

    private static final String PATH = "io/github/moacirrf/netbeans/markdown/";

    public static ImageIcon getICON_PREVIEW() {
        return loadImageIcon(PATH + "icon_preview.png", true);
    }

    public static ImageIcon getICON_SOURCE() {
        return loadImageIcon(PATH + "icon_source.png", true);
    }

    public static ImageIcon getICON_VERTICAL_SPLIT() {
        return loadImageIcon(PATH + "icon_vertical_split.png", true);
    }

    public static ImageIcon getICON_HORIZONTAL_SPLIT() {
        return loadImageIcon(PATH + "icon_horizontal_split.png", true);
    }

    public static ImageIcon getICON_COMPLETION() {
        return loadImageIcon(PATH + "code_template.png", false);
    }

    public static ImageIcon getICON_EXPORT_DOWN() {
        if (UIManager.getBoolean("nb.dark.theme")) {
            return loadImageIcon(PATH + "down_dark.png", false);
        }
        return loadImageIcon(PATH + "down_light.png", false);
    }

    public static ImageIcon getICON_EXPORT_UP() {
        if (UIManager.getBoolean("nb.dark.theme")) {
            return loadImageIcon(PATH + "up_dark.png", false);
        }
        return loadImageIcon(PATH + "up_light.png", false);
    }

    private Icons() {
    }
}
