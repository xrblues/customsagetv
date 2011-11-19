package sagex.studio;

import gkusnick.sagetv.api.API;
import gkusnick.sagetv.api.WidgetAPI;
import gkusnick.sagetv.api.WidgetAPI.Widget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import sagex.studio.Snippet.STV;
import tv.sage.StudioPlugin;

/**
 * Loads a 
 * 
 * @author seans
 *
 */
public class SnippetPluginLoader implements StudioPlugin {

    public class ClickListener implements ActionListener {
        private Snippet snip = null;
        public ClickListener(Snippet snip) {
            this.snip = snip;
        }
        
        public void actionPerformed(ActionEvent e) {
            processSnippetAction(snip);
        }
    }
    
    private JMenu contextMenu = new JMenu("Snippets");
    private JMenu topMenu = new JMenu("Snippets");
    
    private List<Snippet> snippets = new LinkedList<Snippet>();
    
    private gkusnick.sagetv.api.StudioAPI.Studio studio;
    private final API api;
    
    public SnippetPluginLoader(tv.sage.StudioAPI studioAPI) {
        this.api = API.apiLocalUI;
        studio = this.api.studioAPI.Wrap(studioAPI);
    }
    
    public JMenu GetContextMenu() {
        reloadMenus();
        return contextMenu;
    }

    public JMenu GetTopMenu() {
        reloadMenus();
        return topMenu;
    }

