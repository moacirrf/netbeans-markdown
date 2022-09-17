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

import io.github.moacirrf.netbeans.markdown.Icons;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class CompletionItemImpl extends AbstractCompletionItem {

    private static final Pattern LETTERS_NUMBERS = Pattern.compile("[A-Za-z 0-9]");
    private boolean selectTextAfterAction = true;

    public static AbstractCompletionItem newItem(
            int priority,
            String leftText,
            String rightText,
            int startOffset, boolean selectTextAfterInclude) {

        var item = new CompletionItemImpl();
        item.setSelectTextAfterAction(selectTextAfterInclude);
        item.setIcon(Icons.getICON_COMPLETION());
        item.setLeftHtmlText(leftText);
        item.setRighHtmlText("          " + rightText);
        item.setStartOffset(startOffset);
        item.setTemplate(rightText);
        item.setSortPriority(priority);
        return item;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            super.defaultAction(jtc);
            if (selectTextAfterAction) {
                int startSelect = 0;
                int endSelect = 0;
                for (int index = getStartOffset() + 1; index < (getStartOffset() + getTemplate().length()); index++) {
                    String text = jtc.getDocument().getText(index, 1);
                    if (LETTERS_NUMBERS.matcher(text).matches() && !text.equals(" ")) {
                        if (startSelect == 0) {
                            startSelect = index;
                        } else {
                            endSelect = index;
                        }
                    }
                }
                jtc.setCaretPosition(startSelect);
                jtc.select(startSelect, endSelect+1);
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isSelectTextAfterAction() {
        return selectTextAfterAction;
    }

    public void setSelectTextAfterAction(boolean selectTextAfterAction) {
        this.selectTextAfterAction = selectTextAfterAction;
    }

}
