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
import static io.github.moacirrf.netbeans.markdown.Icons.getICON_COMPLETION;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.text.Document;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class CompletionQuery extends AsyncCompletionQuery {

    @Override
    protected void query(CompletionResultSet crs, Document document, int caretOffset) {
        crs.addAllItems(getFormatHints(document, caretOffset));
        crs.addAllItems(getHeadings(document, caretOffset));
        crs.addAllItems(getBlockquotes(document, caretOffset));
        crs.addAllItems(getLists(document, caretOffset));
        crs.addAllItems(getCode(document, caretOffset));
        crs.addAllItems(getLinks(document, caretOffset));
        crs.addAllItems(getImages(document, caretOffset));
        crs.addAllItems(getTables(document, caretOffset));
        crs.finish();
    }

    private Collection<? extends CompletionItem> getFormatHints(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(0, icon, "<i>Italic 1</i>", "*Italic 1*", caretOffset));
        itens.add(newItem(0, icon, "<i>Italic 2</i>", "_Italic 2_", caretOffset));
        itens.add(newItem(0, icon, "<b>Bold 1</b>", "**Bold 1**", caretOffset));
        itens.add(newItem(0, icon, "<b>Bold 2</b>", "__Bold 2__", caretOffset));

        itens.add(newItem(1, icon, "<b><i>Bold Italic 1</i></b>", "***Bold Italic 1***", caretOffset));
        itens.add(newItem(1, icon, "<b><i>Bold Italic 2</i></b>", "___Bold Italic 2___", caretOffset));
        itens.add(newItem(1, icon, "<b><i>Bold Italic 3</i></b>", "__*Bold Italic 3*__", caretOffset));
        itens.add(newItem(1, icon, "<b><i>Bold Italic 4</i></b>", "**_Bold Italic 4_**", caretOffset));
        return itens;
    }

    private Collection<? extends CompletionItem> getHeadings(Document document, int caretOffset) {
        var itens = new ArrayList<AbstractCompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(2, icon, "<h1>Heading level 1</h1>", "# Heading level 1", caretOffset));
        itens.add(newItem(2, icon, "<h2>Heading level 2</h2>", "## Heading level 2", caretOffset));
        itens.add(newItem(2, icon, "<h3>Heading level 3</h3>", "### Heading level 3", caretOffset));
        itens.add(newItem(2, icon, "<h4>Heading level 4</h4>", "#### Heading level 4", caretOffset));
        itens.add(newItem(2, icon, "<h5>Heading level 5</h5>", "##### Heading level 5", caretOffset));
        itens.add(newItem(2, icon, "<h6>Heading level 6</h6>", "###### Heading level 6", caretOffset));

        return itens;
    }

    private Collection<? extends CompletionItem> getBlockquotes(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(3, icon, "Blockquote", "> Blockquote", caretOffset));

        return itens;
    }

    private Collection<? extends CompletionItem> getLists(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(4, icon, "Ordered List", "1. Item 1\n", caretOffset));
        itens.add(newItem(4, icon, "Unordered List", "- Item 1\n", caretOffset));

        return itens;
    }

    private Collection<? extends CompletionItem> getCode(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(5, icon, "Code Block", "    fun code(){}", caretOffset));

        return itens;
    }

    private Collection<? extends CompletionItem> getLinks(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(6, icon, "Link", "[Description](https://netbeans.apache.org/).", caretOffset));
        itens.add(newItem(6, icon, "Email URL", "<https://netbeans.apache.org/)>\n"
                + "<fake@fakedomain.com>", caretOffset));
        return itens;
    }

    private Collection<? extends CompletionItem> getImages(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        itens.add(newItem(7, icon, "Image 1",
                "![Best IDE](https://netbeans.apache.org/images/nblogo48x48.png)", caretOffset));
        itens.add(newItem(7, icon, "Image Resizable", "<img src=\"https://netbeans.apache.org/images/nblogo48x48.png\" width=\"60\" height=\"60\">", caretOffset));

        return itens;
    }

    private Collection<? extends CompletionItem> getTables(Document document, int caretOffset) {
        var itens = new ArrayList<CompletionItem>();
        var icon = getICON_COMPLETION();
        try ( var stream = getClass().getResourceAsStream("/io/github/moacirrf/netbeans/markdown/completion/table.md")) {
            var item = newItem(7, icon, "Table", "A three Column table", caretOffset);
            item.setTemplate(new String(stream.readAllBytes()));
            itens.add(item);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return itens;
    }
}
