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

import org.jsoup.Jsoup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HrTransformerTest {

    @Test
    public void testAddsMarginStyleToHr() {
        var doc = Jsoup.parse("<p>Before</p><hr><p>After</p>");
        new HrTransformer().adjust(doc);
        var hr = doc.selectFirst("hr");
        assertEquals("margin-top: 0px; margin-bottom: 7px;", hr.attr("style"));
    }

    @Test
    public void testPreservesExistingStyleOnHr() {
        var doc = Jsoup.parse("<hr style=\"color: red\">");
        new HrTransformer().adjust(doc);
        var style = doc.selectFirst("hr").attr("style");
        assertTrue(style.startsWith("color: red; "));
        assertTrue(style.contains("margin-bottom: 7px;"));
    }

    @Test
    public void testHandlesNullDocument() {
        assertNull(new HrTransformer().adjust(null));
    }

    @Test
    public void testHtmlBuilderAppliesHrStyle() {
        String html = HtmlBuilder.getInstance().build("Above\n\n---\n\nBelow");
        var hr = Jsoup.parse(html).selectFirst("hr");
        assertEquals("margin-top: 0px; margin-bottom: 7px;", hr.attr("style"));
    }
}
