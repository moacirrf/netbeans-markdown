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
package io.github.moacirrf.netbeans.markdown.export;

import java.io.File;
import java.util.List;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class ExporterConfig {

    public static ExporterConfig newUniqueFile(File destinyFolder, List<InputModel> mdfiles, String outputFileName) {
        var config = new ExporterConfig();
        config.destinyFolder = destinyFolder;
        config.mdfiles = mdfiles;
        config.outputFileName = outputFileName;
        config.uniqueFile = true;
        return config;
    }

    public static ExporterConfig newSeparatedFile(File destinyFolder, List<InputModel> mdfiles) {
        var config = new ExporterConfig();
        config.destinyFolder = destinyFolder;
        config.mdfiles = mdfiles;
        config.uniqueFile = false;
        return config;
    }
    

    private File destinyFolder;
    private List<InputModel> mdfiles;
    private boolean uniqueFile;
    private String outputFileName;

    public File getDestinyFolder() {
        return destinyFolder;
    }

    public void setDestinyFolder(File destinyFolder) {
        this.destinyFolder = destinyFolder;
    }

    public List<InputModel> getMdfiles() {
        return mdfiles;
    }

    public void setMdfiles(List<InputModel> mdfiles) {
        this.mdfiles = mdfiles;
    }

    public boolean isUniqueFile() {
        return uniqueFile;
    }

    public void setUniqueFile(boolean uniqueFile) {
        this.uniqueFile = uniqueFile;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }


}
