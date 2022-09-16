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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This was necessary because HtmlEditorKit ignore collapsible property of
 * tables So the trick is create some css classes to remove borders.
 *
 */
public class TablesAdjuster implements HtmlAdjuster {

    @Override
    public Document adjust(Document document) {
        if (document != null && document.getElementsByTag("table") != null) {
            document.getElementsByTag("table").forEach(table
                    -> fixTableRowsStyle(table.getElementsByTag("tr"))
            );
        }
        return document;
    }

    private void fixTableRowsStyle(Elements rows) {
        Element firtRow = rows.first();
        if (firtRow != null) {
            rows.forEach((Element e) -> {
                if (!firtRow.equals(e)) {
                    e.getElementsByIndexEquals(0).forEach(element -> {
                        if (element.isBlock()) {
                            element.addClass("noBorderTop");
                        }
                    });
                    e.getElementsByIndexGreaterThan(0).forEach(element -> {
                        if (element.isBlock()) {
                            element.addClass("noBorderTopLeft");
                        }
                    });

                } else {
                    e.getElementsByIndexGreaterThan(0).forEach(element -> {
                        if (element.isBlock()) {
                            element.addClass("noBorderLeft");
                        }
                    });
                }
            });
        }
    }

}
