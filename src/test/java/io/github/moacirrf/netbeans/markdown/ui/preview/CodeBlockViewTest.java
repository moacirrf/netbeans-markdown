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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JEditorPane;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CodeBlockViewTest {

    private static JEditorPane createRenderedPane(String bodyHtml, String preBgColor) {
        StyleSheet ss = new StyleSheet();
        ss.addRule("body { margin: 0px; padding: 0px 15px 15px 15px; font-family: Monospaced; font-size: 13; }"
                + "p { margin-top: 0px; margin-bottom: 10px; }"
                + "pre { margin-top: 0px; margin-bottom: 10px; padding: 10px 44px 10px 15px; background-color: " + preBgColor + "; }"
                + "pre code { background-color: " + preBgColor + "; }"
                + "li pre { margin-top: 6px; margin-bottom: 6px; }");

        JEditorPane pane = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit() {
            @Override
            public StyleSheet getStyleSheet() {
                return ss;
            }

            @Override
            public ViewFactory getViewFactory() {
                return new LocalViewFactory(pane);
            }
        };
        pane.setEditable(false);
        pane.setEditorKit(kit);
        pane.setText("<html><body>" + bodyHtml + "</body></html>");
        pane.setSize(520, Short.MAX_VALUE);
        Dimension pref = pane.getPreferredSize();
        int height = Math.max(100, pref.height);
        pane.setSize(520, height);

        BufferedImage img = new BufferedImage(520, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        pane.paint(g);
        g.dispose();
        return pane;
    }

    private static List<CodeBlockView> findCodeBlockViews(JEditorPane pane) {
        List<CodeBlockView> result = new ArrayList<>();
        collect(pane.getUI().getRootView(pane), result);
        return result;
    }

    private static void collect(View v, List<CodeBlockView> out) {
        if (v instanceof CodeBlockView) {
            out.add((CodeBlockView) v);
            return;
        }
        for (int i = 0; i < v.getViewCount(); i++) {
            collect(v.getView(i), out);
        }
    }

    @Test
    public void testExtractCodeTextPreservesFormattingAndTrimsTrailingNewline() {
        String html = "<pre><code>mvn clean install\n</code></pre>"
                + "<pre><code>public class Demo {\n    List&lt;String&gt; items = a &amp;&amp; b;\n\n    int n = 1;\n}\n</code></pre>"
                + "<ul><li>Step<pre><code>podman --version\n</code></pre></li></ul>";

        JEditorPane pane = createRenderedPane(html, "#3C3D3E");
        List<CodeBlockView> views = findCodeBlockViews(pane);
        assertEquals(3, views.size());

        assertEquals("mvn clean install", views.get(0).extractCodeText());
        assertEquals("public class Demo {\n    List<String> items = a && b;\n\n    int n = 1;\n}",
                views.get(1).extractCodeText());
        assertEquals("podman --version", views.get(2).extractCodeText());
    }

    @Test
    public void testButtonBoundsSingleLineMultiLineAndHorizontalClamp() {
        Rectangle singleCard = new Rectangle(15, 20, 480, 44);
        Rectangle singleBtn = CodeBlockView.computeButtonBounds(singleCard, new Rectangle(0, 0, 520, 400));
        assertNotNull(singleBtn);
        assertEquals(15 + 480 - CodeBlockView.BUTTON_SIZE - CodeBlockView.BUTTON_INSET, singleBtn.x);
        assertEquals(20 + (44 - CodeBlockView.BUTTON_SIZE) / 2, singleBtn.y);

        Rectangle multiCard = new Rectangle(15, 80, 480, 120);
        Rectangle multiBtn = CodeBlockView.computeButtonBounds(multiCard, new Rectangle(0, 0, 520, 400));
        assertNotNull(multiBtn);
        assertEquals(80 + CodeBlockView.BUTTON_INSET, multiBtn.y);

        Rectangle wideCard = new Rectangle(15, 80, 1200, 120);
        Rectangle narrowViewport = new Rectangle(0, 0, 500, 400);
        Rectangle clampedBtn = CodeBlockView.computeButtonBounds(wideCard, narrowViewport);
        assertNotNull(clampedBtn);
        assertEquals(500 - CodeBlockView.BUTTON_SIZE - CodeBlockView.BUTTON_INSET, clampedBtn.x);
    }

    @Test
    public void testHoverClickCopyAndNativeSelectionPassthrough() {
        JEditorPane pane = createRenderedPane("<pre><code>mvn clean install\n</code></pre>", "#3C3D3E");
        List<CodeBlockView> views = findCodeBlockViews(pane);
        assertEquals(1, views.size());
        CodeBlockView view = views.get(0);

        Rectangle card = view.getLastCardBounds();
        Rectangle btn = view.getButtonBounds(pane.getVisibleRect());
        assertNotNull(card);
        assertNotNull(btn);

        AtomicReference<String> copied = new AtomicReference<>();
        CodeBlockCopyController controller = new CodeBlockCopyController(pane, copied::set);

        // Hover over code text (inside card, outside button)
        MouseEvent moveOnText = new MouseEvent(pane, MouseEvent.MOUSE_MOVED, 0, 0,
                card.x + 20, card.y + card.height / 2, 0, false);
        assertFalse(controller.handleMouseMotionEvent(moveOnText));
        assertTrue(view.isCardHovered());
        assertFalse(view.isButtonHovered());
        assertEquals(1.0f, view.getTargetAlpha(), 0.001f);

        // Click on code text must NOT be consumed (preserves native selection)
        MouseEvent pressOnText = new MouseEvent(pane, MouseEvent.MOUSE_PRESSED, 0, 0,
                card.x + 20, card.y + card.height / 2, 1, false, MouseEvent.BUTTON1);
        assertFalse(controller.handleMouseEvent(pressOnText));
        assertFalse(pressOnText.isConsumed());
        assertNull(copied.get());

        // Hover over copy button
        MouseEvent moveOnBtn = new MouseEvent(pane, MouseEvent.MOUSE_MOVED, 0, 0,
                btn.x + btn.width / 2, btn.y + btn.height / 2, 0, false);
        assertTrue(controller.handleMouseMotionEvent(moveOnBtn));
        assertTrue(view.isButtonHovered());
        assertEquals(Cursor.HAND_CURSOR, pane.getCursor().getType());
        assertEquals("Copy", pane.getToolTipText());

        // Click copy button
        MouseEvent pressOnBtn = new MouseEvent(pane, MouseEvent.MOUSE_PRESSED, 0, 0,
                btn.x + btn.width / 2, btn.y + btn.height / 2, 1, false, MouseEvent.BUTTON1);
        assertTrue(controller.handleMouseEvent(pressOnBtn));
        assertTrue(pressOnBtn.isConsumed());
        assertEquals("mvn clean install", copied.get());
        assertTrue(view.isCopied());
        assertEquals(1.0f, view.getButtonAlpha(), 0.001f);
        assertEquals("Copied!", pane.getToolTipText());

        // Mouse exit clears hover/cursor/tooltip while copied keeps button visible
        MouseEvent exit = new MouseEvent(pane, MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, 0, false);
        assertFalse(controller.handleMouseEvent(exit));
        assertFalse(view.isCardHovered());
        assertFalse(view.isButtonHovered());
        assertEquals(1.0f, view.getTargetAlpha(), 0.001f);
        assertNull(pane.getToolTipText());

        controller.reset();
    }

    @Test
    public void testPaintsLightAndDarkStatesWithoutError() {
        for (String bg : new String[]{"#3C3D3E", "#F5F7F9"}) {
            JEditorPane pane = createRenderedPane("<pre><code>echo ok\n</code></pre>", bg);
            CodeBlockView view = findCodeBlockViews(pane).get(0);
            BufferedImage img = new BufferedImage(pane.getWidth(), pane.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();

            view.setButtonAlpha(1.0f);
            view.setButtonHovered(false);
            pane.paint(g);

            view.setButtonHovered(true);
            pane.paint(g);

            view.setCopied(true);
            pane.paint(g);
            g.dispose();
        }
    }
}
