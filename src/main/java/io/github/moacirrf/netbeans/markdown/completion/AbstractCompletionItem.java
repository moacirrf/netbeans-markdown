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
package io.github.moacirrf.netbeans.markdown.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public abstract class AbstractCompletionItem implements CompletionItem {

    private ImageIcon icon;
    private String leftHtmlText;
    private String righHtmlText;
    private String template;

    private int startOffset;
    private int sortPriority;

    @Override
    public boolean instantSubstitution(JTextComponent jtc) {
        return false;
    }

    @Override
    public int getPreferredWidth(Graphics g, Font font) {
        return CompletionUtilities
                .getPreferredWidth(leftHtmlText, righHtmlText, g, font);
    }

    @Override
    public int getSortPriority() {
        return sortPriority;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            Document doc = jtc.getDocument();
            doc.insertString(startOffset, getTemplate(), null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent ke) {

    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {

        CompletionUtilities.renderHtml(getIcon(),
                leftHtmlText, righHtmlText,
                g,
                defaultFont,
                defaultColor,
                width,
                height,
                selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public CharSequence getSortText() {
        return leftHtmlText;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return leftHtmlText;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getLeftHtmlText() {
        return leftHtmlText;
    }

    public void setLeftHtmlText(String leftHtmlText) {
        this.leftHtmlText = leftHtmlText;
    }

    public String getRighHtmlText() {
        return righHtmlText;
    }

    public void setRighHtmlText(String righHtmlText) {
        this.righHtmlText = righHtmlText;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public void setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
    }

}
