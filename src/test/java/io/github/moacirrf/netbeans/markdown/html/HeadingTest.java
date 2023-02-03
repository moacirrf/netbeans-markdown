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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class HeadingTest {

    private HtmlBuilder htmlBuilder = HtmlBuilder.getInstance();

    @Test
    public void testH1() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <h1 id=\"0-6\" class=\"removeMarginPaddingTop\"><span id=\"2-6\">text</span></h1>\n"
                + "  <h1 id=\"7-32\"><span id=\"7-19\">another text</span></h1>\n"
                + " </body>\n"
                + "</html>";

        var given = "# text\n"
                + "another text\n"
                + "============";

        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testH2() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <h1 id=\"0-6\" class=\"removeMarginPaddingTop\"><span id=\"2-6\">text</span></h1>\n"
                + "  <h2 id=\"7-32\"><span id=\"7-19\">another text</span></h2>\n"
                + " </body>\n"
                + "</html>";

        var given = "# text\n"
                + "another text\n"
                + "------------";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testH3() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <h3 id=\"0-8\" class=\"removeMarginPaddingTop\"><span id=\"4-8\">text</span></h3>\n"
                + "  <h3 id=\"9-25\"><span id=\"13-25\">another text</span></h3>\n"
                + " </body>\n"
                + "</html>";

        var given = "### text\n"
                + "### another text";

        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testH4() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <h4 id=\"0-9\" class=\"removeMarginPaddingTop\"><span id=\"5-9\">text</span></h4>\n"
                + "  <h4 id=\"10-27\"><span id=\"15-27\">another text</span></h4>\n"
                + " </body>\n"
                + "</html>";

        var given = "#### text\n"
                + "#### another text";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testH5() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <h5 id=\"0-10\" class=\"removeMarginPaddingTop\"><span id=\"6-10\">text</span></h5>\n"
                + "  <h5 id=\"11-29\"><span id=\"17-29\">another text</span></h5>\n"
                + " </body>\n"
                + "</html>";

        var given = "##### text\n"
                + "##### another text";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testH6() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <h6 id=\"0-11\" class=\"removeMarginPaddingTop\"><span id=\"7-11\">text</span></h6>\n"
                + "  <h6 id=\"12-31\"><span id=\"19-31\">another text</span></h6>\n"
                + " </body>\n"
                + "</html>";

        var given = "###### text\n"
                + "###### another text";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

}
