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

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.text.Element;
import javax.swing.text.html.FormView;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class CheckboxView extends FormView {

    public CheckboxView(Element elem) {
        super(elem);
    }

    @Override
    protected Component createComponent() {
        Component component = super.createComponent();
        if (component instanceof JCheckBox) {
            JCheckBox c = (JCheckBox) component;
            c.setBorder(BorderFactory.createEmptyBorder(0, 0, -4, 0));
            c.addActionListener((ActionEvent e) -> c.setSelected(!c.isSelected()));
        }
        return component;
    }

}
