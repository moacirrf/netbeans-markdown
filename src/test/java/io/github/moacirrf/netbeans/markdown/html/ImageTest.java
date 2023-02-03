/*
 * Copyright (C) 2023 Moacir da Roza Flores <moacirrf@gmail.com>
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

import static io.github.moacirrf.netbeans.markdown.TempDir.getTempDir;
import static java.io.File.separator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageTest {

    private HtmlBuilder htmlBuilder = HtmlBuilder.getInstance();

    @Test
    public void testResizableImage() {
        var srcExpected = "file:" + getTempDir() + separator + "tux.png";
        var html = "<html>\n"
                + " <body>\n"
                + "  <img src=\"%s\" width=\"300\" height=\"300\" class=\"removeMarginPaddingTop\" />\n"
                + " </body>\n"
                + "</html>";

        var expected = String.format(html, srcExpected);

        var given = "<img src=\"https://mdg.imgix.net/assets/images/tux.png\" width=\"300\" height=\"300\" />";

        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testImage() {
        var srcExpected = "file:" + getTempDir() + separator + "tux.png";

        String html = "<html>\n"
                + " <body>\n"
                + "  <p id=\"0-134\" class=\"removeMarginPaddingTop\"><span id=\"0-24\"><a href=\"https://mdg.imgix.net/assets/images/tux.png\" id=\"0-133\" class=\"removeColorLinkWithImage\"><img src=\"%s\" alt=\"Tux, the Linux mascot\" title=\"Title of image\" id=\"1-87\" /></a></span></p>\n"
                + " </body>\n"
                + "</html>";

        var expected = String.format(html, srcExpected);

        var given = "[![Tux, the Linux mascot](https://mdg.imgix.net/assets/images/tux.png \"Title of image\")](https://mdg.imgix.net/assets/images/tux.png)\n";

        var result = htmlBuilder.build(given);

        assertNotNull(result);
        assertEquals(expected, result);
    }

}
