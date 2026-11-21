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

import java.io.ByteArrayInputStream;
import java.io.File;
import static java.io.File.separator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import java.security.InvalidParameterException;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ImageHelper {

    private static final List<String> EXTENSION_IMAGES = asList("jpg", "png", "jpeg", "webp", "gif", "svg", "bmp", "ico");

    /**
     * Number of bytes read to discover the image type.
     */
    private static final int HEADER_SIZE = 4096;

    private static final Map<String, URL> CACHE_CONVERTED_IMAGES = new HashMap<>();

    private static final Map<String, URL> CACHE_DOWNLOADED_IMAGES = new java.util.concurrent.ConcurrentHashMap<>();

    public static boolean isSVG(URL url) {
        if (url == null) {
            return false;
        }
        String fileName = url.getFile().toLowerCase();
        return getImageType(url).contains("svg") || fileName.endsWith("svg");
    }

    public static boolean isImage(String url) {
        try {
            for (String ext : EXTENSION_IMAGES) {
                if (url.toLowerCase().endsWith(ext)) {
                    return true;
                }
            }
            URL target = isHttpUrl(url)
                    ? URI.create(url).toURL()
                    : getLocalImage(removeParameters(url)).toURI().toURL();
            String type = getImageType(target);
            for (String ext : EXTENSION_IMAGES) {
                if (type.toLowerCase().contains(ext)) {
                    return true;
                }
            }
        } catch (MalformedURLException | IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    /**
     * Discover the image type reading the first bytes of the url content.
     *
     * @param url Url of image
     * @return The format name in lower case (ex.: png, jpeg, gif, webp, svg) or
     * empty string if not discovered.
     */
    public static String getImageType(URL url) {
        if (url == null) {
            return "";
        }
        try (InputStream in = url.openStream()) {
            byte[] head = in.readNBytes(HEADER_SIZE);
            String ext = detectExtension(head);
            if (!ext.isEmpty()) {
                return "jpg".equals(ext) ? "jpeg" : ext;
            }
            return getImageTypeByImageIO(head);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    private static String getImageTypeByImageIO(byte[] bytes) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (iis != null) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                if (readers != null && readers.hasNext()) {
                    return readers.next().getFormatName().toLowerCase();
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    /**
     * Detect the image extension based on magic bytes (file signature).
     *
     * @param bytes The image content (or at least the first bytes of it)
     * @return png, jpg, gif, webp, bmp, ico, svg or empty string when the
     * format is unknown.
     */
    public static String detectExtension(byte[] bytes) {
        if (bytes == null || bytes.length < 2) {
            return "";
        }
        if (startsWith(bytes, 0x89, 'P', 'N', 'G')) {
            return "png";
        }
        if (startsWith(bytes, 0xFF, 0xD8, 0xFF)) {
            return "jpg";
        }
        if (startsWith(bytes, 'G', 'I', 'F', '8')) {
            return "gif";
        }
        if (bytes.length >= 12 && startsWith(bytes, 'R', 'I', 'F', 'F')
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "webp";
        }
        if (startsWith(bytes, 0x00, 0x00, 0x01, 0x00)) {
            return "ico";
        }
        if (startsWith(bytes, 'B', 'M')) {
            return "bmp";
        }
        String head = new String(bytes, 0, Math.min(bytes.length, HEADER_SIZE), StandardCharsets.ISO_8859_1)
                .toLowerCase();
        if (head.contains("<svg")) {
            return "svg";
        }
        return "";
    }

    private static boolean startsWith(byte[] bytes, int... signature) {
        if (bytes.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((bytes[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert a http Content-Type to a image extension.
     *
     * @param contentType Ex.: image/png, image/svg+xml; charset=utf-8
     * @return The extension or empty string if unknown.
     */
    public static String extensionFromContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "";
        }
        String type = contentType.split(";")[0].trim().toLowerCase();
        switch (type) {
            case "image/png":
                return "png";
            case "image/jpeg":
            case "image/jpg":
            case "image/pjpeg":
                return "jpg";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            case "image/svg+xml":
                return "svg";
            case "image/bmp":
            case "image/x-ms-bmp":
                return "bmp";
            case "image/x-icon":
            case "image/vnd.microsoft.icon":
                return "ico";
            default:
                return "";
        }
    }

    /**
     * Test if the file name ends with a known image extension.
     *
     * @param fileName The file name
     * @return true if has a known image extension
     */
    public static boolean hasImageExtension(String fileName) {
        if (fileName == null) {
            return false;
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return false;
        }
        return EXTENSION_IMAGES.contains(fileName.substring(index + 1).toLowerCase());
    }

    /**
     * If the file name has no known image extension, discover the image type
     * and concatenate the extension. Ex.: badge -> badge.svg, image.php ->
     * image.php.png. When the type cannot be discovered the name is returned
     * unchanged.
     *
     * @param fileName The file name
     * @param bytes Image content, used to detect the type by magic bytes
     * @param contentType Optional http Content-Type, used as fallback
     * @return The file name with extension
     */
    public static String ensureExtension(String fileName, byte[] bytes, String contentType) {
        if (fileName == null || hasImageExtension(fileName)) {
            return fileName;
        }
        String ext = detectExtension(bytes);
        if (ext.isEmpty()) {
            ext = extensionFromContentType(contentType);
        }
        return ext.isEmpty() ? fileName : fileName + "." + ext;
    }

    public static boolean isHttpUrl(String url) {
        return url.toLowerCase().contains("http");
    }

    /**
     * Download image from internet, if file exists with same name but with
     * different hash a new file will be create with number concatenated ex.:
     * 0_java.png.
     *
     * @param url
     * @return
     */
    public static URL downloadImage(URL url) {

        if (!isHttpUrl(url.toString())) {
            throw new InvalidParameterException("Url must be http");
        }
        String cacheKey = url.toString();
        URL cached = CACHE_DOWNLOADED_IMAGES.get(cacheKey);
        if (cached != null && isCachedFileExisting(cached)) {
            return cached;
        }
        URL returnUrl = null;
        boolean downloaded = false;
        try {
            HttpRequest request = HttpRequest.newBuilder(url.toURI())
                    .GET()
                    .build();

            HttpClient http = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpResponse<byte[]> response = http.send(request, ofByteArray());
            byte[] bytes = response.body();

            if (bytes == null || bytes.length < 1000) {
                return TempDir.getCantLoadImage().toUri().toURL();
            }

            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            String fileName = buildFileName(url, bytes, contentType);
            Path tempDir = TempDir.getTempDir();

            Path file = Path.of(tempDir.toString(), fileName);
            int contRepeatImage = 0;
            while (Files.exists(file) && !fileExistsByHash(file, bytes)) {
                file = Path.of(tempDir.toString(), (contRepeatImage++) + "_" + fileName);
            }
            Files.write(file, bytes, CREATE, TRUNCATE_EXISTING);
            returnUrl = file.toUri().toURL();
            downloaded = true;
        } catch (NoSuchFileException ex) {
            Exceptions.printStackTrace(ex);
        } catch (URISyntaxException | InterruptedException | IOException ex) {
            Exceptions.printStackTrace(ex);
            if (ex instanceof InterruptedException && Thread.currentThread() != null) {
                Thread.currentThread().interrupt();
            }
        }
        if (downloaded) {
            CACHE_DOWNLOADED_IMAGES.put(cacheKey, returnUrl);
        }
//        getImageType(returnUrl);
        if (returnUrl == null) {
            try {
                return TempDir.getCantLoadImage().toUri().toURL();
            } catch (MalformedURLException ex) {
                return null;
            }
        }
        return returnUrl;
    }

    /**
     * Build a valid local file name to a downloaded image: takes the last
     * segment of url path, removes parameters, replaces invalid chars and
     * ensures a image extension.
     *
     * @param url Original url
     * @param bytes Downloaded content
     * @param contentType Http Content-Type, can be null
     * @return File name with extension (when discoverable)
     */
    static String buildFileName(URL url, byte[] bytes, String contentType) {
        String path = url.getPath() == null ? "" : url.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        name = removeParameters(name);
        name = name == null ? "" : name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (name.isEmpty() || ".".equals(name) || "..".equals(name)) {
            name = "image_" + DigestUtils.md5Hex(bytes).substring(0, 12);
        }
        return ensureExtension(name, bytes, contentType);
    }

    /**
     * Convert SVG to png
     *
     * @param url
     * @return Return a new png url referencing a existing file
     */
    public static URL convertSVGToPNG(URL url) {
        if (url == null) {
            return null;
        }
        if (isSVG(url)) {
            try {
                String fileName = new File(url.getFile()).getName().toLowerCase();

                Path imageTemp = Paths.get(TempDir.getTempDir().toString(), fileName + ".png");
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

        return getNotLoadedImage().toFile();
    }

    public static Path getNotLoadedImage() {
        return TempDir.getCantLoadImage();
    }

    private ImageHelper() {
    }

    /**
     * Test if a file is the same based on md5Hex, compare Path with the byte
     * array
     *
     * @param path File existing on disk to compare with a byte array.
     * @param bytes Byte arrays to compare to path
     * @return true if file exists(is the same), false if not
     */
    public static void clearCache() {
        CACHE_DOWNLOADED_IMAGES.clear();
        CACHE_CONVERTED_IMAGES.clear();
    }

    private static boolean isCachedFileExisting(URL url) {
        try {
            return "file".equals(url.getProtocol()) && Files.exists(Path.of(url.toURI()));
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    public static boolean fileExistsByHash(Path path, byte[] bytes) {
        try {
            if (Files.exists(path)) {
                String hash = DigestUtils.md5Hex(Files.readAllBytes(path));

                return hash.equals(DigestUtils.md5Hex(bytes));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    public static String removeParameters(String src) {
        if (src == null || src.isBlank()) {
            return src;
        }
        int index = src.indexOf("?");
        if (index == -1) {
            index = src.indexOf("%3F");
        }
        if (index != -1) {
            return src.replace(src.substring(index, src.length()), "");
        }
        return src;
    }
}
