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

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.StyleSheet;
import org.apache.commons.text.StringSubstitutor;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.FontColorNames;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.openide.util.Exceptions;

public final class ThemeResolver {

    public static final String STYLE_CSS = "/io/github/moacirrf/netbeans/markdown/ui/preview/style.css.template";

    private FontColorSettings fontColorSettings;

    public StyleSheet resolve() {
        fontColorSettings = (FontColorSettings) MimeLookup.getLookup(MimePath.parse("text/x-java"))
                .lookup(FontColorSettings.class);
        StyleSheet styleSheet = new StyleSheet();
        try {
            String style = new String(getClass().getResourceAsStream(STYLE_CSS).readAllBytes());
            if (UIManager.getBoolean("nb.dark.theme")) {
                style = StringSubstitutor.replace(style, getDarkStyle());
                styleSheet.addRule(style);
            } else {
                style = StringSubstitutor.replace(style, getLightStyle());
                styleSheet.addRule(style);
            }
            return styleSheet;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return styleSheet;
    }

    private Map<String, Object> getLightStyle() {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("blockquote.bgColor", "#F5F7F9");
        maps.put("blockquote.borderLeftColor", "#a1c1dd");
        maps.put("removeColorLinkWithImage.color", "#FFFFFF");
        maps.put("pre.bgColor", "#F5F7F9");
        maps.put("code.bgColor", "#F5F7F9");
        maps.put("tdTh.borderColor", "#515151");
        setFontFamilySize(maps);
        return maps;
    }

    private Map<String, Object> getDarkStyle() {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("blockquote.bgColor", "#3C3D3E");
        maps.put("blockquote.borderLeftColor", "#476387");
        maps.put("removeColorLinkWithImage.color", "#2B2B2B");
        maps.put("pre.bgColor", "#3C3D3E");
        maps.put("code.bgColor", "#3C3D3E");
        maps.put("tdTh.borderColor", "#515151");
        setFontFamilySize(maps);
        return maps;
    }

    /**
     * See configuration of fontSize, fontFamily, fontColor, background from
     * Preferences\Font &Colors
     *
     *
     * @param maps
     */
    private void setFontFamilySize(HashMap<String, Object> maps) {

        var att = fontColorSettings.getFontColors(FontColorNames.DEFAULT_COLORING);

        maps.put("fontFamily", att.getAttribute(StyleConstants.FontConstants.FontFamily));
        maps.put("fontSize", att.getAttribute(StyleConstants.FontConstants.FontSize));

        Color fontColor = (Color) att.getAttribute(StyleConstants.FontConstants.Foreground);
        Color bgColor = (Color) att.getAttribute(StyleConstants.FontConstants.Background);
        maps.put("body.color", toRGB(fontColor));
        maps.put("body.bgColor", toRGB(bgColor));
    }

    private String toRGB(Color color) {
        return String.format("rgb(%s,%s,%s)", color.getRed(), color.getGreen(), color.getBlue());
    }
}