    private synchronized void reloadMenus() {
        File dir = new File("studio/snippets");
        File[] snips = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().endsWith(".snip");
            }
        });
        
        for (File f  : snips) {
            loadSnippet(f);
        }
    }
    
    public void loadSnippet(File f) {
        Snippet snip = getSnippet(f);
        if (snip==null) {
            System.out.println("Loading Snippet: " + f.getAbsolutePath());
            snip = new Snippet(f);
            snippets.add(snip);
            snip.reloadSnippet();
            
            if (snip.isTopMenu()) {
                JMenuItem mi = new JMenuItem(snip.getLabel());
                if (snip.getIcon()!=null) {
                    mi.setIcon(new ImageIcon(snip.getIcon()));
                }
                mi.addActionListener(new ClickListener(snip));
                
                topMenu.add(mi);
            }
            
            if (snip.isContextMenu()) {
                JMenuItem mi = new JMenuItem(snip.getLabel());
                if (snip.getIcon()!=null) {
                    mi.setIcon(new ImageIcon(snip.getIcon()));
                }
                mi.addActionListener(new ClickListener(snip));
                
                contextMenu.add(mi);
            }
        }
        
        if (snip.isModified()) {
            snip.reloadSnippet();
        }
    }

    private Snippet getSnippet(File f) {
        for (Snippet s : snippets) {
            if (s.isUsingFile(f)) return s;
        }
        return null;
    }
    
    /**
     * TODO: We need to clean this up....
     * 
     * @param snip
     */
    public void processSnippetAction(Snippet snip) {
        STV stv = snip.getSTV();
        if (stv!=null && stv.isRequireSelection()) {
            // test if we have a selected node, and if so, then carry on
            // TODO: Maybe, at some point, assign a WidgetType the the snippet can act on
            try {
                Object o = studio.GetSingleSelectedWidget();
                if (o==null) {
                    throw new Exception("Selection Required.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(studio.GetJFrame(), "This snippet requires a selected Widget.", "Snippet: " + snip.getLabel(),JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Map<String, Object> values = null;
        String xml = null;
        if (snip.getInputs().size()>0) {
            // show the form dialog
            System.out.println("Showing Snippet Input Dialog");
            SnippetInputDialog dlg = new SnippetInputDialog(studio.GetJFrame(), snip, this);
            dlg.setModal(true);
            dlg.setVisible(true);
            
            if (dlg.isCancelled()) return;
            values = dlg.getValues();
        }
        
        // if there is a script then invoke it...
        ScriptEngineManager manager = null;
        ScriptEngine engine = null;
        try {
            String s = snip.getScript();
            if (s==null || s.trim().length()==0) {
                // no script
            } else {
                manager = new ScriptEngineManager();
                engine = manager.getEngineByName("JavaScript");
                if (values!=null) {
                    // add our form bings to the global scope for convenience
                    engine.getBindings(ScriptContext.GLOBAL_SCOPE).putAll(values);
                }
                
                // add in core bindings to objects we want to expose to the script
                engine.put("form", values);
                engine.put("plugin", this);
                engine.put("snippet", snip);
                engine.put("studio", studio);
                engine.put("api", api);
                
                // evaluate the script
                engine.eval(s);
                
                // check for an onBeforeInsert call
                if (snip.getOnBeforeInsert()!=null) {
                    Invocable inv = (Invocable) engine;

                    // invoke the global function named "hello"
                    Object o = inv.invokeFunction(snip.getOnBeforeInsert());
                    if (o instanceof Boolean) {
                        if (!((Boolean)o).booleanValue()) {
                            System.out.println("Exiting because user function returned false.");
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(studio.GetJFrame(), "Javascript Error.\n\n" + toString(e), "Snippet: " + snip.getLabel(),JOptionPane.ERROR_MESSAGE);
            return;
        }

        // get the stv xml, if there is one
        if (stv!=null) {
            xml = stv.getBody();
        }
        
        // if we have the xml body, and we have some values, then lets replace
        if (xml!=null && values!=null) {
            xml = format(xml, values);
        }
        
        if (xml!=null) {
            try {
                STVWidgetInserter inserter = new STVWidgetInserter(xml, getSelectedWidget(stv));
                inserter.insert();
                studio.RefreshTree();
            } catch (SAXException e) {
                JOptionPane.showMessageDialog(studio.GetJFrame(), "The STV XML cannot be parsed. Please check your xml to ensure that it is valid.\n\n  The Parser Exception is\n" + toString(e), "Snippet: " + snip.getLabel(),JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(studio.GetJFrame(), "Failed for some reason.\n\n Error is\n" + toString(e), "Snippet: " + snip.getLabel(),JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // check for an onAfterInsert call
        if (engine!=null && snip.getOnAfterInsert()!=null) {
            Invocable inv = (Invocable) engine;
            try {
                inv.invokeFunction(snip.getOnAfterInsert());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(studio.GetJFrame(), "Failed to execute the onAfterInsert event.\n\n Error is\n" + toString(e), "Snippet: " + snip.getLabel(),JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private Widget getSelectedWidget(STV stv) {
        Widget w = null;
        if (stv==null || "top".equals(stv.getInsert())) {
            return null;
        }
        
        if (stv.getInsert() == null || "child".equals(stv.getInsert())) {
            try {
                w = studio.GetSelectedWidgetRefs().iterator().next().GetWidget();
            } catch (Throwable t) {
                throw new RuntimeException("Unable to Find an insert point for: " + stv.getInsert());
            }
        } else {
            throw new RuntimeException("Unknown insert location: " + stv.getInsert());
        }
        
        if (w==null) {
            throw new RuntimeException("Failed to find a Widget so that we could attach the fragment.");
        }
        
        return w;
    }

    /**
     * Given a String in the format, ${KEY}..., it will replace all occurances
     * of ${KEY} with a lookup in the Map for KEY.
     * 
     * ie, ${TEST1} -- ${TEST2} would result in Hello -- There, provided that
     * the map contained 2 keys TEST1=Hello, TEST2=There
     * 
     * @param s
     *            Format String
     * @param map
     *            Map of Named Paramters
     * @return
     */
    public static String format(String s, Map<String, Object> props) {
        Pattern p = Pattern.compile("(\\$\\{[_\\.a-zA-Z]+\\})");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();

        int lastStart = 0;
        while (m.find()) {
            String token = m.group(0);
            sb.append(s.substring(lastStart, m.start()));
            lastStart = m.end();
            String key = token.substring(2, token.length() - 1);
            String val = toString(props.get(key));
            sb.append(val);
        }

        sb.append(s.substring(lastStart));

        return sb.toString();
    }

    private static String toString(Throwable t) {
        if (t==null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    private static String toString(Object o) {
        if (o==null) return "";
        String val = String.valueOf(o);
        
        // now update for xml entities
        val = val.replaceAll("&", "&amp;");
        val = val.replaceAll("\"", "&quot;");
        val = val.replaceAll("<", "&lt;");
        val = val.replaceAll(">", "&gt;");
        return val;
    }
    
    
    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
                f.setSize(400, 600);
                
                SnippetPluginLoader plugins = new SnippetPluginLoader(new StubStudioAPI());
                JMenuBar mb =new JMenuBar();
                mb.add(plugins.GetContextMenu());
                f.setJMenuBar(mb);
                f.setVisible(true);
            }
        });
    }

    public boolean confirm(String msg) {
        if (JOptionPane.showConfirmDialog(studio.GetJFrame(), msg)== JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
    
    public void info(String msg) {
        JOptionPane.showMessageDialog(studio.GetJFrame(), msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void alert(String msg) {
        JOptionPane.showMessageDialog(studio.GetJFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void logDebug(String msg) {
        System.out.println("DEBUG: " + msg);
    }

    public void logError(String msg) {
        System.out.println("DEBUG: " + msg);
    }
}
