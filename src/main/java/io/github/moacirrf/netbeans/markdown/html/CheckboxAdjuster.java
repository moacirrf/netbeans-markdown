/*
 * Copyright (C) 2024 moacirrf
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

import java.util.function.Predicate;
import static javax.swing.text.html.HTML.Tag.LI;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author moacirrf
 */
public class CheckboxAdjuster implements HtmlAdjuster, Predicate<Element> {

    private static final String STYLE = "list-style-type:none";

    @Override
    public Document adjust(Document inputDocument) {
        inputDocument.getElementsByTag("input")
                .stream()
                .filter(this::test)
                .map(input -> {
                    if (!isLI(input.parent())) {
                        return input.parent().parent();
                    }
                    return input.parent();
                })
                .forEach(li
                        -> li.attr("style", STYLE));
        return inputDocument;
    }

    @Override
    public boolean test(Element inputElement) {
        return isLI(inputElement.parent()) || isLI(inputElement.parent().parent());
    }

    public boolean isLI(Element inputElement) {
        return LI.toString().equalsIgnoreCase(inputElement.tag().getName());
    }

}
