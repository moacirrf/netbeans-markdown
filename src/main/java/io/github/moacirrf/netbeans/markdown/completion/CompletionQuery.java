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

import static io.github.moacirrf.netbeans.markdown.completion.CompletionItemImpl.newItem;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class CompletionQuery extends AsyncCompletionQuery {

    private final Set<AbstractCompletionItem> itens = new HashSet<>();

    @Override
    protected void query(CompletionResultSet crs, Document document, int caretOffset) {
        addFormatHints(caretOffset);
        addLineSeparators(caretOffset);
        addHeadings(caretOffset);
        addBlockquotes(caretOffset);
        addLists(caretOffset);
        addCode(caretOffset);
        addLinks(caretOffset);
        addImages(caretOffset);
        addTables(caretOffset);
        addCheckbox(caretOffset);
        crs.addAllItems(itens);
        crs.finish();
    }

    private void addFormatHints(int caretOffset) {
        itens.add(newItem(0, "<i>Italic 1</i>", "*Italic 1*", caretOffset, true));
        itens.add(newItem(0, "<i>Italic 2</i>", "_Italic 2_", caretOffset, true));
        itens.add(newItem(0, "<b>Bold 1</b>", "**Bold 1**", caretOffset, true));
        itens.add(newItem(0, "<b>Bold 2</b>", "__Bold 2__", caretOffset, true));

        itens.add(newItem(1, "<b><i>Bold Italic 1</i></b>", "***Bold Italic 1***", caretOffset, true));
        itens.add(newItem(1, "<b><i>Bold Italic 2</i></b>", "___Bold Italic 2___", caretOffset, true));
        itens.add(newItem(1, "<b><i>Bold Italic 3</i></b>", "__*Bold Italic 3*__", caretOffset, true));
        itens.add(newItem(1, "<b><i>Bold Italic 4</i></b>", "**_Bold Italic 4_**", caretOffset, true));
    }

    private void addLineSeparators(int caretOffset) {
        itens.add(newItem(2, "Horizontal Line Separator", "***", caretOffset, false));
        itens.add(newItem(2, "Horizontal Line Separator", "___", caretOffset, false));
    }

    private void addHeadings(int caretOffset) {
        itens.add(newItem(3, "<h1>Heading level 1</h1>", "# Heading level 1", caretOffset, true));
        itens.add(newItem(3, "<h2>Heading level 2</h2>", "## Heading level 2", caretOffset, true));
        itens.add(newItem(3, "<h3>Heading level 3</h3>", "### Heading level 3", caretOffset, true));
        itens.add(newItem(3, "<h4>Heading level 4</h4>", "#### Heading level 4", caretOffset, true));
        itens.add(newItem(3, "<h5>Heading level 5</h5>", "##### Heading level 5", caretOffset, true));
        itens.add(newItem(3, "<h6>Heading level 6</h6>", "###### Heading level 6", caretOffset, true));
    }

    private void addBlockquotes(int caretOffset) {
        itens.add(newItem(4, "Blockquote", "> Blockquote", caretOffset, true));
    }

    private void addLists(int caretOffset) {
        itens.add(newItem(5, "Ordered List", "1. Item 1\n", caretOffset, true));
        itens.add(newItem(5, "Unordered List", "- Item 1\n", caretOffset, true));
    }

    private void addCode(int caretOffset) {
        itens.add(newItem(6, "Code Block", "        fun code(){}", caretOffset, true));
    }

    private void addLinks(int caretOffset) {
        itens.add(newItem(7, "Link", "[Description Here](https://netbeans.apache.org/).", caretOffset, false));
        itens.add(newItem(7, "Email URL", "<https://netbeans.apache.org/)>\n"
                + "<fake@fakedomain.com>", caretOffset, false));
    }

    private void addImages(int caretOffset) {
        itens.add(newItem(8, "Image 1",
                "![Description Here](https://raw.githubusercontent.com/moacirrf/netbeans-markdown/main/images/nblogo48x48.png)", caretOffset, false));
        itens.add(newItem(8, "Image Resizable", "<img src=\"https://raw.githubusercontent.com/moacirrf/netbeans-markdown/main/images/nblogo48x48.png\" width=\"60\" height=\"60\">", caretOffset, true));
    }

    private void addTables(int caretOffset) {
        try ( var stream = getClass().getResourceAsStream("/io/github/moacirrf/netbeans/markdown/completion/table.md")) {
            var item = newItem(9, "Table", "A Three Column Table", caretOffset, false);
            item.setTemplate(new String(stream.readAllBytes()));
            itens.add(item);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void addCheckbox(int caretOffset) {
         itens.add(newItem(10, "Checkbox", "- [x]", caretOffset, false));
    }
}
