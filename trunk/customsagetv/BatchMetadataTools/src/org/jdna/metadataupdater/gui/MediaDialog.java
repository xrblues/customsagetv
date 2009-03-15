package org.jdna.metadataupdater.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.ui.SwingBindingUtils;

public class MediaDialog extends JDialog {
    private IMediaResource res;
    private IMediaMetadata md;

    JTextField title;
    JTextField mpaa;
    public MediaDialog(IMediaResource resource, JFrame parent, String dialogTitle) {
        super(parent, dialogTitle, true);
        this.res = resource;
        this.md=resource.getMetadata();
        if (md==null) {
            md=new MediaMetadata();
        }

        setSize(800, 600);
        
        JPanel panel = new JPanel(new MigLayout("","[][grow][]"));
        JLabel l = new JLabel("Title");
        panel.add(l);
        title=SwingBindingUtils.createTextBox(md, MetadataKey.MEDIA_TITLE);
        panel.add(title, "growx");
        
        JButton search  = new JButton("Search");
        panel.add(search, "wrap");
        
        l = new JLabel("MPAA");
        panel.add(l);
        mpaa=SwingBindingUtils.createTextBox(md, MetadataKey.MPAA_RATING);
        panel.add(mpaa, "growx, wrap");
        
        try {
            ImageLabel img = new ImageLabel(res.getLocalPosterUri());
            panel.add(img, "span, wrap");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        
        JPanel buttons = new JPanel(new MigLayout());
        
        JButton ok = new JButton("Save");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttons.add(ok, "tag ok");
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttons.add(cancel, "tag cancel");
        
        panel.add(buttons, "span, align right");
        
        getContentPane().add(panel);
        pack();
    }
}
