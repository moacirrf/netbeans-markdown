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

import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import static com.vladsch.flexmark.pdf.converter.PdfConverterExtension.exportToPdf;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import io.github.moacirrf.netbeans.markdown.html.HtmlBuilder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class PDFExporter implements Exporter {

    private static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP),
            TocExtension.create()).toMutable()
            .set(TocExtension.LIST_CLASS, PdfConverterExtension.DEFAULT_TOC_LIST_CLASS)
            .toImmutable();

    private static final String EXT = ".pdf";

    @Override
    public List<File> export(ExporterConfig config) {
        var lista = new ArrayList<File>();
        sort(config.getMdfiles());
        try {

            if (config.isUniqueFile()) {
                var html = getHtml(mergeMd(config.getMdfiles()));
                String name = config.getOutputFileName().replace(".md", EXT)+EXT;
                var file = new File(config.getDestinyFolder(), name);
                exportToPdf(new FileOutputStream(file), html, "", OPTIONS);
                lista.add(file);
            } else {
                for (InputModel input : config.getMdfiles()) {
                    var html = getHtml(input.getFile());
                    String name = input.getName().replace(".md", EXT);
                    var file = new File(config.getDestinyFolder(), name);
                    exportToPdf(new FileOutputStream(file), html, "", OPTIONS);
                    lista.add(file);
                }
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return lista;
    }

    private File mergeMd(List<InputModel> mds) {
        File file = null;
        try {
            file = Files.createTempFile("uniqueMd", "uniqueMd").toFile();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        try ( var writer = new BufferedOutputStream(new FileOutputStream(file));) {
            mds.forEach(input -> {
                try {
                    writer.write(getMarkdownContent(input.getFile()).getBytes());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            });

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return file;
    }

    private String getHtml(File input) {
        return HtmlBuilder.getInstance().build(getMarkdownContent(input));
    }

    private String getMarkdownContent(File input) {
        try {
            return Files.readString(input.toPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }
}
