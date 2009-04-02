package org.jdna.metadataupdater.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import org.jdna.media.IMediaResource;

public class MediaTableMouseListener implements MouseListener {

    public void mouseClicked(MouseEvent e) {
        // passing -1 as the column yields the IMediaResource object
        JTable t = (JTable) e.getSource();
        IMediaResource res = (IMediaResource) t.getModel().getValueAt(t.convertRowIndexToModel(t.getSelectedRow()),-1);
        System.out.println("Selected Resource: " + res.getName());
        
        //MediaDialog md = new MediaDialog(res, BatchMetadataToolsGUI.APPLICATION_FRAME, "Media: " + res.getName());
        //md.setVisible(true);
        
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
