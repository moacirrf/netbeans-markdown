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
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler.CustomNodeRenderer;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import java.util.Set;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ImageNodeRenderer implements NodeRenderer, CustomNodeRenderer {

    private final ImageNodeHelper imageNodeHelper = new ImageNodeHelper();

    public ImageNodeRenderer(DataHolder options) {
    }

    public ImageNodeRenderer() {
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        return Set.of(
                new NodeRenderingHandler(Image.class, this),
                new NodeRenderingHandler(HtmlBlock.class, this)
        );
    }

    @Override
    public void render(Node node, NodeRendererContext context, HtmlWriter html) {
        if (node instanceof Image) {
            imageNodeHelper.renderImage((Image) node);
        } else if (node instanceof HtmlBlock) {
            imageNodeHelper.renderHtmlBlock((HtmlBlock) node);
        }
        context.delegateRender();
    }

    static NodeRendererFactory factory() {
        return ImageNodeRenderer::new;
    }

}
