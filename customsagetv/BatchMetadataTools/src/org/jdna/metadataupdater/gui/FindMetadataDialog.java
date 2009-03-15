package org.jdna.metadataupdater.gui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.MediaMetadataUtils;

public class FindMetadataDialog extends JDialog {
    private IMediaResource currentResourse = null;
    JLabel searchLabel = new JLabel("Title");
    JTextField searchQuery = new JTextField();
    JButton searchButton = new JButton("Find");
    
    public FindMetadataDialog(IMediaResource resource, JFrame parent) {
        super(parent);
        this.currentResourse=resource;
        
        setTitle("Find Metadata: " + resource.getLocationUri());
        
        JPanel p = new JPanel(new MigLayout("","[][grow][]",""));
        
        
        p.add(searchLabel);
        p.add(searchQuery, "growx");
        p.add(searchButton, "wrap");
        
        updateValues();
    }

    private void updateValues() {
        searchQuery.setText(MediaMetadataUtils.cleanSearchCriteria(currentResourse.getName(), false));
    }
}
