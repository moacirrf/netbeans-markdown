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

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;

/**
 * This class will do the following: 1- Download all images locally 2-convert
 * SVG to PNG.
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageRendererExtension implements HtmlRenderer.HtmlRendererExtension {

    @Override
    public void rendererOptions(MutableDataHolder options) {
    }

    @Override
    public void extend(HtmlRenderer.Builder builder, String rendererType) {
        builder.
                nodeRendererFactory(ImageNodeRenderer.factory());
    }

    public static ImageRendererExtension create() {
        return new ImageRendererExtension();
    }

}
