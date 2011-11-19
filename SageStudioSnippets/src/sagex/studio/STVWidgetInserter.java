package sagex.studio;

import gkusnick.sagetv.api.API;
import gkusnick.sagetv.api.WidgetAPI.Widget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class STVWidgetInserter extends DefaultHandler {
    private String        fragment      = null;
    private Stack<Widget> stack         = new Stack<Widget>();

    private API           api           = API.apiLocalUI;

    private String        propertyValue = null;

    public STVWidgetInserter(String stvFragment, Widget parent) {
        fragment = "<stv>" + stvFragment + "</stv>";
        if (parent!=null) {
            stack.add(parent);
        }
    }

    public void insert() throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(fragment.getBytes());
        parser.parse(xmlStream, this);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if ("stv".equals(name)) return;

        // TODO: Figure out a better way to handle "Properties"
        if (attributes == null || attributes.getLength() == 0) {
            propertyValue = "";
            return;
        }

        debug("Inserting Widget: " + name);

        // if this is a reference widget, then find the widget, and add as child
        Widget w = null; 
        String wRef = attributes.getValue("Ref");
        String wName = attributes.getValue("Name");
        if (wRef != null && wRef.trim().length() > 0) {
            w = api.widgetAPI.FindWidget(name, wName);
            if (w==null) {
                debug("Invalid Theme Reference: " + wName + " for Element: " + name);
            }
        }
        
        if (w==null) {
            // create the new widget
            w = api.widgetAPI.AddWidget(name);
            if (wName != null && wName.trim().length() > 0) {
                w.SetWidgetName(wName);
            }
        }
        
        // if there is a parent the add this as a child.
        if (stack.size()>0) {
            stack.lastElement().AddWidgetChild(w);
        }
        
        // set the new widget to be the parent
        stack.add(w);
    }

    private void debug(String string) {
        System.out.println("DEBUG: " + string);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // capture property values
        String s = new String(ch, start, length);
        if (propertyValue!=null && s.trim().length()>0) {
            debug("Setting Property Value: " + s);
            propertyValue += s;
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if ("stv".equals(name)) return;

        // handle property elements
        if (propertyValue != null) {
            try {
                if (propertyValue.trim().length() > 0) {
                    Widget w = stack.lastElement();
                    String prop = name;
                    try {
                        debug("SetWidgetProperty Widget: " + w.GetWidgetName() + "; Attr: " + prop + "; Value: " + propertyValue);
                        w.SetWidgetProperty(prop, propertyValue);
                    } catch (Throwable t) {
                        String msg = "Failed to call SetWidgetProperty for widget: " + w.GetWidgetName() + "; Attr: " + prop + "; Value: " + propertyValue;
                        throw new SAXException(msg);
                    }
                }
            } finally {
                propertyValue = null;
            }
        } else {
            stack.pop();
        }
    }

    @Override
    public void error(SAXParseException arg0) throws SAXException {
        debug("ERROR: " + arg0.getMessage());
        throw arg0;
    }

    @Override
    public void fatalError(SAXParseException arg0) throws SAXException {
        debug("FATAL: " + arg0.getMessage());
        throw arg0;
    }
}
