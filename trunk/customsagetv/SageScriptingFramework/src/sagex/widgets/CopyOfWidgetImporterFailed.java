package sagex.widgets;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sagex.UIContext;
import sagex.api.WidgetAPI;
import sagex.util.ILog;
import sagex.util.LogProvider;

public class CopyOfWidgetImporterFailed {
    private ILog log = LogProvider.getLogger(CopyOfWidgetImporterFailed.class);
    private UIContext ctx = null;
    private DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
    public CopyOfWidgetImporterFailed() {
        //Log4jConfigurator.configureQuietly("sagex-widgets");
    }
    
    public void importWidgets(String uictx, String file) throws ParserConfigurationException, SAXException, IOException, WidgetException {
        if (uictx==null) uictx = UIContext.SAGETV_PROCESS_LOCAL_UI.getName();
        ctx = new UIContext(uictx);
        
        log.info("Widget Importer called; UI: " + uictx + "; File: " + file);
        
        DocumentBuilder builder = parserFactory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));
        
        // it parsed, now process the document
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        if (nodes.getLength()==0) {
            throw new WidgetException("Document contains no importable information", file);
        }
        
        int size= nodes.getLength();
        for (int i=0;i<size;i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                applyNode((Element) node, doc, null);
            }
        }
    }

    private Object applyNode(Element node, Document doc, Object widgetParent) throws WidgetException {
        //log.debug("Applying Node: " + node);
        String method = node.getNodeName() + "Handler";
        try {
            Method m = this.getClass().getMethod(method, Element.class, Document.class, Object.class);
            return m.invoke(this, node, doc, widgetParent);
        } catch (SecurityException e) {
            log.warn("Unable to call method: " + method, e);
            throw new WidgetException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.warn("No Method Handler called " + method, e);
            throw new WidgetException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid Arugments for method: " + method, e);
            throw new WidgetException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.warn("Unable to call method: " + method, e);
            throw new WidgetException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.warn("Error while calling method: " + method, e.getTargetException());
            throw new WidgetException(e.getMessage(), e.getTargetException());
        }
    }
    
    public void widgetsHandler(Element node, Document doc, Object parent) throws WidgetException {
        log.info("Widgets Handler is just a placeholder");
    }
    
    public void scriptHandler(Element node, Document doc, Object parent) throws WidgetException {
        log.info("Apply Script: " + node);
    }

    public void popupPanelHandler(Element node, Document doc, Object parent) throws WidgetException {
        log.info("Apply Panel: " + node);
    }

    /**
     * Attach Handler finds a Widget Section in our DOM and Add Attaches it to SageTV Widget Tree, applying
     * all necessary transformations along the way.
     * 
     * @param node
     * @param doc
     * @param ctx
     * @throws WidgetException
     */
    public void attachHandler(Element node, Document doc, Object parent) throws WidgetException {
        log.info("Apply Attachment: " + node);
        
        // find the content that we are going to insert...
        Node content = getElementById(doc.getDocumentElement(), node.getAttribute("widget"));
        if (content==null) {
            throw new WidgetException("Invalid Widget Reference in our Document for widget id: " + node.getAttribute("widget"));
        }
        
        Object insertionPoint = WidgetAPI.FindWidgetBySymbol(node.getAttribute("reference"));
        if (insertionPoint==null) {
            throw new WidgetException("Could not locate the Insertion Widget Reference for: " + node.getAttribute("reference"));
        }
        
        // now process the inserted Node, and then attach those results as indicated by the location
        
        // note the parent is null, for now, we'll attach it later.
        Object widget = applyNode((Element)content, doc, null);
     
        if (widget==null) {
            throw new WidgetException("Failed to create a valid SageTV widget for: " + content);
        }
        
        String location = node.getAttribute("location");
        if ("child".equals(location)) {
            WidgetAPI.AddWidgetChild(insertionPoint, widget);
            log.info("Attached Widget: " + widget + " at location " + node.getAttribute("reference"));
        } else {
            throw new WidgetException("Invation Attach location: " + location);
        }
    }
    
    
    public Object buttonHandler(Element node, Document doc, Object parent) throws WidgetException {
        Object w = WidgetAPI.AddWidget("Item");
        //setCommonWidgetProperties(w, node);
        if (parent!=null) {
            WidgetAPI.AddWidgetChild(parent, w);
        }
        
        Object label = WidgetAPI.AddWidget("Text");
        WidgetAPI.SetWidgetName(label, node.getAttribute("label"));
        WidgetAPI.AddWidgetChild(w, label);

        log.debug("Button Created: " + node.getAttribute("label"));
        
        // TODO: Add Actions
        return w;
    }
    
    private void setCommonWidgetProperties(Object w, Element node) {
        // can't set the Sym property, how do we set it?
        // setStringProperty(w, "Sym", node, "id");
        
        //String name = node.getAttribute("name");
        //String comment = node.getAttribute("comment");
        //String label = node.getAttribute("label");
        //if (name==null) name=comment;
        //if (name==null) name=label;
        //if (name==null) name=node.getNodeName();
        //WidgetAPI.SetWidgetName(ctx, w, name);
        
        //setStringProperty(w, "Insets", node, "insets");
    }
    
    private void setStringProperty(Object w, String PropertyName , Element node, String attr) {
        String v = node.getAttribute(attr);
        if (v==null || v.trim().length()==0) return;
        WidgetAPI.SetWidgetProperty(w, PropertyName, v);
    }

    private Node getElementById(Element doc, String value) {
        return findElementByAttribute(doc, "id", value);
    }

    private Element findElementByAttribute(Element doc, String attr, String value) {
        if (value.equals(doc.getAttribute(attr))) {
            return doc;
        }
        
        if (!doc.hasChildNodes()) return null;

        NodeList nl = doc.getChildNodes();
        int size=nl.getLength();
        for (int i=0;i<size;i++) {
            Node n = nl.item(i);
            if (n.getNodeType()==Node.ELEMENT_NODE) {
                Element e = findElementByAttribute((Element)n, attr, value);
                if (e!=null) {
                    return e;
                }
            }
        }
        return null;
    }
}
