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
package io.github.moacirrf.netbeans.markdown.ui;

import io.github.moacirrf.netbeans.markdown.Icons;
import java.awt.Graphics;
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;

public class TopBar extends javax.swing.JPanel {

    private final SplitPanel splitPanel;

    /**
     * Force recalculation of position of divider, align to center
     */
    private boolean refreshDividerLocation;
    private boolean splitModeOn;

    /**
     * Creates new form TopBar
     */
    public TopBar(SplitPanel splitPanel) {
        this.splitPanel = splitPanel;
        initComponents();
        buttonGroup1.add(showSourceBtn);
        buttonGroup1.add(splitModeBtn);
        buttonGroup1.add(showPreviewBtn);
        showSourceBtn.setIcon(Icons.getICON_SOURCE());
        showPreviewBtn.setIcon(Icons.getICON_PREVIEW());
        split();
        splitPanel.getSplitPanel().setOrientation(HORIZONTAL_SPLIT);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        showSourceBtn = new javax.swing.JToggleButton();
        splitModeBtn = new javax.swing.JToggleButton();
        showPreviewBtn = new javax.swing.JToggleButton();

        showSourceBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/moacirrf/netbeans/markdown/icon_source.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showSourceBtn, org.openide.util.NbBundle.getMessage(TopBar.class, "TopBar.showSourceBtn.text")); // NOI18N
        showSourceBtn.setToolTipText(org.openide.util.NbBundle.getMessage(TopBar.class, "TopBar.showSourceBtn.toolTipText")); // NOI18N
        showSourceBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSourceBtnActionPerformed(evt);
            }
        });

        splitModeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/moacirrf/netbeans/markdown/icon_horizontal_split.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(splitModeBtn, org.openide.util.NbBundle.getMessage(TopBar.class, "TopBar.splitModeBtn.text")); // NOI18N
        splitModeBtn.setToolTipText(org.openide.util.NbBundle.getMessage(TopBar.class, "TopBar.splitModeBtn.toolTipText")); // NOI18N
        splitModeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                splitModeBtnActionPerformed(evt);
            }
        });

        showPreviewBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/io/github/moacirrf/netbeans/markdown/icon_preview.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showPreviewBtn, org.openide.util.NbBundle.getMessage(TopBar.class, "TopBar.showPreviewBtn.text")); // NOI18N
        showPreviewBtn.setToolTipText(org.openide.util.NbBundle.getMessage(TopBar.class, "TopBar.showPreviewBtn.toolTipText")); // NOI18N
        showPreviewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPreviewBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showSourceBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitModeBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPreviewBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showSourceBtn)
                    .addComponent(splitModeBtn)
                    .addComponent(showPreviewBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showSourceBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSourceBtnActionPerformed
        this.onlySource();
    }//GEN-LAST:event_showSourceBtnActionPerformed

    private void splitModeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_splitModeBtnActionPerformed
        this.split();
    }//GEN-LAST:event_splitModeBtnActionPerformed

    private void showPreviewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPreviewBtnActionPerformed
        this.onlyPreview();
    }//GEN-LAST:event_showPreviewBtnActionPerformed

    private void onlySource() {
        splitPanel.getSplitPanel().getLeftComponent().setVisible(true);
        splitPanel.getSplitPanel().getRightComponent().setVisible(false);
        splitModeOn = false;
    }

    private void split() {
        splitModeBtn.setSelected(true);
        splitPanel.getSplitPanel().getLeftComponent().setVisible(true);
        splitPanel.getSplitPanel().getRightComponent().setVisible(true);
        if (splitModeOn) {
            switch (splitPanel.getSplitPanel().getOrientation()) {
                case HORIZONTAL_SPLIT:
                    splitPanel.getSplitPanel().setOrientation(VERTICAL_SPLIT);
                    this.splitModeBtn.setIcon(Icons.getICON_VERTICAL_SPLIT());
                    break;
                default:
                    splitPanel.getSplitPanel().setOrientation(HORIZONTAL_SPLIT);
                    this.splitModeBtn.setIcon(Icons.getICON_HORIZONTAL_SPLIT());
            }
        }
        splitModeOn = true;
        refreshDividerLocation = true;
    }

    private void onlyPreview() {
        splitPanel.getSplitPanel().getLeftComponent().setVisible(false);
        splitPanel.getSplitPanel().getRightComponent().setVisible(true);
        splitModeOn = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (refreshDividerLocation && splitModeOn) {
            splitPanel.getSplitPanel().setDividerLocation(0.5);
            refreshDividerLocation = false;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JToggleButton showPreviewBtn;
    private javax.swing.JToggleButton showSourceBtn;
    private javax.swing.JToggleButton splitModeBtn;
    // End of variables declaration//GEN-END:variables
}
