/*
 * Copyright (C) 2025 mobsolution
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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class MyConfigurations {

    public enum ViewMode {
        EDITOR,
        SPLIT_HORIZONTAL,
        SPLIT_VERTICAL,
        PREVIEW
    }

    private static final String PREF_KEEP_SCROLL_SYNC = "io.github.moacirrf.netbeans.markdown.SCROLL_SYNC";
    private static final String PREF_VIEW_MODE = "io.github.moacirrf.netbeans.markdown.VIEW_MODE";
    private static final Preferences prefs;

    static {
        prefs = NbPreferences.forModule(MyConfigurations.class);
    }

    public static void setScrollSync(boolean sync) {
        if (prefs != null) {
            prefs.putBoolean(PREF_KEEP_SCROLL_SYNC, sync);
        }
    }

    public static boolean isScrollSync() {
        if (prefs != null) {
            return prefs.getBoolean(PREF_KEEP_SCROLL_SYNC, true);
        }
        return true;
    }

    public static void setDefaultViewMode(ViewMode viewModel) {
        if (prefs != null) {
            prefs.put(PREF_VIEW_MODE, viewModel.name());
        }
    }

    public static ViewMode getViewMode() {
        if (prefs != null) {
            return ViewMode.valueOf(prefs.get(PREF_VIEW_MODE, ViewMode.SPLIT_HORIZONTAL.name()));
        }
        return ViewMode.SPLIT_HORIZONTAL;
    }

    public static void clear() {
        try {
            if (prefs != null) {
                prefs.clear();
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private MyConfigurations() {
    }

}
