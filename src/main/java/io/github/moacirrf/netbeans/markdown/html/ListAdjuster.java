/*
 * Copyright (C) 2023 moacirrf
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

/**
 *
 * @author moacirrf
 */
public class ListAdjuster implements HtmlAdjuster {

    @Override
    public Document adjust(Document inputDocument) {
        var elements = inputDocument.getElementsByTag("ul");
        if (elements != null) {
            elements.forEach(e -> {
                var next = e.nextElementSibling();
                if (next != null && "p".equals(next.tagName().toLowerCase())) {
                    e.addClass("margin-bottom");
                }
            });
        }
        return inputDocument;
    }

}
