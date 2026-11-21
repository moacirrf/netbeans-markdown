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

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class JEditorPaneCopyTest {

    private static JEditorPaneImpl createPane(String bodyHtml, AtomicReference<String> clipboard) {
        JEditorPaneImpl pane = new JEditorPaneImpl(clipboard::set);
        pane.setEditable(false);
        pane.setEditorKit(new HTMLEditorKit());
        pane.setText("<html><body>" + bodyHtml + "</body></html>");
        return pane;
    }

    @Test
    public void testCopyPartialSelectionAndNoSelection() throws Exception {
        AtomicReference<String> clipboard = new AtomicReference<>();
        JEditorPaneImpl pane = createPane("<p>Hello world</p><pre><code>mvn clean install\n</code></pre>", clipboard);

        // No selection: clipboard must stay untouched
        pane.copy();
        assertNull(clipboard.get());

        // Partial selection inside paragraph
        String fullText = pane.getDocument().getText(0, pane.getDocument().getLength());
        int start = fullText.indexOf("world");
        pane.select(start, start + "world".length());
        pane.copy();
        assertEquals("world", clipboard.get());

        // Partial selection inside code block
        int cmdStart = fullText.indexOf("clean install");
        pane.select(cmdStart, cmdStart + "clean install".length());
        pane.copy();
        assertEquals("clean install", clipboard.get());
    }

    @Test
    public void testSelectAllCopyStripsSyntheticLeadingNewline() {
        AtomicReference<String> clipboard = new AtomicReference<>();
        JEditorPaneImpl pane = createPane("<h2>Title</h2><p>Line 1</p>", clipboard);

        pane.selectAll();
        pane.copy();

        assertEquals("Title\nLine 1", clipboard.get());
    }

    @Test
    public void testContextMenuItemsAndActionMap() throws Exception {
        AtomicReference<String> clipboard = new AtomicReference<>();
        JEditorPaneImpl pane = createPane("<p>Sample text</p>", clipboard);

        JPopupMenu menu = pane.getContextMenu();
        assertEquals(3, menu.getComponentCount());
        JMenuItem copyItem = (JMenuItem) menu.getComponent(0);
        JMenuItem copyLinkItem = (JMenuItem) menu.getComponent(1);
        JMenuItem selectAllItem = (JMenuItem) menu.getComponent(2);

        assertEquals("Copy", copyItem.getText());
        assertEquals("Copy Link", copyLinkItem.getText());
        assertEquals("Select All", selectAllItem.getText());
        assertFalse("Copy disabled without selection", copyItem.isEnabled());
        assertFalse("Copy Link hidden when not over a link", copyLinkItem.isVisible());
        assertTrue("Select All enabled when document has text", selectAllItem.isEnabled());

        // Trigger Select All via menu item
        selectAllItem.doClick();
        assertTrue(pane.hasSelectedText());
        menu = pane.getContextMenu();
        assertTrue("Copy enabled when text is selected", ((JMenuItem) menu.getComponent(0)).isEnabled());

        // Trigger Copy via ActionMap (Ctrl+C target)
        Action copyAction = pane.getActionMap().get(DefaultEditorKit.copyAction);
        assertNotNull(copyAction);
        copyAction.actionPerformed(new ActionEvent(pane, ActionEvent.ACTION_PERFORMED, "copy"));
        assertEquals("Sample text", clipboard.get());
    }

    @Test
    public void testCopyLinkContextMenu() throws Exception {
        AtomicReference<String> clipboard = new AtomicReference<>();
        JEditorPaneImpl pane = createPane("<p>See <a href=\"https://example.com/docs\">the docs</a> and plain text.</p>", clipboard);
        pane.setSize(500, 200);
        pane.getPreferredSize();

        String docText = pane.getDocument().getText(0, pane.getDocument().getLength());
        int linkOffset = docText.indexOf("the docs") + 2;
        int plainOffset = docText.indexOf("plain text") + 2;

        var linkRect = pane.modelToView2D(linkOffset).getBounds();
        var plainRect = pane.modelToView2D(plainOffset).getBounds();

        // Over plain text: Copy Link is hidden
        JPopupMenu plainMenu = pane.getContextMenu(new java.awt.Point(plainRect.x, plainRect.y + plainRect.height / 2));
        assertFalse(((JMenuItem) plainMenu.getComponent(1)).isVisible());

        // Over link: Copy Link is visible and copies the href URL
        JPopupMenu linkMenu = pane.getContextMenu(new java.awt.Point(linkRect.x, linkRect.y + linkRect.height / 2));
        JMenuItem copyLinkItem = (JMenuItem) linkMenu.getComponent(1);
        assertTrue(copyLinkItem.isVisible());
        copyLinkItem.doClick();
        assertEquals("https://example.com/docs", clipboard.get());
    }
}
