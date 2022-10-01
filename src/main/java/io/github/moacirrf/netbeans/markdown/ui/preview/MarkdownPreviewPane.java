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
        progressPanel = createProgressBar();
        add(progressPanel);
        add(scrollPane);
        scrollPane.setVisible(false);
        progressPanel.setVisible(true);

    }

    public void setFileObject(FileObject fileObject) {
        if (this.fileObject == null) {
            this.fileObject = fileObject;
            fillEditorPane();
        }
    }

    private JPanel createProgressBar() {
        var progressPanel = new JPanel();
        var layout = new GroupLayout(progressPanel);
        progressPanel.setLayout(layout);
        var jProgressBar = new JProgressBar();
        jProgressBar.setIndeterminate(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE - 100)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(137, Short.MAX_VALUE)
                                .addComponent(jProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(133, Short.MAX_VALUE))
        );

        return progressPanel;
    }

    public void fillEditorPane() {
        SwingUtilities.invokeLater(() -> {
            scrollPane.setVisible(false);
            progressPanel.setVisible(true);
            new FillEditorPaneWorker().execute();
        });

    }

    public JScrollPane getScrollPane() {
        return scrollPane;
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
