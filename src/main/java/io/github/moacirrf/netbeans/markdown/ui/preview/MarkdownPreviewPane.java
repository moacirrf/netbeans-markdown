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

import io.github.moacirrf.netbeans.markdown.html.HtmlBuilder;
import java.awt.CardLayout;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;

public class MarkdownPreviewPane extends JPanel {

    private JEditorPane editorPane;

    private JScrollPane scrollPane;

    private JPanel progressPanel;

    private transient FileObject fileObject;

    public MarkdownPreviewPane() {
        this.initComponents();
        this.setBorder(BorderFactory.createEmptyBorder(22, 0, 9, 0));
        editorPane.setBorder(BorderFactory.createEmptyBorder());
        editorPane.setFocusable(true);
        editorPane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getInputEvent() instanceof MouseEvent && ACTIVATED.equals(e.getEventType())) {
                HtmlBrowser.URLDisplayer.getDefault().showURL(e.getURL());
            }
        });
    }

    private void initComponents() {
        var layout = new CardLayout();
        setLayout(layout);

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setEditorKit(new MarkdownEditorKit());

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(editorPane);
        createProgressBar();
        add(progressPanel);
        add(scrollPane);

        scrollPane.setVisible(false);
        progressPanel.setVisible(true);

    }

    public void setFileObject(FileObject fileObject) {
        if (this.fileObject == null) {
            this.fileObject = fileObject;
            fillEditorPane(true);
        }
    }

    private void createProgressBar() {
        this.progressPanel = new JPanel();
        var progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        var labelPanel = new JPanel();
        var loadingLabel = new JLabel("Loading...");

        labelPanel.add(loadingLabel);
        var layout = new GroupLayout(this.progressPanel);
        this.progressPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
                                        .addComponent(labelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(116, 116, 116)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(113, Short.MAX_VALUE))
        );
    }

    public void fillEditorPane(boolean showProgressBar) {
        SwingUtilities.invokeLater(() -> {
            scrollPane.setVisible(!showProgressBar);
            progressPanel.setVisible(showProgressBar);
            new FillEditorPaneWorker().execute();
        });

    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }
    
    private final class FillEditorPaneWorker extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {

            var html = HtmlBuilder.getInstance()
                    .build(fileObject.asText());
            editorPane.setText(html);

            return html;
        }

        @Override
        protected void done() {
            scrollPane.setVisible(true);
            progressPanel.setVisible(false);
        }
    }
}
