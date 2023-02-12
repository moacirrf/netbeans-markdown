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

import static io.github.moacirrf.netbeans.markdown.ImageHelper.getLocalImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.ImageView;
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

    public ImageViewImpl(Element elem) {
        super(elem);
    }

    @Override
    public URL getImageURL() {
        return super.getImageURL();
//        String src = (String) getElement().getAttributes().
//                getAttribute(HTML.Attribute.SRC);
//        if (src == null) {
//            return null;
//        }
//        URL url = null;
//        try {
//            File f = getLocalImage(src);
//            if (f == null) {
//                return null;
//            }
//            if (f.exists() && !f.isDirectory()) {
//                url = Utilities.toURI(f).toURL();
//            }
//        } catch (MalformedURLException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        return url;
    }
}
