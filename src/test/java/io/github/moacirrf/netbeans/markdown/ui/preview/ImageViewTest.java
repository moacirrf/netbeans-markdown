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

import javax.swing.JEditorPane;
import javax.swing.SwingConstants;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ImageViewTest {

    private static JEditorPane createPane(String bodyHtml, StyleSheet ss) {
        HTMLEditorKit kit = new HTMLEditorKit() {
            @Override
            public StyleSheet getStyleSheet() {
                return ss;
            }
        };
        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(kit);
        pane.setText("<html><body>" + bodyHtml + "</body></html>");
        return pane;
    }

    private static Element findFirstImg(Element root) {
        if (HTML.Tag.IMG.toString().equals(root.getName())) {
            return root;
        }
        for (int i = 0; i < root.getElementCount(); i++) {
            Element found = findFirstImg(root.getElement(i));
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Test
    public void testStandaloneBlockImagesResolveParagraphBottomMargin() {
        StyleSheet ss = new StyleSheet();
        ss.addRule("p { margin-top: 0px; margin-bottom: 10px; }");

        String[] standaloneCases = {
            "<p>Before</p><img src=\"a.png\" width=\"500\" /><p>After</p>",
            "<p>Before</p><a href=\"https://example.com\"><img src=\"a.png\" /></a><p>After</p>",
            "<blockquote><img src=\"a.png\" /><p>After</p></blockquote>",
            "<div align=\"center\"><img src=\"a.png\" /></div><p>After</p>",
            "<p>Before</p><img src=\"a.png\" /> <img src=\"b.png\" /><p>After</p>"
        };

        for (String html : standaloneCases) {
            JEditorPane pane = createPane(html, ss);
            Element img = findFirstImg(pane.getDocument().getDefaultRootElement());
            assertNotNull(html, img);
            assertTrue("Expected standalone block image: " + html, ImageView.isStandaloneBlockImage(img));

            ImageView view = new ImageView(img, pane);
            int expected = Math.round(ss.getBoxPainter(ss.getRule("p")).getInset(SwingConstants.BOTTOM, view));
            assertTrue(expected > 0);
            assertEquals(html, expected, view.resolveBlockBottomMargin());
        }
    }

    @Test
    public void testParagraphAndInlineImagesDoNotAddExtraMargin() {
        StyleSheet ss = new StyleSheet();
        ss.addRule("p { margin-top: 0px; margin-bottom: 10px; }");

        String[] nonStandaloneCases = {
            "<p><img src=\"a.png\" /></p><p>After</p>",
            "<p>Inline <img src=\"badge.png\" /> image</p>",
            "<table><tr><td><img src=\"icon.png\" /></td></tr></table>",
            "<ul><li><img src=\"icon.png\" /></li></ul>",
            "Text <img src=\"icon.png\" /> more text"
        };

        for (String html : nonStandaloneCases) {
            JEditorPane pane = createPane(html, ss);
            Element img = findFirstImg(pane.getDocument().getDefaultRootElement());
            assertNotNull(html, img);
            assertFalse("Did not expect standalone block image: " + html, ImageView.isStandaloneBlockImage(img));
            assertEquals(html, 0, new ImageView(img, pane).resolveBlockBottomMargin());
        }
    }
}
