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

import org.netbeans.ModuleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class Installer extends ModuleInstall {

    private static final String MARKDOWN_SUPPORT = "org.netbeans.modules.markdown";

    @Override
    public void restored() {
        ModuleManager moduleManager = (ModuleManager) ModuleManager.getDefault();
        if (moduleManager != null) {
            org.netbeans.Module markdownSupport = moduleManager.get(MARKDOWN_SUPPORT);
            if (markdownSupport != null && markdownSupport.isEnabled()) {
                try {
                    moduleManager.disable(markdownSupport);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public boolean closing() {
        TempDir.removeTempDir();
        return super.closing();
    }
}
