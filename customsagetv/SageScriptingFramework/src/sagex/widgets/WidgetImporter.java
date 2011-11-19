package sagex.widgets;

import gkusnick.sagetv.api.API;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sagex.UIContext;
import sagex.api.Global;
import sagex.api.WidgetAPI;
import sagex.util.ILog;
import sagex.util.LogProvider;

public class WidgetImporter {
    private ILog                   log           = LogProvider.getLogger(WidgetImporter.class);
    private UIContext              ctx           = null;
    private DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();

    private class Var {
        private String name;
        private String value;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Var other = (Var) obj;
            if (!getOuterType().equals(other.getOuterType())) return false;
            if (name == null) {
                if (other.name != null) return false;
            } else if (!name.equals(other.name)) return false;
            return true;
        }

        private WidgetImporter getOuterType() {
            return WidgetImporter.this;
        }
    }

    private Stack<Set<Var>> scopedVars = new Stack<Set<Var>>();

    public WidgetImporter() {
    }

    public void importWidgets(String uictx, String file) throws ParserConfigurationException, SAXException, IOException, WidgetException {
        if (uictx == null) {
            String names[] = Global.GetUIContextNames();
            if (names.length > 0) {
                uictx = names[0];
            }
        }
        ctx = new UIContext(uictx);

        log.info("importWidgets Called: " + uictx + "; File: " + file);

        DocumentBuilder builder = parserFactory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));

        // it parsed, now process the document
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        if (nodes.getLength() == 0) {
            throw new WidgetException("Document contains no importable information", file);
        }

        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                applyNode((Element) node, doc, null);
            }
        }
    }

    private Object applyNode(Element node, Document doc, Object widgetParent) throws WidgetException {
        // log.debug("Applying Node: " + node);
        String method = node.getNodeName() + "Handler";

        String varScopeId = node.getAttribute("scopeVarId");
        if (varScopeId != null) {
            scopedVars.add(new LinkedHashSet<Var>());
        }

        try {
            Method m = this.getClass().getMethod(method, Element.class, Document.class, Object.class);
            Object widget = m.invoke(this, node, doc, widgetParent);

            // if there are scoped vars, then add those vars as the first
            // children to the
            if (varScopeId != null) {
                // apply those vars to this widget
                for (Var v : scopedVars.pop()) {
                    Object var = WidgetAPI.AddWidget(ctx, "Variable");
                    WidgetAPI.SetWidgetName(ctx, var, v.name);
                    WidgetAPI.SetWidgetProperty(ctx, var, "Value", v.value);
                    WidgetAPI.AddWidgetChild(ctx, widget, var);
                }
            }

            return widget;
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
     * Attach Handler finds a Widget Section in our DOM and Add Attaches it to
     * SageTV Widget Tree, applying all necessary transformations along the way.
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
        if (content == null) {
            throw new WidgetException("Invalid Widget Reference in our Document for widget id: " + node.getAttribute("widget"));
        }

        Object insertionPoint = WidgetAPI.FindWidgetBySymbol(ctx, node.getAttribute("reference"));

        if (insertionPoint == null) {
            throw new WidgetException("Could not locate the Insertion Widget Reference for: " + node.getAttribute("reference"));
        }

        // now process the inserted Node, and then attach those results as
        // indicated by the location

        // note the parent is null, for now, we'll attach it later.
        Object widget = applyNode((Element) content, doc, null);

        if (widget == null) {
            throw new WidgetException("Failed to create a valid SageTV widget for: " + content);
        }

        String location = node.getAttribute("location");
        if ("child".equals(location)) {
            log.info("Attaching Widget " + widget + " to insertion point: " + node.getAttribute("reference"));
            WidgetAPI.AddWidgetChild(ctx, insertionPoint, widget);
        } else {
            throw new WidgetException("Invation Attach location: " + location);
        }
    }

    public Object buttonHandler(Element node, Document doc, Object parent) throws WidgetException {
        Object w = newWidget("Item", node);
        if (parent != null) {
            WidgetAPI.AddWidgetChild(ctx, parent, w);
        }

        Object label = newWidget("Text", node);
        WidgetAPI.SetWidgetName(ctx, label, node.getAttribute("label"));
        WidgetAPI.AddWidgetChild(ctx, w, label);

        // TODO: Set other widget props
        // TODO: Add Actions

        String showId = node.getAttribute("showId");
        if (showId != null) {
            processNodeId(showId, doc, parent);
        }

        return w;
    }

    private void processNodeId(String showId, Document doc, Object parent) throws WidgetException {
        Object ref = WidgetAPI.FindWidgetBySymbol(ctx, showId);
        if (ref != null) {
            // process a WidgetRef
            throw new WidgetException("WidgetRefs are not Implemented!");
        } else {
            Element n = getElementById(doc.getDocumentElement(), showId);
            if (n == null) throw new WidgetException("Missing Widget Reference and/or Document Element Id Reference: " + showId);
            applyNode(n, doc, parent);
        }
    }

    private Element getElementById(Element doc, String value) {
        return findElementByAttribute(doc, "id", value);
    }

    private Element findElementByAttribute(Element doc, String attr, String value) {
        if (value.equals(doc.getAttribute(attr))) {
            return doc;
        }

        if (!doc.hasChildNodes()) return null;

        NodeList nl = doc.getChildNodes();
        int size = nl.getLength();
        for (int i = 0; i < size; i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = findElementByAttribute((Element) n, attr, value);
                if (e != null) {
                    return e;
                }
            }
        }
        return null;
    }

    private Object newWidget(String name, Element node) throws WidgetException {
        Object w = WidgetAPI.AddWidget(ctx, name);
        if (w == null) {
            throw new RuntimeException("Failed to create a new Widget: " + name);
        }

        log.debug("Created New Sage Widget: " + name + " in context: " + ctx);
        return w;
    }
}
