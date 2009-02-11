package org.jdna.metadataupdater.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageLabel extends JLabel {
    public ImageLabel(String uri) throws IOException {
        this(new URL(uri));
    }

    public ImageLabel(File file) throws IOException {
        this(file.toURL());
    }

    public ImageLabel(URL url) throws IOException {
        super();
        ImageIcon icon = new ImageIcon(url);
        setIcon(icon);
    }
}
