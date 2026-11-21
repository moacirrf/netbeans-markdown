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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Offline tests for image type detection and extension handling.
 */
public class ImageHelperExtensionTest {

    private static final byte[] PNG = bytes(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
    private static final byte[] JPG = bytes(0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10);
    private static final byte[] GIF = "GIF89a....".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] WEBP = "RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] SVG = "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>".getBytes(StandardCharsets.UTF_8);
    private static final byte[] SVG_XML = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!-- comment -->\n"
            + "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>").getBytes(StandardCharsets.UTF_8);
    private static final byte[] UNKNOWN = "just some text".getBytes(StandardCharsets.UTF_8);

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }

    @Test
    public void testDetectExtension() {
        assertEquals("png", ImageHelper.detectExtension(PNG));
        assertEquals("jpg", ImageHelper.detectExtension(JPG));
        assertEquals("gif", ImageHelper.detectExtension(GIF));
        assertEquals("webp", ImageHelper.detectExtension(WEBP));
        assertEquals("svg", ImageHelper.detectExtension(SVG));
        assertEquals("svg", ImageHelper.detectExtension(SVG_XML));
        assertEquals("", ImageHelper.detectExtension(UNKNOWN));
        assertEquals("", ImageHelper.detectExtension(new byte[0]));
        assertEquals("", ImageHelper.detectExtension(new byte[]{1}));
        assertEquals("", ImageHelper.detectExtension(null));
    }

    @Test
    public void testExtensionFromContentType() {
        assertEquals("png", ImageHelper.extensionFromContentType("image/png"));
        assertEquals("jpg", ImageHelper.extensionFromContentType("image/jpeg"));
        assertEquals("svg", ImageHelper.extensionFromContentType("image/svg+xml; charset=utf-8"));
        assertEquals("", ImageHelper.extensionFromContentType("text/html"));
        assertEquals("", ImageHelper.extensionFromContentType(null));
    }

    @Test
    public void testHasImageExtension() {
        assertTrue(ImageHelper.hasImageExtension("a.png"));
        assertTrue(ImageHelper.hasImageExtension("a.JPEG"));
        assertFalse(ImageHelper.hasImageExtension("badge"));
        assertFalse(ImageHelper.hasImageExtension("mypng"));
        assertFalse(ImageHelper.hasImageExtension("image.php"));
        assertFalse(ImageHelper.hasImageExtension("image."));
    }

    @Test
    public void testEnsureExtension() {
        assertEquals("badge.svg", ImageHelper.ensureExtension("badge", SVG, null));
        assertEquals("photo.png", ImageHelper.ensureExtension("photo.png", JPG, null));
        assertEquals("photo.PNG", ImageHelper.ensureExtension("photo.PNG", PNG, null));
        assertEquals("image.php.png", ImageHelper.ensureExtension("image.php", PNG, null));
        assertEquals("image.gif", ImageHelper.ensureExtension("image", UNKNOWN, "image/gif"));
        assertEquals("unknown", ImageHelper.ensureExtension("unknown", UNKNOWN, null));
    }

    @Test
    public void testBuildFileName() throws Exception {
        assertEquals("build-passing.svg",
                ImageHelper.buildFileName(new URL("https://img.shields.io/badge/build-passing"), SVG, null));
        assertEquals("photo.jpg",
                ImageHelper.buildFileName(new URL("https://example.com/photo?id=1&s=2"), JPG, null));
        assertEquals("logo.png",
                ImageHelper.buildFileName(new URL("https://example.com/logo.png?v=3"), PNG, null));
        String name = ImageHelper.buildFileName(new URL("https://example.com/"), PNG, null);
        assertTrue(name, name.startsWith("image_") && name.endsWith(".png"));
    }
}
