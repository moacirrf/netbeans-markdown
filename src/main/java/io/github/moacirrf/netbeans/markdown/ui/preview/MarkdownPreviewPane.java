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
import io.github.moacirrf.netbeans.markdown.ui.preview.image.ImageLabel;
import java.awt.CardLayout;
import java.awt.Point;
import java.util.concurrent.ExecutionException;
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
import org.openide.util.Exceptions;

public class MarkdownPreviewPane extends JPanel {

    private JEditorPane editorPane;

    private JScrollPane scrollPane;

    private JPanel progressPanel;

    private transient FileObject fileObject;

    private volatile int currentVersion;

    private String lastRenderedHtml;

    public MarkdownPreviewPane() {
        this.initComponents();
        this.setBorder(BorderFactory.createEmptyBorder(22, 0, 9, 0));
        editorPane.setBorder(BorderFactory.createEmptyBorder());
        editorPane.setFocusable(true);
    }

    private void initComponents() {
        var layout = new CardLayout();
        setLayout(layout);

        editorPane = new JEditorPaneImpl();
        editorPane.setEditable(false);
        editorPane.setEditorKit(new MarkdownEditorKit(editorPane));

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(editorPane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
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
        fillEditorPane(null, showProgressBar);
    }

    public void fillEditorPane(String markdownText, boolean showProgressBar) {
        SwingUtilities.invokeLater(() -> {
            scrollPane.setVisible(!showProgressBar);
            progressPanel.setVisible(showProgressBar);
            ScrollState scrollState = null;
            if (!showProgressBar) {
                var viewport = scrollPane.getViewport();
                scrollState = new ScrollState(viewport.getViewPosition(), viewport.getViewSize().height, viewport.getExtentSize().height);
            }
            new FillEditorPaneWorker(markdownText, ++currentVersion, scrollState).execute();
        });

    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    private final class FillEditorPaneWorker extends SwingWorker<Object, Object> {

        private final String markdownText;

        private final int version;

        private final ScrollState scrollState;

        private FillEditorPaneWorker(String markdownText, int version, ScrollState scrollState) {
            this.markdownText = markdownText;
            this.version = version;
            this.scrollState = scrollState;
        }

        @Override
        protected Object doInBackground() throws Exception {
            var source = markdownText;
            if (source == null) {
                source = fileObject.asText();
            }
            var html = HtmlBuilder.getInstance()
                    .build(source);
            ImageLabel.preloadImages(html);
            return html;
        }

        @Override
        protected void done() {
            if (version != currentVersion) {
                return;
            }
            try {
                var html = (String) get();
                if (html.equals(lastRenderedHtml)) {
                    return;
                }
                lastRenderedHtml = html;
                editorPane.setText(html);
                if (scrollState != null) {
                    restoreScrollPosition(scrollState);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            scrollPane.setVisible(true);
            progressPanel.setVisible(false);
        }
    }

    /**
     * Restores the scroll after the re-render layout settles, preserving the
     * relative position so the preview does not jump when the content height
     * changes (e.g. when images appear or text grows).
     */
    private void restoreScrollPosition(ScrollState scrollState) {
        var viewport = scrollPane.getViewport();
        SwingUtilities.invokeLater(() -> {
            Point target = scrollState.resolve(viewport.getViewSize().height, viewport.getExtentSize().height);
            viewport.setViewPosition(target);
        });
    }

    private static final class ScrollState {

        private final float ratio;

        ScrollState(Point position, int viewHeight, int extentHeight) {
            int range = viewHeight - extentHeight;
            this.ratio = range > 0 ? (float) position.y / range : 0f;
        }

        Point resolve(int viewHeight, int extentHeight) {
            int range = viewHeight - extentHeight;
            int targetY = range > 0 ? Math.round(ratio * range) : 0;
            return new Point(0, targetY);
        }
    }
}
