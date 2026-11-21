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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.View;

/**
 * Single per-{@link JEditorPane} controller handling hover fade-in/fade-out,
 * cursor, tooltip, and click-to-copy interactions for all active
 * {@link CodeBlockView} instances without attaching per-view listeners.
 */
final class CodeBlockCopyController {

    private static final int FADE_TICK_MS = 16;
    private static final float FADE_IN_STEP = 0.12f;
    private static final float FADE_OUT_STEP = 0.09f;
    private static final int COPIED_RESET_DELAY_MS = 1500;
    private static final String TOOLTIP_COPY = "Copy";
    private static final String TOOLTIP_COPIED = "Copied!";

    private final JEditorPane editorPane;
    private final Consumer<String> clipboardWriter;
    private final Set<CodeBlockView> animatingViews = new HashSet<>();
    private final Timer fadeTimer;
    private final Timer copiedResetTimer;

    private CodeBlockView hoveredCard;
    private CodeBlockView hoveredButton;
    private CodeBlockView copiedView;
    private boolean cursorManaged;
    private boolean tooltipManaged;
    private Cursor previousCursor;

    CodeBlockCopyController(JEditorPane editorPane) {
        this(editorPane, CodeBlockCopyController::copyToSystemClipboard);
    }

    CodeBlockCopyController(JEditorPane editorPane, Consumer<String> clipboardWriter) {
        this.editorPane = editorPane;
        this.clipboardWriter = clipboardWriter;
        this.fadeTimer = new Timer(FADE_TICK_MS, e -> stepFadeAnimation());
        this.fadeTimer.setRepeats(true);

        this.copiedResetTimer = new Timer(COPIED_RESET_DELAY_MS, e -> clearCopiedState());
        this.copiedResetTimer.setRepeats(false);

        this.editorPane.addPropertyChangeListener("document", evt -> reset());
    }

    /**
     * Handles mouse press/release/click/exit before Swing's caret/UI processes
     * the event. Returns {@code true} when the event targeted a copy button and
     * was consumed.
     */
    boolean handleMouseEvent(MouseEvent e) {
        int id = e.getID();
        if (id == MouseEvent.MOUSE_EXITED) {
            clearHover();
            return false;
        }
        if (e.isPopupTrigger() || !SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }
        if (id == MouseEvent.MOUSE_PRESSED || id == MouseEvent.MOUSE_RELEASED || id == MouseEvent.MOUSE_CLICKED) {
            CodeBlockView btnTarget = findButtonHit(e.getPoint());
            if (btnTarget != null) {
                if (id == MouseEvent.MOUSE_PRESSED) {
                    copyFromView(btnTarget);
                }
                e.consume();
                return true;
            }
        }
        return false;
    }

    /**
     * Handles mouse motion before Swing's UI updates cursors. Returns
     * {@code true} when hovering over a copy button so the hand cursor is not
     * overwritten by Swing's text cursor.
     */
    boolean handleMouseMotionEvent(MouseEvent e) {
        if (e.getID() != MouseEvent.MOUSE_MOVED) {
            return false;
        }
        Point point = e.getPoint();
        List<CodeBlockView> views = collectCodeBlockViews();
        Rectangle visibleRect = editorPane.getVisibleRect();

        CodeBlockView newCard = null;
        CodeBlockView newButton = null;
        for (CodeBlockView view : views) {
            Rectangle card = view.getLastCardBounds();
            if (card != null && card.contains(point)) {
                newCard = view;
                Rectangle btn = view.getButtonBounds(visibleRect);
                if (btn != null && btn.contains(point)) {
                    newButton = view;
                }
                break;
            }
        }

        updateHoverState(newCard, newButton);
        return newButton != null;
    }

    void copyFromView(CodeBlockView view) {
        if (view == null) {
            return;
        }
        String code = view.extractCodeText();
        clipboardWriter.accept(code);

        if (copiedView != null && copiedView != view) {
            copiedView.setCopied(false);
            scheduleFade(copiedView);
        }
        copiedView = view;
        view.setCopied(true);
        view.setButtonAlpha(1.0f);
        repaintButton(view);

        if (hoveredButton == view) {
            setManagedTooltip(TOOLTIP_COPIED);
        }
        copiedResetTimer.restart();
    }

    private void clearCopiedState() {
        if (copiedView != null) {
            CodeBlockView prev = copiedView;
            copiedView = null;
            prev.setCopied(false);
            if (hoveredButton == prev) {
                setManagedTooltip(TOOLTIP_COPY);
            }
            scheduleFade(prev);
            repaintButton(prev);
        }
    }

    private void clearHover() {
        updateHoverState(null, null);
    }

