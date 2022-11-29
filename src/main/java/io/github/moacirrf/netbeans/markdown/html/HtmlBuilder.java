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
package io.github.moacirrf.netbeans.markdown.html;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class HtmlBuilder {

    public static final String MD_SOURCE_POSITION_ATTR = "id";

    public static HtmlBuilder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HtmlBuilder();
        }
        return INSTANCE;
    }

    private final List<HtmlAdjuster> htmlAdjusters = new ArrayList<>();
    private static HtmlBuilder INSTANCE;

    private HtmlBuilder() {
        htmlAdjusters.add(new TablesAdjuster());
        htmlAdjusters.add(new LinksAdjuster());
        htmlAdjusters.add(new FirstElementAdjuster());
    }

    public String build(String markdownText) {

        var options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, asList(TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create()));
        //.set(HtmlRenderer.SOFT_BREAK, "<br/>\n");

        var parser = Parser.builder(options).build();
        options.set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, MD_SOURCE_POSITION_ATTR);
        options.set(HtmlRenderer.SOURCE_POSITION_PARAGRAPH_LINES, true);

        var renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdownText);

        if (document == null) {
            return "";
        }
        Document doc = Jsoup.parse(renderer.render(document));

        if (doc == null) {
            return "";
        }
        if (!htmlAdjusters.isEmpty()) {
            for (var adjuster : htmlAdjusters) {
                doc = adjuster.adjust(doc);
            }
        }
        return doc.html();
    }

  
}
