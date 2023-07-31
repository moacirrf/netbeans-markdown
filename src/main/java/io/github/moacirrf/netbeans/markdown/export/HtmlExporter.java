/*
 * Copyright (C) 2023 moacirrf
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

import io.github.moacirrf.netbeans.markdown.html.HtmlBuilder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openide.util.Exceptions;

/**
 *
 * @author moacirrf
 */
public class HtmlExporter implements Exporter {

    private static final String EXT = ".html";

    @Override
    public List<File> export(ExporterConfig config) {
        var lista = new ArrayList<File>();
        sort(config.getMdfiles());

        if (config.isUniqueFile()) {
            var html = getHtml(mergeMd(config.getMdfiles()));
            String name = config.getOutputFileName().replace(".md", EXT) + EXT;
            var file = new File(config.getDestinyFolder(), name);
            html = moveImages(config.getDestinyFolder().toPath(), html);
            writeHtml(file.toPath(), html);
            lista.add(file);
        } else {
            for (InputModel input : config.getMdfiles()) {
                var html = getHtml(input.getFile());
                String name = input.getName().replace(".md", EXT);
                var file = new File(config.getDestinyFolder(), name);
                html = moveImages(config.getDestinyFolder().toPath(), html);
                writeHtml(file.toPath(), html);
                lista.add(file);
            }
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

        try (var writer = new BufferedOutputStream(new FileOutputStream(file));) {
            mds.forEach(input -> {
                try {
                    writer.write(("\n"+getMarkdownContent(input.getFile())).getBytes());
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
        return HtmlBuilder.getInstance()
                .includeFormTag()
                .build(getMarkdownContent(input));
    }

    private String getMarkdownContent(File input) {
        try {
            return Files.readString(input.toPath());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    private void writeHtml(Path path, String htmlContent) {

        try {
            Files.writeString(path, htmlContent, CREATE_NEW, WRITE, TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Creates a folder image and copy all imges to this folder.
     * 
     * @param path Directory selected top export the html
     * @param html The actual Html in string format
     * @return Returns the html text with images with new address.
     */
    private String moveImages(Path path, String html) {
        Path images = Path.of(path.toString(), "images");
        try {
            if (!Files.exists(images)) {
                images = Files.createDirectory(Path.of(path.toString(), "images"));
            }

            if (images != null && Files.exists(images)) {
                Document doc = Jsoup.parse(html);
                Elements elements = doc.getElementsByTag("img");
                for (Element element : elements) {
                    
                    Path originalImage = null;
                    originalImage = Path.of(URI.create(element.attr("src")));
                    if (Files.exists(originalImage)) {
                        Path image = Path.of(images.toString(), originalImage.getFileName().toString());
                        if (Files.exists(image)) {
                            image = Path.of(images.toString(), System.currentTimeMillis() + "_" + originalImage.getFileName().toString());
                        }
                        Files.copy(originalImage, image);
                        element.attr("src", "images/" + image.getFileName().toString());
                    }
                }
                return doc.html();
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return html;
    }

}
