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
package io.github.moacirrf.netbeans.markdown.ui.scroll;

import static io.github.moacirrf.netbeans.markdown.html.HtmlBuilder.MD_SOURCE_POSITION_ATTR;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Moacir da Roza Flores <moacirrf@gmail.com>
 */
public final class ScrollableModel implements Comparable<ScrollableModel> {

    private static List<String> LIST_TAGS =Arrays.asList("p","h1","h2","h3","h4","h5","h6","li", "pre","code", "a");

    public static ScrollableModelList from(Document document, String completeMdText) {
        int size = completeMdText.length();
        Elements elements = document.getElementsByAttribute(MD_SOURCE_POSITION_ATTR);
        var lista = new ScrollableModelList();
        elements.forEach(element -> {
            if(LIST_TAGS.contains(element.tagName().toLowerCase())){
                String[] attrs = element.attr(MD_SOURCE_POSITION_ATTR).split("-");
                int endIndex = parseInt(attrs[1]);
                if(endIndex > size){
                    endIndex = size-1;
                }
                String mdText = completeMdText.substring(parseInt(attrs[0]),  endIndex);
                lista.add(new ScrollableModel(parseInt(attrs[0]), parseInt(attrs[1]), element,mdText));
            }
        });

        return lista;
    }

    private final int mdBegin;
    private final int mdEnd;
    private final Element element;
    private String mdText;
    private String id;
    

    private ScrollableModel(int mdBegin, int mdEnd, Element element, String mdText) {
        this.mdBegin = mdBegin;
        this.mdEnd = mdEnd;
        this.element = element;
        this.mdText = mdText;
        
        id = element.id();
    }

    public int getMdBegin() {
        return mdBegin;
    }

    public int getMdEnd() {
        return mdEnd;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final ScrollableModel other = (ScrollableModel) obj;
        return Objects.equals(this.id, other.id);
    }



    @Override
    public int compareTo(ScrollableModel o) {
        return Integer.compare(mdBegin, o.mdBegin);
    }

    public static class ScrollableModelList extends ArrayList<ScrollableModel> {

        public Optional<ScrollableModel> before(ScrollableModel element) {
            int index = indexOf(element);
            if (index - 1 > -1) {
                return Optional.of(get(index - 1));
            }
            return Optional.empty();
        }

        public Optional<ScrollableModel> after(ScrollableModel element) {
            int index = indexOf(element);
            if (index + 1 < size() - 1) {
                return Optional.of(get(index + 1));
            }
            return Optional.empty();
        }
    }

    public String text(){
        return element.text();
    }
    
    public String id(){
        return element.id();
    }

    public String getMdText() {
        return mdText;
    }
    
}
