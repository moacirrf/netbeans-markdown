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

import com.vladsch.flexmark.docx.converter.DocxRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.moacirrf.netbeans.markdown.html.HtmlBuilder;
import io.github.moacirrf.netbeans.markdown.html.flexmark.MyCoreNodeDocxRenderer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.List;
import java.util.Optional;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public class DocxExporter implements Exporter {

    private static final String EXT = ".docx";

    private static DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP))
            .toMutable()
            .set(DocxRenderer.SUPPRESS_HTML, true)
            .set(DocxRenderer.DEFAULT_LINK_RESOLVER, true)
            .toImmutable();

    public static final MutableDataSet OPTIONS_RENDERER = HtmlBuilder.OPTIONS_RENDERER;

    @Override
    public List<File> export(ExporterConfig config) {
        var lista = new ArrayList<File>();

        sort(config.getMdfiles());
        if (config.isUniqueFile()) {
            var out = new File(config.getDestinyFolder(), config.getOutputFileName() + EXT);
            Node node = parse(mergeMd(config.getMdfiles()));
            writeDocx(node, out).ifPresent(lista::add);
        } else {
            config.getMdfiles().forEach(input -> {
                String name = input.getName().replace(".md", EXT);
                writeDocx(parse(input.getFile()), new File(config.getDestinyFolder(), name)).ifPresent(lista::add);
            });
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

        try (var writer = new BufferedOutputStream(new FileOutputStream(file))) {
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

    private Node parse(File input) {
        var parser = Parser.builder(OPTIONS).build();
        return parser.parse(getMarkdownContent(input));
    }

    private String getMarkdownContent(File input) {
        try {
            return Files.readString(input.toPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    private Optional<File> writeDocx(Node document, File outPut) {
        DocxRenderer renderer = DocxRenderer.builder(OPTIONS_RENDERER)
                .nodeFormatterFactory(MyCoreNodeDocxRenderer::new)
                .build();
        WordprocessingMLPackage template = DocxRenderer.getDefaultTemplate();
        renderer.render(document, template);
        try {
            String orignalName = outPut.getName();
            int contRepeat = 0;
            while (outPut.exists()) {
                outPut = new File(outPut.getParent(), (contRepeat++) + "_" + orignalName);
            }
            template.save(outPut, Docx4J.FLAG_SAVE_ZIP_FILE);
            return Optional.of(outPut);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
