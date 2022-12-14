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

/**
 * Clear top padding of the first element, include a custom css
 */
public class FirstElementAdjuster implements HtmlAdjuster {

    @Override
    public Document adjust(Document inputDocument) {
        if (inputDocument != null) {
            var body = inputDocument.getElementsByTag("body");
            if (body != null && body.first() != null && body.first().firstElementChild() != null) {
                body.first().firstElementChild()
                        .addClass("removeMarginPaddingTop");
            }
        }
        return inputDocument;
    }

}
