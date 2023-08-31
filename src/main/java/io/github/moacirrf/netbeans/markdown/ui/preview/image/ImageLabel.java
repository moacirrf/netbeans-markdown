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
package io.github.moacirrf.netbeans.markdown.ui.preview.image;

import io.github.moacirrf.netbeans.markdown.ui.MouseListenerBuilder;
import static io.github.moacirrf.netbeans.markdown.ui.MouseListenerBuilder.EventType.ENTERED;
import static io.github.moacirrf.netbeans.markdown.ui.MouseListenerBuilder.EventType.EXITED;
import static io.github.moacirrf.netbeans.markdown.ui.MouseListenerBuilder.EventType.RELEASED;
import io.github.moacirrf.netbeans.markdown.ui.preview.ViewUtils;
import io.github.moacirrf.netbeans.markdown.ui.scroll.ScrollUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;

/**
 *
 * @author moacirrf
 */
public class ImageLabel extends JLabel {

    private static final Color COLOR_IMAGED_HIPERLINK = Color.decode("#3B6AAB");
    private static final int PADDING_RIGHT = 25;
    private static final int DELAY_SCALE_IMAGE = 500;
    private String urlHiperlinkParent;
    private final ImageIcon imageIconOriginal;

    public ImageLabel(Element element, JEditorPane editorPane) {

        this.imageIconOriginal = this.createImageIcon(element);
        this.setIcon(this.createImageIcon(element));

        JScrollPane scrollPane = ScrollUtils.getScrollPaneOf(editorPane);
        resizeImageIcon(scrollPane.getViewport().getWidth() - PADDING_RIGHT);

        this.setToolTipText(ViewUtils.getAttributeFrom(HTML.Attribute.TITLE, element));

        Timer timer = new Timer(DELAY_SCALE_IMAGE, event
                -> resizeImageIcon(scrollPane.getViewport().getWidth() - PADDING_RIGHT));
        timer.setRepeats(false);

        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (!timer.isRunning()) {
                    timer.start();
                } else {
                    timer.restart();
                }
            }

        });
        setAddMouseListener(element);
        if (elementHasAParentHiperlink(element)) {
            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_IMAGED_HIPERLINK));
        }
    }

    private void setAddMouseListener(Element element) {
        this.addMouseListener(MouseListenerBuilder.build((mouseEvent, eventType) -> {
            switch (eventType) {
                case ENTERED:
                    if (elementHasAParentHiperlink(element)) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    break;
                case EXITED:
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    break;
                case RELEASED:
                    if (elementHasAParentHiperlink(element) && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                        String url = getUrlHiperlinkParent(element);
                        try {
                            HtmlBrowser.URLDisplayer.getDefault().showURL(URI.create(url).toURL());
                        } catch (MalformedURLException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
            }
        }));
    }

    private ImageIcon createImageIcon(Element element) {
        var imageIcon = new ImageIcon(getImageURL(element));
        String widthElem = ViewUtils.getAttributeFrom(HTML.Attribute.WIDTH, element);
        String heightElem = ViewUtils.getAttributeFrom(HTML.Attribute.HEIGHT, element);

        if (isNumeric(widthElem) && isNumeric(heightElem)) {
            imageIcon.setImage(scaleImage(imageIcon.getImage(), new Dimension(Integer.parseInt(widthElem), Integer.parseInt(heightElem))));
        } else {
            var referenceSize = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
            var newSize = new Dimension(0, 0);

            if (isNumeric(widthElem)) {
                newSize.width = Integer.parseInt(widthElem);
            }
            if (isNumeric(heightElem)) {
                newSize.height = Integer.parseInt(heightElem);
            }
            if (newSize.width > 0 || newSize.height > 0) {
                var calculatedSize = getCalculatedResizeImage(referenceSize, newSize);
                Image scaledImage = scaleImage(imageIcon.getImage(), calculatedSize);
                imageIcon.setImage(scaledImage);
            }
        }

        setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());

        return imageIcon;
    }

    private void resizeImageIcon(int newWidth) {
        ImageIcon icon = (ImageIcon) getIcon();
        if (this.imageIconOriginal.getIconWidth() <= newWidth) {
            icon.setImage(this.imageIconOriginal.getImage());
            setSize(icon.getIconWidth(), icon.getIconHeight());
        } else {
            var referenceSize = new Dimension(this.imageIconOriginal.getIconWidth(), this.imageIconOriginal.getIconHeight());
            var newSize = new Dimension(newWidth, 0);
            var calculatedSize = getCalculatedResizeImage(referenceSize, newSize);
            Image scaledImage = scaleImage(imageIconOriginal.getImage(), calculatedSize);
            icon.setImage(scaledImage);
            setSize(icon.getIconWidth(), icon.getIconHeight());
        }
    }

    private Image scaleImage(Image image, Dimension size) {
        return image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
    }

    private Dimension getCalculatedResizeImage(final Dimension referenceSize, final Dimension newSize) {
        Dimension dimension = new Dimension();
        //calculate by width   
        if (newSize.width > 0) {
            double percent = 100.0 - ((100.0 * (newSize.width)) / referenceSize.width);
            double width = (referenceSize.width - (referenceSize.width * (percent / 100.0)));
            double height = (referenceSize.height - (referenceSize.height * (percent / 100.0)));
            dimension.setSize(width, height);

            // calculate by height    
        } else if (newSize.height > 0) {
            double percent = 100.0 - ((100.0 * (newSize.height)) / referenceSize.height);
            double height = (referenceSize.height - (referenceSize.height * (percent / 100.0)));
            double width = (referenceSize.width - (referenceSize.width * (percent / 100.0)));
            dimension.setSize(width, height);
        }

        return dimension;
    }

    private URL getImageURL(Element element) {
        String src = (String) element.getAttributes().
                getAttribute(HTML.Attribute.SRC);
        if (src == null) {
            return null;
        }

        URL reference = ((HTMLDocument) element.getDocument()).getBase();
        try {
            @SuppressWarnings("deprecation")
            URL u = new URL(reference, src);
            return u;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean elementHasAParentHiperlink(Element element) {
        urlHiperlinkParent = getUrlHiperlinkParent(element);
        return urlHiperlinkParent != null;
    }

    private String getUrlHiperlinkParent(Element element) {

        if (urlHiperlinkParent != null) {
            return urlHiperlinkParent;
        }
        var parent = element.getParentElement();
        int totalChilds = parent.getElementCount();

        for (int c = 0; c < totalChilds; c++) {
            Element child = parent.getElement(c);
            var itt = child.getAttributes().getAttributeNames().asIterator();
            while (itt.hasNext()) {
                var next = itt.next();
                if (next instanceof HTML.Tag) {
                    HTML.Tag tag = (HTML.Tag) next;
                    if (HTML.Tag.A.toString().equals(tag.toString()) && child.getAttributes().getAttribute(tag).toString().contains("href")) {
                        return child.getAttributes().getAttribute(tag).toString().replace("href=", "").split(" ")[0];
                    }
                }
            }
        }
        return null;
    }

}
