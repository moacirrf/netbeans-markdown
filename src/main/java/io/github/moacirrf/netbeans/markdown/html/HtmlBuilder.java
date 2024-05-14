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
import io.github.moacirrf.netbeans.markdown.html.flexmark.ImageRendererExtension;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class HtmlBuilder {

    public static final String MD_SOURCE_POSITION_ATTR = "id";
    private boolean includeFormTag;

    public static final MutableDataSet OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, asList(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    ImageRendererExtension.create(),
                    TaskListExtension.create()));

    public static final MutableDataSet OPTIONS_RENDERER = new MutableDataSet(OPTIONS)
            .set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, MD_SOURCE_POSITION_ATTR)
            .set(HtmlRenderer.SOURCE_POSITION_PARAGRAPH_LINES, true);

    public static HtmlBuilder getInstance() {
        return new HtmlBuilder(null);
    }

    private final List<HtmlAdjuster> htmlAdjusters = new ArrayList<>();

    private final MutableDataSet localOptions;

    private HtmlBuilder(MutableDataSet options) {
        this.localOptions = options;
        this.includeFormTag = false;
        htmlAdjusters.add(new TablesAdjuster());
        htmlAdjusters.add(new LinksAdjuster());
        htmlAdjusters.add(new FirstElementAdjuster());
        htmlAdjusters.add(new ListAdjuster());
        htmlAdjusters.add(new CheckboxAdjuster());
    }

    public String build(String markdownText) {
        var parser = Parser.builder(OPTIONS).build();
        MutableDataSet renderOptions = new MutableDataSet(OPTIONS_RENDERER.toDataSet());
        if (localOptions != null) {
            renderOptions.setAll(localOptions);
        }
        var renderer = HtmlRenderer.builder(OPTIONS_RENDERER).build();
        Node document = parser.parse(markdownText);

        if (document == null) {
            return "";
        }
        Document doc = Jsoup.parse(renderer.render(document));
        doc.getElementsByTag("head").remove();
        if (includeFormTag) {
            Element body = doc.getElementsByTag("body").get(0);
            var html = "<form>" + body.html() + "</form>";
            body.empty();
            body.append(html);
        }
        if (doc == null) {
            return "";
        }
        if (!htmlAdjusters.isEmpty()) {
            for (var adjuster : htmlAdjusters) {
                doc = adjuster.adjust(doc);
            }
        }
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        doc.outputSettings().charset("UTF-8");
        return doc.html();
    }

    public HtmlBuilder includeFormTag() {
        includeFormTag = true;
        return this;
    }

}
