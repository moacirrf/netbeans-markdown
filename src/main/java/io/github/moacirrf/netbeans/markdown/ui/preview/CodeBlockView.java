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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.View;
import javax.swing.text.html.BlockView;
import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet;

/**
 * Custom {@link BlockView} for {@code <pre>} code blocks that renders a VS Code
 * style rounded card with a subtle border and a fade-in/fade-out Copy button in
 * the top-right corner while preserving native Swing text selection and layout.
 */
public class CodeBlockView extends BlockView {

    static final int CARD_ARC = 10;
    static final int BUTTON_SIZE = 28;
    static final int BUTTON_ARC = 6;
    static final int BUTTON_INSET = 8;
    static final int SINGLE_LINE_MAX_HEIGHT = 54;

    private static final Color DARK_BG_FALLBACK = new Color(0x3C, 0x3D, 0x3E);
    private static final Color LIGHT_BG_FALLBACK = new Color(0xF5, 0xF7, 0xF9);

    private static final Color DARK_CARD_BORDER = new Color(255, 255, 255, 34);
    private static final Color LIGHT_CARD_BORDER = new Color(0xD8, 0xDE, 0xE4);

    private static final Color DARK_BTN_BG = new Color(255, 255, 255, 16);
    private static final Color DARK_BTN_BG_HOVER = new Color(255, 255, 255, 38);
    private static final Color DARK_BTN_BORDER = new Color(255, 255, 255, 36);
    private static final Color DARK_BTN_BORDER_HOVER = new Color(255, 255, 255, 64);
    private static final Color DARK_ICON = new Color(0xC8, 0xCD, 0xD2);
    private static final Color DARK_ICON_HOVER = new Color(0xFF, 0xFF, 0xFF);
    private static final Color DARK_COPIED_BG = new Color(0x4E, 0xC9, 0x78, 36);
    private static final Color DARK_COPIED_BORDER = new Color(0x4E, 0xC9, 0x78, 120);
    private static final Color DARK_COPIED_ICON = new Color(0x4E, 0xC9, 0x78);

    private static final Color LIGHT_BTN_BG = new Color(0, 0, 0, 10);
    private static final Color LIGHT_BTN_BG_HOVER = new Color(0, 0, 0, 24);
    private static final Color LIGHT_BTN_BORDER = new Color(0, 0, 0, 32);
    private static final Color LIGHT_BTN_BORDER_HOVER = new Color(0, 0, 0, 56);
    private static final Color LIGHT_ICON = new Color(0x57, 0x60, 0x6A);
    private static final Color LIGHT_ICON_HOVER = new Color(0x1F, 0x23, 0x28);
    private static final Color LIGHT_COPIED_BG = new Color(0x1A, 0x7F, 0x37, 26);
    private static final Color LIGHT_COPIED_BORDER = new Color(0x1A, 0x7F, 0x37, 105);
    private static final Color LIGHT_COPIED_ICON = new Color(0x1A, 0x7F, 0x37);

    private int marginTop;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;

    private Rectangle lastCardBounds;
    private boolean cardHovered;
    private boolean buttonHovered;
    private boolean copied;
    private float buttonAlpha;

    public CodeBlockView(Element elem) {
        super(elem, View.Y_AXIS);
    }

    @Override
    protected void setPropertiesFromAttributes() {
        super.setPropertiesFromAttributes();
        resolveMargins();
    }

    private void resolveMargins() {
        AttributeSet attr = getAttributes();
        StyleSheet ss = getStyleSheet();
        if (attr == null || ss == null) {
            marginTop = marginBottom = marginLeft = marginRight = 0;
            return;
        }
        SimpleAttributeSet marginsOnly = new SimpleAttributeSet();
        for (CSS.Attribute key : new CSS.Attribute[]{
            CSS.Attribute.MARGIN_TOP,
            CSS.Attribute.MARGIN_BOTTOM,
            CSS.Attribute.MARGIN_LEFT,
            CSS.Attribute.MARGIN_RIGHT
        }) {
            Object val = attr.getAttribute(key);
            if (val != null) {
                marginsOnly.addAttribute(key, val);
            }
        }
        View marginProbe = new View(getElement()) {
            @Override
            public AttributeSet getAttributes() {
                return marginsOnly;
            }

            @Override
            public float getPreferredSpan(int axis) {
                return 0;
            }

            @Override
            public void paint(Graphics g, Shape allocation) {
            }

            @Override
            public Shape modelToView(int pos, Shape a, Position.Bias b) {
                return null;
            }

            @Override
            public int viewToModel(float x, float y, Shape a, Position.Bias[] biasReturn) {
                return 0;
            }
        };
        StyleSheet.BoxPainter mp = ss.getBoxPainter(marginsOnly);
        marginTop = Math.round(mp.getInset(SwingConstants.TOP, marginProbe));
        marginBottom = Math.round(mp.getInset(SwingConstants.BOTTOM, marginProbe));
        marginLeft = Math.round(mp.getInset(SwingConstants.LEFT, marginProbe));
        marginRight = Math.round(mp.getInset(SwingConstants.RIGHT, marginProbe));
    }

