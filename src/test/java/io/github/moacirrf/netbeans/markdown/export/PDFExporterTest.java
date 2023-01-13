/*
 * Copyright (C) 2023 Moacir da Roza Flores <moacirrf@gmail.com>
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
import java.net.URISyntaxException;
import java.nio.file.Path;
import static java.util.Arrays.asList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class PDFExporterTest {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testExportJoinMds() {
        var mdFiles = asList(InputModel.from(getMdfile("teste.md"), 1), InputModel.from(getMdfile("README.md"), 0));
        
        var exporterConfig = ExporterConfig.newUniqueFile(folder.getRoot(), mdFiles, "output");
        var exporter = new PDFExporter();
        List<File> files = exporter.export(exporterConfig);
        assertFalse("No one pdf was generated", files.isEmpty());
        files.forEach(f -> assertTrue("File not found",f.exists()));
    }

    @Test
    public void testExportSepratedMds() {
        
        var mdFiles = asList(InputModel.from(getMdfile("teste.md"), 1), InputModel.from(getMdfile("README.md"), 0));
        
        var exporterConfig = ExporterConfig.newSeparatedFile(folder.getRoot(), mdFiles);
        var exporter = new PDFExporter();
        
        List<File> files = exporter.export(exporterConfig);
        assertFalse("No one pdf was generated", files.isEmpty());
        assertTrue("Must have two pdf", files.size() == 2);
        files.forEach(f -> assertTrue("File not found",f.exists()));
    }
    
    public File getMdfile(String name) {
        try {
            var file = Path.of(PDFExporterTest.class.getResource(name).toURI()).toFile();
            assertTrue("Md file font exists", file.exists());
            return file;
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
}
