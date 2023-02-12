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
import static java.io.File.separator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import java.security.InvalidParameterException;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.jetbrains.annotations.NotNull;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ImageHelper {

    private static final List<String> EXTENSION_IMAGES = asList("jpg", "png", "jpeg", "webp", "gif", "svg");

    private static final Path TEMP_DIR = TempDir.getTempDir();

    public static final Path NOT_LOADED_IMAGE = Path.of(TEMP_DIR.toString(), "not_load_image.png");

    private static final Map<String, URL> CACHE_CONVERTED_IMAGES = new HashMap<>();

    public static boolean isNonSVG(URL url) {
        String fileName = url.getFile().toLowerCase();
        return (fileName.contains("jpg")
                || fileName.contains("png")
                || fileName.contains("jpeg")
                || fileName.contains("webp")
                || fileName.contains("gif"));

    }

    public static boolean isImage(String url) {
        for (String ext : EXTENSION_IMAGES) {
            if (url.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHttpUrl(String url) {
        return url.toLowerCase().contains("http");
    }

    public static URL downloadImage(URL url) {

        if (!isHttpUrl(url.toString())) {
            throw new InvalidParameterException("Url must be http");
        }
        URL returnUrl = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(url.toURI()).GET().build();
            HttpClient http = HttpClient.newHttpClient();

            byte[] bytes = http.send(request, ofByteArray())
                    .body();

            Path file = Path.of(TEMP_DIR.toString(), Path.of(url.getFile()).getFileName().toString());
            if (file != null) {
                Files.write(file, bytes, CREATE, TRUNCATE_EXISTING);
                returnUrl = file.toUri().toURL();
            }

        } catch (URISyntaxException | InterruptedException | IOException ex) {
            Exceptions.printStackTrace(ex);
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return returnUrl;
    }

    public static URL convertSVGToPNG(URL url) {
        if (url == null) {
            return null;
        }
        try {
            String fileName = new File(url.getFile()).getName().toLowerCase();

            Path imageTemp = Paths.get(TEMP_DIR.toString(), fileName + ".png");
            imageTemp.toFile().setWritable(true);

            var t = new PNGTranscoder();
            var input = new TranscoderInput(url.toString());
            // I don't know why, but "try with resources are not working", so don't use.
            var ostream = new FileOutputStream(imageTemp.toFile());
            TranscoderOutput output = new TranscoderOutput(ostream);
            // Save the image to temp.
            t.transcode(input, output);
            ostream.flush();
            ostream.close();
            CACHE_CONVERTED_IMAGES.put(url.toString(), imageTemp.toUri().toURL());
            return CACHE_CONVERTED_IMAGES.get(url.toString());

        } catch (TranscoderException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return url;
    }

    /**
     * Try to find image save on disk, with relative and absolute paths
     *
     * @param src The content src property of image
     * @return The file
     */
    @NotNull
    public static File getLocalImage(String src) {
        File file = null;
        try {
            URL url = new URL(src);
            file = new File(url.getFile());
        } catch (MalformedURLException ex) {
            file = new File(src);
        }

        if (file != null && !file.exists() && Context.OPENED_FILE != null) {
            file = new File(Context.OPENED_FILE.getParent().getPath(), separator + file.getPath().replace("./", ""));
        }
        if (file != null && file.exists()) {
            return file;
        }

        return NOT_LOADED_IMAGE.toFile();
    }

    private ImageHelper() {
    }
}
