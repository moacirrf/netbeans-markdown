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
package io.github.moacirrf.netbeans.markdown.ui.preview;

import io.github.moacirrf.netbeans.markdown.Context;
import io.github.moacirrf.netbeans.markdown.TempDir;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ofPattern;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 * SVG images need to be converted to PNG, this class will do this job, if image
 * its not PNG, JPG or WEBP, it will suppose that is a SVG image and try
 * converts to PNG and save on temporary directory.
 *
 * If the convertion fail will try to use the original image.
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageViewImpl extends ImageView {

    private final Path tempDir;

    private static final Map<String, URL> CACHE_CONVERTED_IMAGES = new HashMap<>();

    public ImageViewImpl(Element elem) {
        super(elem);
        tempDir = TempDir.getTempDir();
    }

    @Override
    public URL getImageURL() {
        String src = (String) getElement().getAttributes().
                getAttribute(HTML.Attribute.SRC);
        if (src == null) {
            return null;
        }
        URL url = null;
        try {
            URL reference = ((HTMLDocument) getDocument()).getBase();
            url = new URL(reference, src);
        } catch (MalformedURLException e) {
            try {
                File f = getLocalImage(src);
                if (f == null) {
                    return null;
                }
                if (f.exists() && !f.isDirectory()) {
                    url = Utilities.toURI(f).toURL();
                }
            } catch (MalformedURLException ex) {
                return null;
            }
        }

        if (url == null) {
            return null;
        }

        String fileName = new File(url.getFile()).getName().toLowerCase();
        if (fileName.contains("jpg")
                || fileName.contains("png")
                || fileName.contains("jpeg")
                || fileName.contains("webp")
                || fileName.contains("gif")) {
            return url;
        }

        return convertToPNG(url);
    }

    /**
     * Try to find image save on disk, with relative and absolute paths
     *
     * @param src The content src property of image
     * @return The file
     */
    private File getLocalImage(String src) {
        File f = new File(src.trim());
        if (!f.exists() && Context.OPENED_FILE != null) {
            f = new File(Context.OPENED_FILE.getParent().getPath(), File.separator + src.trim());
        }
        if (f.exists()) {
            return f;
        }
        return null;
    }

    public URL convertToPNG(URL url) {
        if (url == null) {
            return null;
        }
        try {
            String fileName = new File(url.getFile()).getName().toLowerCase();

            Path imageTemp = Paths.get(tempDir.toString(), fileName + ".png");
            imageTemp.toFile().setWritable(true);
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(url.toString());
            try {
                // I don't know why, but "try with resources are not working", so don't use.
                var ostream = new FileOutputStream(imageTemp.toFile());
                TranscoderOutput output = new TranscoderOutput(ostream);
                // Save the image to temp.
                t.transcode(input, output);
                ostream.flush();
                ostream.close();
                CACHE_CONVERTED_IMAGES.put(url.toString(), imageTemp.toUri().toURL());
                return CACHE_CONVERTED_IMAGES.get(url.toString());
            } catch (TranscoderException ex) {
                Exceptions.printStackTrace(ex);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return url;
    }
}
