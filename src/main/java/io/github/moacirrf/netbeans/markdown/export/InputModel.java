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
import static java.lang.Integer.compare;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class InputModel implements Comparable<InputModel> {

    public static InputModel from(File file, int order) {
        var model = new InputModel();
        model.file = file;
        model.order = order;
        return model;
    }

    private String name;
    private File file;
    private int order;

    private InputModel() {
    }

    public File getFile() {
        return file;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        if (StringUtils.isBlank(name)) {
            return file.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(InputModel o) {
        return compare(order, o.order);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.file);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputModel other = (InputModel) obj;
        return Objects.equals(this.file, other.file);
    }

}
