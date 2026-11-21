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

import io.github.moacirrf.netbeans.markdown.ui.preview.image.ImageLabel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class ImageView extends ComponentView {

    private static final Set<String> BLOCK_CONTAINERS = Set.of("body", "blockquote", "div", "form");

    private JEditorPane editorPane;

    public ImageView(Element elem, JEditorPane editorPane) {
        super(elem);
        this.editorPane = editorPane;
    }

    @Override
    protected Component createComponent() {
        ImageLabel label = new ImageLabel(getElement(), editorPane);
        int bottomMargin = resolveBlockBottomMargin();
        if (bottomMargin <= 0) {
            return label;
        }
        return wrapWithBottomMargin(label, bottomMargin);
    }

    @Override
    public float getAlignment(int axis) {
        return 1;
    }

    /**
     * Raw HTML {@code <img>} tags written as standalone blocks in Markdown are
     * not wrapped in {@code <p>}; Swing places them inside an anonymous
     * {@code p-implied} with zero bottom margin, causing the next block to
     * stick to the image. Returns {@code true} only for such standalone images
     * so inline images and {@code <p><img></p>} are unaffected.
     */
    static boolean isStandaloneBlockImage(Element elem) {
        if (elem == null) {
            return false;
        }
        Element parent = elem.getParentElement();
        if (parent == null || !HTML.Tag.IMPLIED.toString().equals(parent.getName())) {
            return false;
        }
        Element grandParent = parent.getParentElement();
        if (grandParent == null || !BLOCK_CONTAINERS.contains(grandParent.getName())) {
            return false;
        }
        Document doc = elem.getDocument();
        if (doc == null) {
            return false;
        }
        for (int i = 0; i < parent.getElementCount(); i++) {
            Element child = parent.getElement(i);
            if (!HTML.Tag.IMG.toString().equals(child.getName())) {
                try {
                    String text = doc.getText(child.getStartOffset(), child.getEndOffset() - child.getStartOffset());
                    if (!text.isBlank()) {
                        return false;
                    }
                } catch (BadLocationException ex) {
                    return false;
                }
            }
        }
        return true;
    }

    int resolveBlockBottomMargin() {
        if (!isStandaloneBlockImage(getElement())) {
            return 0;
        }
        Document doc = getDocument();
        if (!(doc instanceof HTMLDocument)) {
            return 0;
        }
        StyleSheet styleSheet = ((HTMLDocument) doc).getStyleSheet();
        if (styleSheet == null) {
            return 0;
        }
        AttributeSet pRule = styleSheet.getRule("p");
        if (pRule == null) {
            return 0;
        }
        return Math.round(styleSheet.getBoxPainter(pRule).getInset(SwingConstants.BOTTOM, this));
    }

    private static Component wrapWithBottomMargin(Component component, int bottomMargin) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, bottomMargin, 0));
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }
}