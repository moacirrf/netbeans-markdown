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

import io.github.moacirrf.netbeans.markdown.MarkdownDataObject;
import io.github.moacirrf.netbeans.markdown.ui.preview.MarkdownPreviewScrollPane;
import io.github.moacirrf.netbeans.markdown.ui.preview.MyFileChangeListener;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public class MultiViewSplitEditorElement extends MultiViewEditorElement {

    private transient SplitPanel splitPanel;
    private transient ChangeListener leftScrollListener;
    private transient MarkdownPreviewScrollPane rightJScrollPane;
    private transient JScrollPane leftJScrollPane;

    public MultiViewSplitEditorElement(Lookup lookup) {
        super(lookup);
        initComponents();
    }

    @Override
    public JComponent getVisualRepresentation() {
        if (splitPanel == null) {
            initComponents();
        }
        return splitPanel;
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();

        leftJScrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class,
                this.getEditorPane());

        if (leftScrollListener == null) {
            leftScrollListener = this::onChangeScrollLeftEditor;
            leftJScrollPane.getViewport().addChangeListener(leftScrollListener);
        }

    }

    private void initComponents() {
        rightJScrollPane = new MarkdownPreviewScrollPane();
        FileObject mdFile = super.getLookup().lookup(MarkdownDataObject.class).getPrimaryFile();
        mdFile.addFileChangeListener(new MyFileChangeListener(rightJScrollPane) {
            @Override
            public void fileChanged(FileEvent fe) {
                super.fileChanged(fe);
                syncronizeScrolls();
            }

        });
        rightJScrollPane.setFileObject(mdFile);

        this.splitPanel = new SplitPanel();
        this.splitPanel.getSplitPanel().setRightComponent(rightJScrollPane);
        this.splitPanel.getSplitPanel().setLeftComponent(super.getVisualRepresentation());
        TopBar topBar = new TopBar(splitPanel);
        this.getToolbarRepresentation().add(topBar);
    }

    private void onChangeScrollLeftEditor(ChangeEvent e) {
        if (e.getSource() instanceof JViewport) {
            syncronizeScrolls();
        }
    }

    private void syncronizeScrolls() {
        var rightViewPort = rightJScrollPane.getViewport();
        var leftViewPort = leftJScrollPane.getViewport();
        rightViewPort.setViewPosition((Point) leftViewPort.getViewPosition().clone());
        if (isScrolledToMaximum(leftJScrollPane)) {
            setScrollToMaximum(rightJScrollPane);
        }
    }

    private void setScrollToMaximum(JScrollPane scrollPane) {
        Point p = scrollPane.getViewport().getViewPosition();
        p.y = Integer.MAX_VALUE;
        scrollPane.getViewport().setViewPosition(p);
    }

    private boolean isScrolledToMaximum(JScrollPane scrollPane) {
        var viewPort = scrollPane.getViewport();
        if ((viewPort.getViewSize().height - viewPort.getExtentSize().getHeight()) == viewPort.getViewPosition().y) {
            return true;
        }
        return false;
    }
    

}
