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

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class LocalViewFactory extends HTMLEditorKit.HTMLFactory {

    @Override
    public View create(Element elem) {
        if (isTagImg(elem)) {
            return new LocalImageView(elem);
        }
        return super.create(elem);
    }

    public boolean isTagImg(Element elem) {

        AttributeSet attrs = elem.getAttributes();
        Object elementName
                = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
        Object o = (elementName != null)
                ? null : attrs.getAttribute(StyleConstants.NameAttribute);
        HTML.Tag kind = (HTML.Tag) o;

        return (o instanceof HTML.Tag) && (kind == HTML.Tag.IMG);

    }
}
