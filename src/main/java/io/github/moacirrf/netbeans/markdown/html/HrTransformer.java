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
package io.github.moacirrf.netbeans.markdown.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Workaround for Swing's {@code HRuleView}, which resolves its attributes in
 * the constructor before being attached to a parent view and therefore ignores
 * margins defined in the {@code StyleSheet}, reading only inline {@code style}.
 */
public class HrTransformer implements HtmlTransformer {

    private static final String HR_STYLE = "margin-top: 0px; margin-bottom: 7px;";

    @Override
    public Document adjust(Document inputDocument) {
        if (inputDocument == null) {
            return null;
        }
        for (Element hr : inputDocument.getElementsByTag("hr")) {
            String existing = hr.attr("style").trim();
            if (existing.isEmpty()) {
                hr.attr("style", HR_STYLE);
            } else {
                String separator = existing.endsWith(";") ? " " : "; ";
                hr.attr("style", existing + separator + HR_STYLE);
            }
        }
        return inputDocument;
    }
}
