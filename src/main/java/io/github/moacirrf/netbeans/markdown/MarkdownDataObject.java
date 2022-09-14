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
package io.github.moacirrf.netbeans.markdown;

import io.github.moacirrf.netbeans.markdown.ui.MultiViewSplitEditorElement;
import java.io.IOException;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.modules.textmate.lexer.api.GrammarRegistration;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_Markdown_LOADER=Files of Markdown"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Markdown_LOADER",
        mimeType = MarkdownDataObject.MIME_TYPE,
        extension = {"md"}
)
@DataObject.Registration(
        mimeType = MarkdownDataObject.MIME_TYPE,
        iconBase = "io/github/moacirrf/netbeans/markdown/markdown.png",
        displayName = "#LBL_Markdown_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/x-markdown/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/ctions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/" + MarkdownDataObject.MIME_TYPE + "/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
@GrammarRegistration(mimeType = "text/x-markdown-nb", grammar = "markdown.tmLanguage.json")
public class MarkdownDataObject extends MultiDataObject {

    public static final String MIME_TYPE = "text/x-markdown-nb";

    public MarkdownDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor(MarkdownDataObject.MIME_TYPE, true);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_Markdown_EDITOR",
            iconBase = "io/github/moacirrf/netbeans/markdown/markdown.png",
            mimeType = "text/x-markdown-nb",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "Markdown",
            position = 1000
    )
    @Messages("LBL_Markdown_EDITOR=Source")
    public static MultiViewElement createEditor(Lookup lkp) {
        return new MultiViewSplitEditorElement(lkp);
    }
}
