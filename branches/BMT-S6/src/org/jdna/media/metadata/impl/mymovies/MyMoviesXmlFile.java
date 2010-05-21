package org.jdna.media.metadata.impl.mymovies;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyMoviesXmlFile {
    private File                   file       = null;
    private DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder        parser;
    private Document               doc        = null;

    public MyMoviesXmlFile(File xmlFile) throws Exception {
        this.file = xmlFile;
        parser = xmlFactory.newDocumentBuilder();
        doc = parser.parse(file);
    }

    public void visitMovies(MyMoviesNodeVisitor visitor) {
        NodeList nl = doc.getDocumentElement().getChildNodes();
        int s = nl.getLength();
        for (int i = 0; i < s; i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && "Title".equalsIgnoreCase(n.getNodeName())) {
                visitor.visitMovie((Element) n);
            }
        }
    }

    public static String getElementValue(Element el, String tag) {
        NodeList nl = el.getElementsByTagName(tag);
        if (nl.getLength() > 0) {
            Node n = nl.item(0);
            return n.getTextContent().trim();
        }
        return null;
    }

    public Element findMovieById(String id) throws Exception {
        if (id == null) throw new Exception("passed id was null!");
        Element e = null;

        NodeList nl = doc.getDocumentElement().getChildNodes();
        int s = nl.getLength();
        for (int i = 0; i < s; i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && "Title".equalsIgnoreCase(n.getNodeName())) {
                if (id.equals(getElementValue((Element) n, "ID"))) {
                    e = (Element) n;
                    break;
                }
            }
        }

        if (e == null) {
            throw new Exception("Failed to find movie for id: " + id);
        }
        return e;
    }
}