    @Override
    public void paint(Graphics g, Shape allocation) {
        Rectangle alloc = (allocation instanceof Rectangle) ? (Rectangle) allocation : allocation.getBounds();
        Rectangle card = new Rectangle(
                alloc.x + marginLeft,
                alloc.y + marginTop,
                Math.max(0, alloc.width - marginLeft - marginRight),
                Math.max(0, alloc.height - marginTop - marginBottom)
        );
        this.lastCardBounds = card;

        Color bg = resolveCardBackground();
        boolean dark = isDarkColor(bg);

        paintCardBackground(g, card, bg, dark);
        paintChildren(g, alloc);

        if (buttonAlpha > 0.01f) {
            Rectangle visibleRect = resolveVisibleRect();
            Rectangle btn = computeButtonBounds(card, visibleRect);
            if (btn != null) {
                paintCopyButton(g, btn, dark);
            }
        }
    }

    private void paintChildren(Graphics g, Rectangle alloc) {
        int n = getViewCount();
        int x = alloc.x + getLeftInset();
        int y = alloc.y + getTopInset();
        Rectangle clip = g.getClipBounds();
        Rectangle childAlloc = new Rectangle();
        for (int i = 0; i < n; i++) {
            childAlloc.x = x + getOffset(X_AXIS, i);
            childAlloc.y = y + getOffset(Y_AXIS, i);
            childAlloc.width = getSpan(X_AXIS, i);
            childAlloc.height = getSpan(Y_AXIS, i);
            if (clip == null || childAlloc.intersects(clip)) {
                paintChild(g, childAlloc, i);
            }
        }
    }

