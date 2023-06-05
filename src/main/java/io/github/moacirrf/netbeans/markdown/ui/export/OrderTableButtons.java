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

import io.github.moacirrf.netbeans.markdown.Icons;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class OrderTableButtons extends javax.swing.JPanel {

    /**
     * Creates new form OrderTableButtons
     */
    public OrderTableButtons(JTable table, int row) {
        initComponents();
        this.downBtn.setIcon(Icons.getICON_EXPORT_DOWN());
        this.upBtn.setIcon(Icons.getICON_EXPORT_UP());
        
        
        MyDefaultTableModel model = (MyDefaultTableModel) table.getModel();
        MyOrderTableCellEditor editor = (MyOrderTableCellEditor) table.getDefaultEditor(JPanel.class);
        this.upBtn.setEnabled(row > 0);
        this.downBtn.setEnabled(row < (model.getRowCount() - 1));

        this.upBtn.addActionListener(event -> {
            int endRow = row - 1;
            model.moveRow(row, row, endRow);
            table.getSelectionModel().setSelectionInterval(endRow, endRow);
            editor.stopCellEditing();
        });

        this.downBtn.addActionListener(event -> {
            int endRow = row + 1;
            model.moveRow(row, row, endRow);
            table.getSelectionModel().setSelectionInterval(endRow, endRow);
            editor.stopCellEditing();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        upBtn = new javax.swing.JButton();
        downBtn = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(44, 15));
        setOpaque(false);
        setLayout(new java.awt.GridLayout(0, 2));

        upBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/moacirrf/netbeans/markdown/anchor_n.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(upBtn, org.openide.util.NbBundle.getMessage(OrderTableButtons.class, "OrderTableButtons.upBtn.text")); // NOI18N
        add(upBtn);
        upBtn.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(OrderTableButtons.class, "OrderTableButtons.upBtn.AccessibleContext.accessibleName")); // NOI18N

        downBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/moacirrf/netbeans/markdown/anchor_s.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(downBtn, org.openide.util.NbBundle.getMessage(OrderTableButtons.class, "OrderTableButtons.downBtn.text")); // NOI18N
        downBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        add(downBtn);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downBtn;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton upBtn;
    // End of variables declaration//GEN-END:variables

}
