package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;
import org.jdna.metadataupdater.gui.MediaDialog;

public class TestSwingLauncher extends JFrame {
    public TestSwingLauncher() {
        super("Test Swing");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        getContentPane().setLayout(new MigLayout());
        
        JButton b = new JButton("Click");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doAction();
            }
        });
        
        getContentPane().add(b);
        pack();
    }
    
    
    public void doAction() {
        try {
            IMediaResource mr = MediaResourceFactory.getInstance().createResource(new File("/home/seans/DevelopmentProjects/workspaces/sage/MovieMetadataUpdater/testing/Movies/Batman Begins.avi").toURI());
            MediaDialog md = new MediaDialog(mr, this, "Media file");
            md.setVisible(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestSwingLauncher().setVisible(true);
            }
        });
    }
}
