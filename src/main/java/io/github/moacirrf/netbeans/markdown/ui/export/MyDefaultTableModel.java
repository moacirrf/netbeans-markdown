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
package io.github.moacirrf.netbeans.markdown.ui.export;

import io.github.moacirrf.netbeans.markdown.export.InputModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class MyDefaultTableModel extends DefaultTableModel {

    private static final Class[] TYPES = new Class[]{
        JPanel.class, String.class, String.class
    };

    private static final boolean[] CAN_EDIT = new boolean[]{true, false, false};

    public MyDefaultTableModel(List<File> data) {
        super(new Object[][]{}, new String[]{"Order", "Name", "Path"});
        int row = 0;
        setRowCount(data.size());
        for (File file : data) {
            setValueAt(file.getName(), row, 1);
            setValueAt(file.getPath(), row, 2);
            row++;
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return TYPES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return CAN_EDIT[columnIndex];
    }

    public List<InputModel> toInputModel() {
        var lista = new ArrayList<InputModel>();
        int count = this.getRowCount();
        for (int index = 0; index <= count - 1; index++) {
            var file = new File((String) this.getValueAt(index, 2));
            lista.add(InputModel.from(file, index));
        }
        return lista;
    }    
}
