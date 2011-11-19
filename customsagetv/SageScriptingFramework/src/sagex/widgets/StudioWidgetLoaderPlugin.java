package sagex.widgets;

import gkusnick.sagetv.api.API;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sagex.util.ILog;
import sagex.util.LogProvider;
import tv.sage.StudioPlugin;

public class StudioWidgetLoaderPlugin implements StudioPlugin {
    private ILog log = LogProvider.getLogger(StudioWidgetLoaderPlugin.class);
    private JMenu topMenu = new JMenu("Widget Framework");
    
    private gkusnick.sagetv.api.StudioAPI.Studio studio;
    private final API api;
    
    public StudioWidgetLoaderPlugin(tv.sage.StudioAPI studioAPI) {
        this.api = API.apiLocalUI;
        studio = this.api.studioAPI.Wrap(studioAPI);
    }

    
    public void initMenu() {
        topMenu.removeAll();
        
        JMenuItem item = new JMenuItem("Import Widget Xml");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                importWidgetXml();
            }
        });
        
        topMenu.add(item);
    }
    
    public JMenu GetContextMenu() {
        return null;
    }

    public JMenu GetTopMenu() {
        initMenu();
        return topMenu;
    }

    protected void importWidgetXml() {
        JFileChooser chooser = new JFileChooser(new File("."));
        int val = chooser.showOpenDialog(null);
        if (val == JFileChooser.APPROVE_OPTION) {
            File in = chooser.getSelectedFile();
            if (in==null || !in.exists()) {
                JOptionPane.showMessageDialog(null, "Invalid File: " + in);
                return;
            }
            
            WidgetImporter importer = new WidgetImporter();
            try {
                importer.importWidgets(null, in.toString());
                JOptionPane.showMessageDialog(null, "Widgets have been imported.");
                log.info("Widgets were imported.");
            } catch (ParserConfigurationException e) {
                log.warn("Error", e);
                JOptionPane.showMessageDialog(null, "Failed to parse Xml File; " + e.getMessage());
            } catch (SAXException e) {
                log.warn("Error", e);
                JOptionPane.showMessageDialog(null, "Failed to parse Xml File; " + e.getMessage());
            } catch (IOException e) {
                log.warn("Error", e);
                JOptionPane.showMessageDialog(null, "Failed to Read Xml File; " + e.getMessage());
            } catch (WidgetException e) {
                log.warn("Error", e);
                JOptionPane.showMessageDialog(null, "Failed to Apply Widgets; " + e.getMessage());
            }
        }
    }
}
