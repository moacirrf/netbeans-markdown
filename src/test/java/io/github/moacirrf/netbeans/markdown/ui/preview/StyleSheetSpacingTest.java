/*
 * Copyright (C) 2026 Moacir da Roza Flores <moacirrf@gmail.com>
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

import java.io.IOException;
import java.util.HashMap;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.apache.commons.text.StringSubstitutor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Verifies that style.css.template parses cleanly in Swing's StyleSheet and
 * resolves consistent unidirectional vertical margins for block elements.
 */
public class StyleSheetSpacingTest {

    private StyleSheet loadStyleSheet() throws IOException {
        return loadStyleSheet(13);
    }

    private StyleSheet loadStyleSheet(int fontSize) throws IOException {
        try (var in = ThemeResolver.class.getResourceAsStream(ThemeResolver.STYLE_CSS)) {
            assertNotNull("style.css.template not found", in);
            var maps = new HashMap<String, Object>();
            maps.put("body.color", "rgb(0,0,0)");
            maps.put("body.bgColor", "rgb(255,255,255)");
            maps.put("fontFamily", "SansSerif");
            maps.put("fontSize", fontSize);
            ThemeResolver.putHeadingFontSizes(maps, fontSize);
            maps.put("blockquote.bgColor", "#F5F7F9");
            maps.put("blockquote.borderLeftColor", "#a1c1dd");
            maps.put("removeColorLinkWithImage.color", "#FFFFFF");
            maps.put("pre.bgColor", "#F5F7F9");
            maps.put("code.bgColor", "#F5F7F9");
            maps.put("tdTh.borderColor", "#515151");

            String css = StringSubstitutor.replace(new String(in.readAllBytes()), maps);
            StyleSheet ss = new StyleSheet();
            ss.addRule(css);
            return ss;
        }
    }

    private static String attr(AttributeSet rule, CSS.Attribute key) {
        Object val = rule.getAttribute(key);
        return val == null ? null : val.toString();
    }

    @Test
    public void testUnidirectionalBlockMargins() throws IOException {
        StyleSheet ss = loadStyleSheet();

        for (String tag : new String[]{"p", "pre", "blockquote", "table", "dl"}) {
            AttributeSet rule = ss.getRule(tag);
            assertEquals(tag + " margin-top", "0px", attr(rule, CSS.Attribute.MARGIN_TOP));
            assertEquals(tag + " margin-bottom", "10px", attr(rule, CSS.Attribute.MARGIN_BOTTOM));
        }

        for (String listTag : new String[]{"ul", "ol"}) {
            AttributeSet rule = ss.getRule(listTag);
            assertEquals(listTag + " margin-top", "0px", attr(rule, CSS.Attribute.MARGIN_TOP));
            assertEquals(listTag + " margin-bottom", "7px", attr(rule, CSS.Attribute.MARGIN_BOTTOM));
        }

        AttributeSet li = ss.getRule("li");
        assertEquals("0px", attr(li, CSS.Attribute.MARGIN_TOP));
        assertEquals("3px", attr(li, CSS.Attribute.MARGIN_BOTTOM));
        assertEquals("0px", attr(li, CSS.Attribute.MARGIN_LEFT));
        assertEquals("0px", attr(li, CSS.Attribute.MARGIN_RIGHT));
    }

    @Test
    public void testHeadingProximityMargins() throws IOException {
        StyleSheet ss = loadStyleSheet();

        for (String h : new String[]{"h1", "h2"}) {
            AttributeSet rule = ss.getRule(h);
            assertEquals(h + " margin-top", "10px", attr(rule, CSS.Attribute.MARGIN_TOP));
            assertEquals(h + " margin-bottom", "6px", attr(rule, CSS.Attribute.MARGIN_BOTTOM));
        }
        for (String h : new String[]{"h3", "h4", "h5", "h6"}) {
            AttributeSet rule = ss.getRule(h);
            assertEquals(h + " margin-top", "8px", attr(rule, CSS.Attribute.MARGIN_TOP));
            assertEquals(h + " margin-bottom", "5px", attr(rule, CSS.Attribute.MARGIN_BOTTOM));
        }
    }

    @Test
    public void testRelativeHeadingFontSizes() throws IOException {
        for (int baseSize : new int[]{13, 18}) {
            StyleSheet ss = loadStyleSheet(baseSize);
            HTMLEditorKit kit = new HTMLEditorKit() {
                @Override
                public StyleSheet getStyleSheet() {
                    return ss;
                }
            };
            JEditorPane pane = new JEditorPane();
            pane.setEditorKit(kit);
            pane.setText("<html><body>"
                    + "<p>P</p>"
                    + "<h1>H1</h1><h2>H2</h2><h3>H3</h3><h4>H4</h4><h5>H5</h5><h6>H6</h6>"
                    + "</body></html>");
            pane.setSize(400, 1000);
            pane.getPreferredSize();

            View root = pane.getUI().getRootView(pane);
            int p = fontSizeOfTag(root, ss, "p");
            int h1 = fontSizeOfTag(root, ss, "h1");
            int h2 = fontSizeOfTag(root, ss, "h2");
            int h3 = fontSizeOfTag(root, ss, "h3");
            int h4 = fontSizeOfTag(root, ss, "h4");
            int h5 = fontSizeOfTag(root, ss, "h5");
            int h6 = fontSizeOfTag(root, ss, "h6");

            assertEquals("base=" + baseSize, baseSize, p);
            assertTrue(h1 > h2 && h2 > h3 && h3 > h4 && h4 > h5 && h5 > h6);
            assertEquals("h5 equals p", p, h5);
        }
    }

    private static int fontSizeOfTag(View root, StyleSheet ss, String tag) {
        View v = findViewByTag(root, tag);
        assertNotNull("Tag not found: " + tag, v);
        return ss.getFont(v.getAttributes()).getSize();
    }

    private static View findViewByTag(View v, String tag) {
        if (tag.equals(v.getElement().getName())) {
            return v;
        }
        for (int i = 0; i < v.getViewCount(); i++) {
            View found = findViewByTag(v.getView(i), tag);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
