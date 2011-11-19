package sagex.plugintools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import sagex.SageAPI;
import sagex.plugintools.http.HTTPD;
import sagex.stub.StubSageAPI;
import sagex.util.ILog;
import sagex.util.LogProvider;

public class PluginTool {
    private ILog log = LogProvider.getLogger(PluginTool.class);
	private String httpServer;
	private boolean removeAfterLoad;
    
    public PluginTool(String httpServer, boolean removeAfterLoad) {
    	this.httpServer = httpServer;
    	this.removeAfterLoad = removeAfterLoad;
    }
    
    public synchronized void mergePlugins() throws Exception {
        File pluginFile = new File("../SageTVPluginsDev.xml");
        if (!pluginFile.exists()) {
        	pluginFile.createNewFile();
        	FileWriter fw = new FileWriter(pluginFile);
        	fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<PluginRepository>\n</PluginRepository>");
        	fw.flush();
        	fw.close();
        }
        
        SAXReader reader = new SAXReader();
        Document document = reader.read(pluginFile);
        log.info("Loaded " + pluginFile);
        
        File devDir = new File(".");
        File files[] = devDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xml");
            }
        });

        for (File f : files) {
            log.info("Loading Plugin: " + f);
            Document plugin = reader.read(f);
            String id = plugin.getRootElement().element("Identifier").getText();
            removePlugins(document, id);
            plugin.getRootElement().addComment("This Plugin inserted via DeveloperPlugin Tools");
            if (httpServer!=null) {
                replaceHTTPServerInLocation(plugin, httpServer);
            } else {
                log.info("Did not replace Locations for plugin: " + id + " since Location replacement is disabled.");
            }
            document.getRootElement().add(plugin.getRootElement());
            log.info("Added Plugin: " + f);
        }
        
        log.info("Saving: " + pluginFile);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( new FileWriter(pluginFile), format );
        writer.write( document );
        writer.flush();
        writer.close();
        log.info("Saved: " + pluginFile);
        
        // now remove the developer plugin files
        if (removeAfterLoad) {
            for (File f : files) {
                log.info("Removing File: " + f);
                if (!f.delete()) {
                    log.warn("Failed to remove file: " + f);
                }
            }
        } else {
            log.info("Removing of Developer Plugin Files has been disabled, so they will not be cleaned up.");
        }
    }
    
    private void replaceHTTPServerInLocation(Document doc, final String http) {
        final Pattern p = Pattern.compile("/([^/]*.zip)", Pattern.CASE_INSENSITIVE);
        doc.accept(new VisitorSupport() {
            /* (non-Javadoc)
             * @see org.dom4j.VisitorSupport#visit(org.dom4j.Element)
             */
            @Override
            public void visit(Element node) {
                if ("Location".equals(node.getName())) {
                    String text = node.getTextTrim();
                    Matcher m = p.matcher(text);
                    if (m.find()) {
                        text = http + m.group(1);
                    } else {
                        log.warn("Failed to match: " + p + " for " + text);
                    }
                    node.setText(text);
                }
            }
        });
    }

    private void removePlugins(final Document doc, final String id) {
        final List<Element> toRemove = new ArrayList<Element>();
        doc.accept(new VisitorSupport() {
            /* (non-Javadoc)
             * @see org.dom4j.VisitorSupport#visit(org.dom4j.Element)
             */
            @Override
            public void visit(Element node) {
                if ("Identifier".equals(node.getName()) && node.getText().equals(id)) {
                    toRemove.add(node.getParent());
                }
            }
        });
        
        for (Element e: toRemove) {
            log.info("Making room for: " + id);
            if (!doc.getRootElement().remove(e)) {
                log.warn("failed to remove previous: " + id);
            }
        }
    }
    
    public static boolean MergePlugins(String httpServer, boolean removeAfterLoad) {
        boolean worked=false;
        PluginTool pt = new PluginTool(httpServer, removeAfterLoad);
        try {
            pt.mergePlugins();
            worked=true;
        } catch (Exception e) {
            LogProvider.getLogger(PluginTool.class).warn("Failed to merge plugins", e);
        }
        return worked;
    }
    
    public static void main(String args[]) throws Exception {
    	System.out.println("DevPluginHelper [--http HTTP_SERVER --remove true|false]");
    	System.out.println("  HTTP_SERVER is a local http that hosts your plugin");
    	System.out.println("  if remove is true, then your .xml files will be removed when loaded");
    	System.out.println();
    	
    	boolean remove = false;
    	String http = "http://localhost:8000/";
    	
    	if (args !=null && args.length>0) {
    		for (int i=0;i<args.length;i++) {
    			if ("--http".equals(args[i])) {
    				http = args[++i];
    				continue;
    			}
    			
    			if ("--remove".equals(args[i])) {
    				remove = Boolean.parseBoolean(args[++i]);
    				continue;
    			}
    		}
    	}
    	
    	SageAPI.setProvider(new StubSageAPI());
        LogProvider.useSystemOut();
        if (!PluginTool.MergePlugins(http, remove)) {
            System.out.println("Failed");
        } else {
        	HTTPD.startServer(8000, ".");
        }
    }
}
