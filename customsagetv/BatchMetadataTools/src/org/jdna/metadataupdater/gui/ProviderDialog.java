package org.jdna.metadataupdater.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;

public class ProviderDialog extends JDialog {
    List<String> providerIds=new ArrayList<String>();
    public ProviderDialog(String sectedProviderIds, JFrame parent, String title) {
        
        super(parent, title, true);
        
        String ids[] = sectedProviderIds.split("\\s*,\\s*");
        for (String id : ids) {
            providerIds.add(id);
        }
        
        JPanel panel = new JPanel(new MigLayout());
        
        for (IMediaMetadataProvider mp : MediaMetadataFactory.getInstance().getMetaDataProviders()) {
            JCheckBox cb = new JCheckBox(String.format("[%s] - %s", mp.getInfo().getId(), mp.getInfo().getName()));
            cb.setToolTipText(mp.getInfo().getDescription());
            cb.setSelected(providerIds.contains(mp.getInfo().getId()));
            cb.setName(mp.getInfo().getId());
            cb.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    JCheckBox cb = (JCheckBox) e.getSource();
                    if (cb.isSelected()) {
                        providerIds.add(cb.getName());
                    } else {
                        providerIds.remove(cb.getName());
                    }
                }
                
            });
            
            panel.add(cb, "wrap");
        }
        
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        panel.add(ok, "align right, tag ok");
        
        getContentPane().add(panel);
        pack();
    }
    
    public String getProviderIdString() {
        return StringUtils.join(providerIds, ",");
    }
    
}
