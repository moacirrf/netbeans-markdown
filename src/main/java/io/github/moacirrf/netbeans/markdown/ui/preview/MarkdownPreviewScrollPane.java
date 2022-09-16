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
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class MarkdownPreviewScrollPane extends JScrollPane {

    private JEditorPane editorPane;

    private transient FileObject fileObject;

    public MarkdownPreviewScrollPane() {
        this.initComponents();
        setAutoscrolls(false);
        this.setBorder(BorderFactory.createEmptyBorder(22, 0, 9, 0));
        editorPane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getInputEvent() instanceof MouseEvent) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    HtmlBrowser.URLDisplayer.getDefault().showURL(e.getURL());
                }
            }
        });
    }

    private void initComponents() {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setEditorKit(new MarkdownEditorKit());
        setViewportView(editorPane);
    }

    public void setFileObject(FileObject fileObject) {
        if (this.fileObject == null) {
            this.fileObject = fileObject;
            fillEditorPane();
        }
    }

    public void fillEditorPane() {
        try {
            editorPane.setText(HtmlBuilder.getInstance()
                    .build(fileObject.asText()));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
