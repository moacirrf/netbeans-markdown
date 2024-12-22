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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageHelperTest {

    private static final String SVG_HTTP_URL
            = "https://github.com/moacirrf/netbeans-markdown/actions/workflows/maven-publish.yml/badge.svg";

    private static final String PNG_HTTP_URL
            = "https://raw.githubusercontent.com/moacirrf/netbeans-markdown/main/src/main/resources/io/github/moacirrf/netbeans/markdown/code_template.png";

    private MockedStatic<TempDir> mockedStatic;

    @Before
    public void setup() {
        mockedStatic = mockStatic(TempDir.class);
    }

    @After
    public void tearDown() {
        mockedStatic.close();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testIsSVG() throws MalformedURLException {
        assertTrue(ImageHelper.isSVG(new URL(SVG_HTTP_URL)));
        assertFalse(ImageHelper.isSVG(new URL(PNG_HTTP_URL)));
    }

    @Test
    public void testIsHttpUrl() throws MalformedURLException {
        assertTrue(ImageHelper.isHttpUrl(SVG_HTTP_URL));
    }

    @Test
    public void testDownloadImage() throws MalformedURLException, URISyntaxException {
        mockedStatic.when(() -> TempDir.getTempDir()).thenReturn(TempDirTest.getTempDir());
        mockedStatic.when(() -> TempDir.getCantLoadImage()).thenReturn(TempDirTest.getCantLoadImage());
        
        URL url = ImageHelper.downloadImage(new URL(SVG_HTTP_URL));
        var path = Path.of(url.toURI());

        assertNotNull(url);
        assertTrue(Files.exists(path));

        path.toFile().delete();
        TempDirTest.removeTempDir();
    }

    @Test
    public void testConvertSVGToPNG() throws MalformedURLException, URISyntaxException {
        mockedStatic.when(() -> TempDir.getTempDir()).thenReturn(TempDirTest.getTempDir());
        mockedStatic.when(() -> TempDir.getCantLoadImage()).thenReturn(TempDirTest.getCantLoadImage());

        URL url = ImageHelper.downloadImage(new URL(SVG_HTTP_URL));
        URL urlPng = ImageHelper.convertSVGToPNG(url);

        File file = new File(urlPng.getFile());

        assertNotNull(urlPng);
        assertNotNull(file);
        assertTrue(file.exists());

        file.delete();
        TempDirTest.removeTempDir();
    }

    @Test
    public void testGetImageType() {
        try {
            assertEquals(ImageHelper.getImageType(URI.create(SVG_HTTP_URL).toURL()), "svg");;
            assertEquals(ImageHelper.getImageType(URI.create(PNG_HTTP_URL).toURL()), "png");;
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFileExistsByHash() {
        mockedStatic.when(() -> TempDir.getTempDir()).thenReturn(TempDirTest.getTempDir());
        mockedStatic.when(() -> TempDir.getCantLoadImage()).thenReturn(TempDirTest.getCantLoadImage());
        try {
            Path path = folder.newFile("teste.png").toPath();
            byte[] bytes = URI.create(PNG_HTTP_URL).toURL().openStream().readAllBytes();
            Files.write(path, bytes, TRUNCATE_EXISTING, WRITE);
            assertTrue(Files.exists(path));
            assertTrue(ImageHelper.fileExistsByHash(path, bytes));
            Files.delete(path);

            folder.delete();
            TempDirTest.removeTempDir();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