    private void paintCardBackground(Graphics g, Rectangle card, Color bg, boolean dark) {
        if (card.width <= 0 || card.height <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(card.x, card.y, card.width, card.height, CARD_ARC, CARD_ARC));

            g2.setColor(dark ? DARK_CARD_BORDER : LIGHT_CARD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(
                    card.x + 0.5f,
                    card.y + 0.5f,
                    Math.max(0, card.width - 1f),
                    Math.max(0, card.height - 1f),
                    CARD_ARC,
                    CARD_ARC
            ));
        } finally {
            g2.dispose();
        }
    }

    private void paintCopyButton(Graphics g, Rectangle btn, boolean dark) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, buttonAlpha))));

            Color fill;
            Color border;
            Color iconColor;
            if (copied) {
                fill = dark ? DARK_COPIED_BG : LIGHT_COPIED_BG;
                border = dark ? DARK_COPIED_BORDER : LIGHT_COPIED_BORDER;
                iconColor = dark ? DARK_COPIED_ICON : LIGHT_COPIED_ICON;
            } else if (buttonHovered) {
                fill = dark ? DARK_BTN_BG_HOVER : LIGHT_BTN_BG_HOVER;
                border = dark ? DARK_BTN_BORDER_HOVER : LIGHT_BTN_BORDER_HOVER;
                iconColor = dark ? DARK_ICON_HOVER : LIGHT_ICON_HOVER;
            } else {
                fill = dark ? DARK_BTN_BG : LIGHT_BTN_BG;
                border = dark ? DARK_BTN_BORDER : LIGHT_BTN_BORDER;
                iconColor = dark ? DARK_ICON : LIGHT_ICON;
            }

            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(btn.x, btn.y, btn.width, btn.height, BUTTON_ARC, BUTTON_ARC));

            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(
                    btn.x + 0.5f,
                    btn.y + 0.5f,
                    btn.width - 1f,
                    btn.height - 1f,
                    BUTTON_ARC,
                    BUTTON_ARC
            ));

            g2.setColor(iconColor);
            if (copied) {
                paintCheckIcon(g2, btn);
            } else {
                paintCopyIcon(g2, btn);
            }
        } finally {
            g2.dispose();
        }
    }

    private static void paintCopyIcon(Graphics2D g2, Rectangle btn) {
        g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float x = btn.x;
        float y = btn.y;

        // Front sheet (bottom-right)
        g2.draw(new RoundRectangle2D.Float(x + 11f, y + 11f, 9f, 9f, 2f, 2f));

        // Back sheet (top-left L-frame behind front sheet)
        Path2D.Float back = new Path2D.Float();
        back.moveTo(x + 11f, y + 17f);
        back.lineTo(x + 9f, y + 17f);
        back.quadTo(x + 8f, y + 17f, x + 8f, y + 16f);
        back.lineTo(x + 8f, y + 9f);
        back.quadTo(x + 8f, y + 8f, x + 9f, y + 8f);
        back.lineTo(x + 16f, y + 8f);
        back.quadTo(x + 17f, y + 8f, x + 17f, y + 9f);
        back.lineTo(x + 17f, y + 11f);
        g2.draw(back);
    }

    private static void paintCheckIcon(Graphics2D g2, Rectangle btn) {
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float x = btn.x;
        float y = btn.y;
        Path2D.Float check = new Path2D.Float();
        check.moveTo(x + 8.5f, y + 14.5f);
        check.lineTo(x + 12.2f, y + 18.2f);
        check.lineTo(x + 19.8f, y + 10.0f);
        g2.draw(check);
    }

    private Color resolveCardBackground() {
        StyleSheet ss = getStyleSheet();
        AttributeSet attr = getAttributes();
        if (ss != null && attr != null) {
            Color bg = ss.getBackground(attr);
            if (bg != null) {
                return bg;
            }
        }
        return UIManager.getBoolean("nb.dark.theme") ? DARK_BG_FALLBACK : LIGHT_BG_FALLBACK;
    }

    private static boolean isDarkColor(Color color) {
        double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        return luminance < 128.0;
    }

    private Rectangle resolveVisibleRect() {
        Container container = getContainer();
        if (container instanceof JComponent) {
            return ((JComponent) container).getVisibleRect();
        }
        return null;
    }

    /**
     * Computes the copy button bounds inside {@code cardBounds}, clamping to
     * {@code visibleRect} when a long code block overflows horizontally.
     */
    static Rectangle computeButtonBounds(Rectangle cardBounds, Rectangle visibleRect) {
        if (cardBounds == null || cardBounds.width < BUTTON_SIZE + 4 || cardBounds.height < BUTTON_SIZE + 4) {
            return null;
        }
        int rightEdge = cardBounds.x + cardBounds.width;
        if (visibleRect != null && visibleRect.width > 0) {
            int visibleRight = visibleRect.x + visibleRect.width;
            if (visibleRight < rightEdge) {
                int minRight = cardBounds.x + BUTTON_SIZE + BUTTON_INSET * 2;
                rightEdge = Math.max(minRight, visibleRight);
            }
        }
        int btnX = rightEdge - BUTTON_SIZE - BUTTON_INSET;
        int btnY = (cardBounds.height <= SINGLE_LINE_MAX_HEIGHT)
                ? cardBounds.y + (cardBounds.height - BUTTON_SIZE) / 2
                : cardBounds.y + BUTTON_INSET;
        return new Rectangle(btnX, btnY, BUTTON_SIZE, BUTTON_SIZE);
    }

    /**
     * Extracts plain code text from this {@code <pre>} element, stripping
     * synthetic trailing line breaks so pasting a single command into a
     * terminal does not execute it automatically.
     */
    public String extractCodeText() {
        Element elem = getElement();
        Document doc = getDocument();
        if (elem == null || doc == null) {
            return "";
        }
        int start = elem.getStartOffset();
        int end = elem.getEndOffset();
        if (end <= start) {
            return "";
        }
        try {
            return trimTrailingLineBreaks(doc.getText(start, end - start));
        } catch (BadLocationException ex) {
            return "";
        }
    }

    static String trimTrailingLineBreaks(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int end = text.length();
        while (end > 0 && (text.charAt(end - 1) == '\n' || text.charAt(end - 1) == '\r')) {
            end--;
        }
        return text.substring(0, end);
    }

    Rectangle getLastCardBounds() {
        return lastCardBounds;
    }

    Rectangle getButtonBounds(Rectangle visibleRect) {
        return computeButtonBounds(lastCardBounds, visibleRect);
    }

    boolean isCardHovered() {
        return cardHovered;
    }

    void setCardHovered(boolean cardHovered) {
        this.cardHovered = cardHovered;
    }

    boolean isButtonHovered() {
        return buttonHovered;
    }

    void setButtonHovered(boolean buttonHovered) {
        this.buttonHovered = buttonHovered;
    }

    boolean isCopied() {
        return copied;
    }

    void setCopied(boolean copied) {
        this.copied = copied;
    }

    float getButtonAlpha() {
        return buttonAlpha;
    }

    void setButtonAlpha(float buttonAlpha) {
        this.buttonAlpha = Math.max(0f, Math.min(1f, buttonAlpha));
    }

    float getTargetAlpha() {
        return (cardHovered || copied) ? 1.0f : 0.0f;
    }
}
