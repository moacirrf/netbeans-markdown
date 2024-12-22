/*
 * Copyright (C) 2024 Moacir da Roza Flores <moacirrf@gmail.com>
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

import java.io.IOException;
import static java.lang.System.getProperty;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import org.openide.util.Exceptions;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class TempDirTest {

    private static final String NB_MARKDOWN_NOT_LOAD_IMAGE = "nb_markdown_not_load_image.png";

    private static Path NOT_LOADED_IMAGE;

    public static final String TEMP_DIR_PLUGIN = getProperty("java.io.tmpdir") + "/nb_markdown_teste";

    public static Path getTempDir() {
        Path path = Paths.get(TEMP_DIR_PLUGIN);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            createCantLoadImage();
        }
        return path;
    }

    public static Path getCantLoadImage() {
        if (Files.exists(getTempDir())) {
            if (NOT_LOADED_IMAGE == null) {
                createCantLoadImage();
            }
        }
        return NOT_LOADED_IMAGE;
    }

    private static void createCantLoadImage() {
        try (var inputStream = Icons.class.getResourceAsStream(NB_MARKDOWN_NOT_LOAD_IMAGE)) {
            byte[] bytes = inputStream.readAllBytes();
            NOT_LOADED_IMAGE = Path.of(TEMP_DIR_PLUGIN, NB_MARKDOWN_NOT_LOAD_IMAGE);
            Files.write(NOT_LOADED_IMAGE, bytes, CREATE, TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void removeTempDir() {
        clearTempFolder(getTempDir());
    }

    private static void clearTempFolder(Path path) {
        try {
            if (Files.isDirectory(path) && Files.list(path).count() > 0) {
                Files.list(path).forEach(it -> clearTempFolder(it));
            }
            path.toFile().setWritable(true);
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private TempDirTest() {
    }
}
