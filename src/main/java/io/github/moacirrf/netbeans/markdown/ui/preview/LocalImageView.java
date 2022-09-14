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

import io.github.moacirrf.netbeans.markdown.TempDir;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import javax.swing.text.Element;
import javax.swing.text.html.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.openide.util.Exceptions;

/**
 * SVG images need to be converted to PNG, this class
 * will do this job, if image its not PNG, JPG or WEBP,
 * it will suppose that is a SVG image and try converts to PNG and save 
 * on temporary directory.
 * 
 * If the convertion fail will try to use the original image.
 * 
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class LocalImageView extends ImageView {

    private final Path tempDir;

    public LocalImageView(Element elem) {
        super(elem);
        tempDir = TempDir.getTempDir();
    }

    @Override
    public Image getImage() {
        Image image = super.getImage();
        return image;
    }

    @Override
    public URL getImageURL() {
        return convertToPNG(super.getImageURL());
    }

    public URL convertToPNG(URL url) {
        try {
            String fileName = new File(url.getFile()).getName().toLowerCase();
            if (fileName.contains("jpg") || fileName.contains("png") || fileName.contains("jpeg") || fileName.contains("webp")) {
                return url;
            }
          
            Path imageTemp = Paths.get(tempDir.toString(), new Date().getTime() + "_" + fileName + ".png");
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
                return imageTemp.toUri().toURL();
            } catch (TranscoderException ex) {
                Exceptions.printStackTrace(ex);
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return url;
    }
}
