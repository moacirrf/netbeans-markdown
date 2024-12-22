/*
 * Copyright (C) 2022 moacirrf
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
package io.github.moacirrf.netbeans.markdown;

import io.github.moacirrf.netbeans.markdown.ui.export.ExportPane;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import static org.openide.filesystems.FileUtil.toFile;

import org.openide.util.NbBundle.Messages;
import static org.openide.util.Utilities.actionsGlobalContext;

@ActionID(
        category = "Tools",
        id = "io.github.moacirrf.netbeans.markdown.ExportAction"
)
@ActionRegistration(
        displayName = "#CTL_ExportAction"
)
@ActionReferences(value = {
    @ActionReference(path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions", name = "Markdown Exporters", position = 1425),
    @ActionReference(path = "Editors/" + MarkdownDataObject.MIME_TYPE + "/Popup", position = 9999)
})
@Messages("CTL_ExportAction=Export to...")
public final class ExportAction implements ActionListener {

    private static final String TITLE_SELECT_DESTINY = "Select Destiny folder";
    private static final String MESSAGE_SELECT_FILE = "You must select at least one md file.";
    private static final String MESSAGE_SUCCESS = "Converted with success.";

    private Dialog dialog;

    @Override
    public void actionPerformed(ActionEvent ev) {
        List<File> lista = getSelectedFile();
        if (lista != null && !lista.isEmpty()) {
            var pane = ExportPane.newPane(lista, (e) -> {
                if (dialog != null) {
                    dialog.setVisible(false);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(null, MESSAGE_SUCCESS);
                }
            });

            var dd = new DialogDescriptor(pane, TITLE_SELECT_DESTINY, true, null);
            dd.setClosingOptions(new String[0]);
            dd.setOptions(new String[0]);

            dialog = DialogDisplayer.getDefault()
                    .createDialog(dd);

            dialog.setVisible(true);
        } else {
            showMessage(MESSAGE_SELECT_FILE);
        }
    }

    private void showMessage(String message) {
        DialogDisplayer.getDefault()
                .notify(new NotifyDescriptor.Message(message, NotifyDescriptor.Message.WARNING_MESSAGE));
    }

    private List<File> getSelectedFile() {
        var lista = actionsGlobalContext().lookupAll(MarkdownDataObject.class);
        if (lista != null) {
            return lista.stream()
                    .map(mdo -> toFile(mdo.getPrimaryFile()))
                    .collect(toList());
        }
        return List.of();
    }
}
