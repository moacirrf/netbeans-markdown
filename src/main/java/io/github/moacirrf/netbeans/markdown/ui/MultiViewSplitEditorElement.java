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

import io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollPreviewUtils;
import io.github.moacirrf.netbeans.markdown.Context;
import io.github.moacirrf.netbeans.markdown.MarkdownDataObject;
import io.github.moacirrf.netbeans.markdown.ui.preview.MarkdownPreviewPane;
import io.github.moacirrf.netbeans.markdown.ui.preview.MyFileChangeListener;
import io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollCodeViewUtils;
import static io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils.getScrollPaneOf;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public class MultiViewSplitEditorElement extends MultiViewEditorElement {

    private enum SCROLL_STATE {
        CODE,
        PREVIEW
    }
    private transient SplitPanel splitPanel;
    private transient ChangeListener scrollListener;
    private transient MarkdownPreviewPane previewPane;
    private FileObject mdFile;
    private SCROLL_STATE currentScroll;

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
    public void componentShowing() {
        if (mdFile != null) {
            Context.OPENED_FILE = mdFile;
        }
        super.componentShowing();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        JScrollPane leftJScrollPane = getScrollPaneOf(this.getEditorPane());
        if (scrollListener == null) {
            scrollListener = this::onChangeScrollEditor;
            leftJScrollPane.getViewport().addChangeListener(scrollListener);
            previewPane.getScrollPane().getViewport().addChangeListener(scrollListener);

            this.getEditorPane().addMouseListener(new MouseListenerImpl() {
                @Override
                public void onMouseEntered(MouseEvent e) {
                    currentScroll = SCROLL_STATE.CODE;
                }
            });

            previewPane.getEditorPane().addMouseListener(new MouseListenerImpl() {
                @Override
                public void onMouseEntered(MouseEvent e) {
                    currentScroll = SCROLL_STATE.PREVIEW;
                }
            });
        }
    }

    private void initComponents() {
        previewPane = new MarkdownPreviewPane();
        mdFile = super.getLookup().lookup(MarkdownDataObject.class).getPrimaryFile();
        mdFile.addFileChangeListener(new MyFileChangeListener(previewPane) {
            @Override
            public void fileChanged(FileEvent fe) {
                super.fileChanged(fe);
                ScrollPreviewUtils.syncronizeScrolls(getEditorPane(), previewPane.getEditorPane());
            }
        });
        previewPane.setFileObject(mdFile);

        this.splitPanel = new SplitPanel();
        this.splitPanel.getSplitPanel().setRightComponent(previewPane);
        this.splitPanel.getSplitPanel().setLeftComponent(super.getVisualRepresentation());
        TopBar topBar = new TopBar(splitPanel);
        this.getToolbarRepresentation().add(topBar);
    }

    private void onChangeScrollEditor(ChangeEvent e) {
        if (SCROLL_STATE.CODE == currentScroll) {
            ScrollPreviewUtils.syncronizeScrolls(getEditorPane(), previewPane.getEditorPane());
        } else if (SCROLL_STATE.PREVIEW == currentScroll) {
            ScrollCodeViewUtils.syncronizeScrolls(getEditorPane(), previewPane.getEditorPane());
        }
    }
}