    private void updateHoverState(CodeBlockView newCard, CodeBlockView newButton) {
        if (hoveredCard != newCard) {
            if (hoveredCard != null) {
                hoveredCard.setCardHovered(false);
                scheduleFade(hoveredCard);
            }
            hoveredCard = newCard;
            if (hoveredCard != null) {
                hoveredCard.setCardHovered(true);
                scheduleFade(hoveredCard);
            }
        }

        if (hoveredButton != newButton) {
            CodeBlockView prevBtn = hoveredButton;
            hoveredButton = newButton;
            if (prevBtn != null) {
                prevBtn.setButtonHovered(false);
                repaintButton(prevBtn);
            }
            if (hoveredButton != null) {
                hoveredButton.setButtonHovered(true);
                repaintButton(hoveredButton);
                setManagedCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setManagedTooltip(hoveredButton.isCopied() ? TOOLTIP_COPIED : TOOLTIP_COPY);
            } else {
                restoreCursor();
                restoreTooltip();
            }
        } else if (hoveredButton != null) {
            setManagedCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    private void scheduleFade(CodeBlockView view) {
        if (Math.abs(view.getButtonAlpha() - view.getTargetAlpha()) > 0.001f) {
            animatingViews.add(view);
            if (!fadeTimer.isRunning()) {
                fadeTimer.start();
            }
        } else {
            repaintButton(view);
        }
    }

    private void stepFadeAnimation() {
        if (animatingViews.isEmpty()) {
            fadeTimer.stop();
            return;
        }
        Iterator<CodeBlockView> it = animatingViews.iterator();
        while (it.hasNext()) {
            CodeBlockView view = it.next();
            float current = view.getButtonAlpha();
            float target = view.getTargetAlpha();
            if (current < target) {
                current = Math.min(target, current + FADE_IN_STEP);
            } else if (current > target) {
                current = Math.max(target, current - FADE_OUT_STEP);
            }
            view.setButtonAlpha(current);
            repaintButton(view);
            if (Math.abs(current - target) <= 0.001f) {
                view.setButtonAlpha(target);
                it.remove();
            }
        }
        if (animatingViews.isEmpty()) {
            fadeTimer.stop();
        }
    }

    private CodeBlockView findButtonHit(Point point) {
        Rectangle visibleRect = editorPane.getVisibleRect();
        for (CodeBlockView view : collectCodeBlockViews()) {
            if (view.getButtonAlpha() <= 0.01f && !view.isCardHovered() && !view.isCopied()) {
                continue;
            }
            Rectangle btn = view.getButtonBounds(visibleRect);
            if (btn != null && btn.contains(point)) {
                return view;
            }
        }
        return null;
    }

    private List<CodeBlockView> collectCodeBlockViews() {
        List<CodeBlockView> result = new ArrayList<>();
        if (editorPane.getUI() == null) {
            return result;
        }
        View root = editorPane.getUI().getRootView(editorPane);
        if (root != null) {
            collectRecursive(root, result);
        }
        return result;
    }

    private static void collectRecursive(View view, List<CodeBlockView> result) {
        if (view instanceof CodeBlockView) {
            result.add((CodeBlockView) view);
            return;
        }
        int count = view.getViewCount();
        for (int i = 0; i < count; i++) {
            collectRecursive(view.getView(i), result);
        }
    }

    private void repaintButton(CodeBlockView view) {
        Rectangle btn = view.getButtonBounds(editorPane.getVisibleRect());
        if (btn != null) {
            editorPane.repaint(btn.x - 2, btn.y - 2, btn.width + 4, btn.height + 4);
        } else {
            Rectangle card = view.getLastCardBounds();
            if (card != null) {
                editorPane.repaint(card);
            }
        }
    }

    private void setManagedCursor(Cursor cursor) {
        if (!cursorManaged) {
            previousCursor = editorPane.getCursor();
            cursorManaged = true;
        }
        editorPane.setCursor(cursor);
    }

    private void restoreCursor() {
        if (cursorManaged) {
            editorPane.setCursor(previousCursor != null ? previousCursor : Cursor.getDefaultCursor());
            previousCursor = null;
            cursorManaged = false;
        }
    }

    private void setManagedTooltip(String text) {
        tooltipManaged = true;
        editorPane.setToolTipText(text);
    }

    private void restoreTooltip() {
        if (tooltipManaged) {
            editorPane.setToolTipText(null);
            tooltipManaged = false;
        }
    }

    void reset() {
        fadeTimer.stop();
        copiedResetTimer.stop();
        animatingViews.clear();
        hoveredCard = null;
        hoveredButton = null;
        copiedView = null;
        restoreCursor();
        restoreTooltip();
    }

    private static void copyToSystemClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }
}
