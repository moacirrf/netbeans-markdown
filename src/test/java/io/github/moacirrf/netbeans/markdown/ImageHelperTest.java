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
package io.github.moacirrf.netbeans.markdown;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageHelperTest {

    private static final String SVG_HTTP_URL
            = "https://github.com/moacirrf/netbeans-markdown/actions/workflows/maven-publish.yml/badge.svg";

    private static final String PNG_HTTP_URL
            = "https://user-images.githubusercontent.com/950706/190041477-71d5b5fc-d887-4934-810a-0ceb1048c607.png";

        
    @Test
    public void testIsNonSVG() throws MalformedURLException {
        assertFalse(ImageHelper.isNonSVG(new URL(SVG_HTTP_URL)));
        assertTrue(ImageHelper.isNonSVG(new URL(PNG_HTTP_URL)));
    }

    @Test
    public void testIsHttpUrl() throws MalformedURLException {
        assertTrue(ImageHelper.isHttpUrl(SVG_HTTP_URL));
    }

    @Test
    public void testDownloadImage() throws MalformedURLException, URISyntaxException {

        URL url = ImageHelper.downloadImage(new URL(SVG_HTTP_URL));
        var path = Path.of(url.toURI());

        assertNotNull(url);
        assertTrue(Files.exists(path));

        path.toFile().delete();
    }

    @Test
    public void testConvertSVGToPNG() throws MalformedURLException, URISyntaxException {
        URL url = ImageHelper.downloadImage(new URL(SVG_HTTP_URL));
        URL urlPng = ImageHelper.convertSVGToPNG(url);

        File file = new File(urlPng.getFile());

        assertNotNull(urlPng);
        assertNotNull(file);
        assertTrue(file.exists());

        file.delete();
    }

}
