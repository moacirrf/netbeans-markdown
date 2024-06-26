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

import io.github.moacirrf.netbeans.markdown.ui.preview.image.ImageLabel;
import java.awt.Component;
import javax.swing.JEditorPane;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;

public class ImageView extends ComponentView {

    private JEditorPane editorPane;

    public ImageView(Element elem, JEditorPane editorPane) {
        super(elem);
        this.editorPane = editorPane;
    }

    @Override
    protected Component createComponent() {
        return new ImageLabel(getElement(), editorPane);
    }

    @Override
    public float getAlignment(int axis) {
        return 1;
    }   
}