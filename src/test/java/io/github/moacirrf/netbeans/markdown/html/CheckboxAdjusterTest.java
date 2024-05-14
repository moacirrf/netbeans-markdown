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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author moacirrf
 */
public class CheckboxAdjusterTest {

    private HtmlBuilder htmlBuilder = HtmlBuilder.getInstance();

    public static final String HTML_GIVEN = "<html>\n"
            + " <body>\n"
            + "  <form class=\"removeMarginPaddingTop\">\n"
            + "   <ul>\n"
            + "    <li class=\"task-list-item\" id=\"0-13\"><input type=\"checkbox\" class=\"task-list-item-checkbox\" checked disabled readonly>&nbsp;<span id=\"6-12\">Design</span></li>\n"
            + "    <li class=\"task-list-item\" id=\"13-29\"><input type=\"checkbox\" class=\"task-list-item-checkbox\" disabled readonly>&nbsp;<span id=\"19-29\">Production</span></li>\n"
            + "   </ul>\n"
            + "  </form>\n"
            + " </body>\n"
            + "</html>";


    @Test
    public void testAdjust() {
        var adjuster = new CheckboxAdjuster();

        Document given = Jsoup.parse(HTML_GIVEN);

        Document document = adjuster.adjust(given);

        boolean expected = hasStyle(document, "list-style-type:none");
        
        Assert.assertTrue(expected);
    }

    public boolean hasStyle(Document doc, String style) {
        return doc.getElementsByTag("li").attr("style").contains(style);
    }

}
