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

import io.github.moacirrf.netbeans.markdown.TempDir;
import io.github.moacirrf.netbeans.markdown.TempDirTest;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import static java.util.Arrays.asList;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class HTMLExporterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MockedStatic<TempDir> mockedStatic;

    @Before
    public void setup() {
        mockedStatic = mockStatic(TempDir.class);
    }

    @After
    public void tearDown() {
        mockedStatic.close();
    }

    @Test
    public void testExportJoinMds() {
        mockedStatic.when(() -> TempDir.getTempDir()).thenReturn(TempDirTest.getTempDir());
        mockedStatic.when(() -> TempDir.getCantLoadImage()).thenReturn(TempDirTest.getCantLoadImage());

        var mdFiles = asList(InputModel.from(getMdfile("test.md"), 1), InputModel.from(getMdfile("test_2.md"), 0));

        var exporterConfig = ExporterConfig.newUniqueFile(folder.getRoot(), mdFiles, "output");
        var exporter = new HtmlExporter();
        List<File> files = exporter.export(exporterConfig);
        assertFalse("No one html was generated", files.isEmpty());
        files.forEach(f -> assertTrue("File not found", f.exists()));
        
        TempDirTest.removeTempDir();
    }

    @Test
    public void testExportSepratedMds() {
        mockedStatic.when(() -> TempDir.getTempDir()).thenReturn(TempDirTest.getTempDir());
        mockedStatic.when(() -> TempDir.getCantLoadImage()).thenReturn(TempDirTest.getCantLoadImage());

        var mdFiles = asList(InputModel.from(getMdfile("test.md"), 1), InputModel.from(getMdfile("test_2.md"), 0));

        var exporterConfig = ExporterConfig.newSeparatedFile(folder.getRoot(), mdFiles);
        var exporter = new HtmlExporter();

        List<File> files = exporter.export(exporterConfig);
        assertFalse("No one html was generated", files.isEmpty());
        assertTrue("Must have two html", files.size() == 2);
        files.forEach(f -> assertTrue("File not found", f.exists()));
        
        TempDirTest.removeTempDir();
    }

    public File getMdfile(String name) {
        try {
            var file = Path.of(HTMLExporterTest.class.getResource(name).toURI()).toFile();
            assertTrue("Md file font exists", file.exists());
            return file;
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

}
