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
package io.github.moacirrf.netbeans.markdown.html.flexmark;

import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.ast.HtmlInline;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import io.github.moacirrf.netbeans.markdown.ImageHelper;
import static io.github.moacirrf.netbeans.markdown.ImageHelper.isNonSVG;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 * THis class is used to the follow: 1- Download all images to local folder 2-
 * Convert images from svg to png
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageNodeHelper {

    private static final String IMG_TAG = "img";
    public static final String SRC_ATTR = "src";
    public static final String WIDTH_ATTR = "width";
    public static final String HEIGHT_ATTR = "height";

    public void renderImage(Image image) {
        if (image.getUrl() != null && ImageHelper.isImage(image.getUrl().toString())) {
            URL url = buildURL(image.getUrl().toString());
            if (url != null) {
                image.setUrl(BasedSequence.of(url.toString()));
            }
        }
    }

    public boolean isImageNode(Node node) {
        var tagImageContent = node.getChars().toString();
        return tagImageContent.toLowerCase().contains(IMG_TAG);
    }

    /**
     * A tag img on a md file is regognized as a HtmlBlock or HtmlInline node and not a Image
     * node. so this method will include image attributes to a tag
     *
     * @param node
     * @param context
     */
    public void renderHtmlImageNode(Node node) {

        var tagImageContent = node.getChars().toString();
        var element = Jsoup.parseBodyFragment(tagImageContent).body().firstChild();
        if (element != null) {
            URL url = buildURL(element.attr(SRC_ATTR));
            if (url != null) {
                String urlString = url.toString();
                element.attr(SRC_ATTR, urlString);
                if (urlString.contains(ImageHelper.getNotLoadedImage().getFileName().toString())) {
                    element.removeAttr(WIDTH_ATTR);
                    element.removeAttr(HEIGHT_ATTR);
                }
                if (isImageNode(node) && node instanceof HtmlBlock) {
                    ((HtmlBlock) node).setContent(List.of(BasedSequence.of(element.outerHtml())));
                }
                node.setChars(BasedSequence.of(element.outerHtml()));
            }
        }

    }

    public String extractAttribute(Node htmlBlock, String attr) {
        var tagImageContent = htmlBlock.getChars().toString();
        var element = Jsoup.parseBodyFragment(tagImageContent).body().firstChild();
        if (element != null) {
            return element.attr(attr);
        }
        return null;
    }

    private URL buildURL(String url) {
        URL resultUrl = null;
        try {
            if (ImageHelper.isHttpUrl(url)) {
                resultUrl = new URL(url);
                if (!isNonSVG(resultUrl)) {
                    resultUrl = ImageHelper.convertSVGToPNG(resultUrl);
                } else {
                    resultUrl = ImageHelper.downloadImage(resultUrl);
                }
            } else {
                File localFile = ImageHelper.getLocalImage(url);
                if (localFile != null) {
                    resultUrl = Utilities.toURI(localFile).toURL();
                    if (!ImageHelper.isNonSVG(resultUrl)) {
                        resultUrl = ImageHelper.convertSVGToPNG(resultUrl);
                    }
                }
            }
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return resultUrl;
    }
}
