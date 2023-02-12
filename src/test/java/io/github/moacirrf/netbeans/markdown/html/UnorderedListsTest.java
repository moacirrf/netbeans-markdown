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
public class UnorderedListsTest {

    private HtmlBuilder htmlBuilder = HtmlBuilder.getInstance();

    @Test
    public void test1() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <ul class=\"removeMarginPaddingTop\">\n"
                + "   <li id=\"0-13\"><span id=\"2-12\">First item</span></li>\n"
                + "   <li id=\"13-26\"><span id=\"15-26\">Second item</span></li>\n"
                + "  </ul>\n"
                + " </body>\n"
                + "</html>";

        var given = "- First item\n"
                + "- Second item";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void test2() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <ul class=\"removeMarginPaddingTop\">\n"
                + "   <li id=\"0-13\"><span id=\"2-12\">First item</span></li>\n"
                + "   <li id=\"13-26\"><span id=\"15-26\">Second item</span></li>\n"
                + "  </ul>\n"
                + " </body>\n"
                + "</html>";

        var given = "* First item\n"
                + "* Second item";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void test3() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <ul class=\"removeMarginPaddingTop\">\n"
                + "   <li id=\"0-13\"><span id=\"2-12\">First item</span></li>\n"
                + "   <li id=\"13-26\"><span id=\"15-26\">Second item</span></li>\n"
                + "  </ul>\n"
                + " </body>\n"
                + "</html>";

        var given = "+ First item\n"
                + "+ Second item";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void indentedTest() {
        var expected = "<html>\n"
                + " <body>\n"
                + "  <ul class=\"removeMarginPaddingTop\">\n"
                + "   <li id=\"0-13\"><span id=\"2-12\">First item</span></li>\n"
                + "   <li id=\"13-27\"><span id=\"15-26\">Second item</span></li>\n"
                + "   <li id=\"27-84\"><span id=\"29-39\">Third item</span>\n"
                + "    <ul>\n"
                + "     <li id=\"44-62\"><span id=\"46-61\">Indented item 1</span></li>\n"
                + "     <li id=\"66-84\"><span id=\"68-83\">Indented item 2</span></li>\n"
                + "    </ul></li>\n"
                + "   <li id=\"84-97\"><span id=\"86-97\">Fourth item</span></li>\n"
                + "  </ul>\n"
                + " </body>\n"
                + "</html>";

        var given = "- First item\n"
                + "- Second item\n"
                + "- Third item\n"
                + "    - Indented item 1\n"
                + "    - Indented item 2\n"
                + "- Fourth item";
        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

}
