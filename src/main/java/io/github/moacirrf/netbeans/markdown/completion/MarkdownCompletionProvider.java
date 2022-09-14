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
package io.github.moacirrf.netbeans.markdown.completion;

import io.github.moacirrf.netbeans.markdown.MarkdownDataObject;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
@MimeRegistration(mimeType = MarkdownDataObject.MIME_TYPE, service = CompletionProvider.class)
public class MarkdownCompletionProvider implements CompletionProvider {

    @Override
    public CompletionTask createTask(int type, JTextComponent jtc) {
        if (type == CompletionProvider.COMPLETION_QUERY_TYPE) {
            return new AsyncCompletionTask(new CompletionQuery(), jtc);
        }
        return null;
    }

    @Override
    public int getAutoQueryTypes(JTextComponent jtc, String string) {
        return 0;
    }
}
