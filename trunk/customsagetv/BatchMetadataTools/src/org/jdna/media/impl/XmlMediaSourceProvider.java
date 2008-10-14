package org.jdna.media.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaSource;
import org.jdna.media.IMediaSourceProvider;
import org.jdna.media.MediaResourceFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlMediaSourceProvider implements IMediaSourceProvider {
	private static final Logger log = Logger.getLogger(XmlMediaSourceProvider.class);
	
	private File xmlFile = null;
	private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private long lastModified = -1;
	private List<IMediaSource> sources = new ArrayList<IMediaSource>();
	
	public XmlMediaSourceProvider(File xmlSources) {
		this.xmlFile = xmlSources;
	}

	public IMediaSource addSource(IMediaSource source) throws IOException {
		checkAndReload();
		sources.add(source);
		save();
		return source;
	}

	public IMediaSource getSource(String name) throws IOException {
		checkAndReload();
		
		for (IMediaSource ms : sources) {
			if (name.equals(ms.getName())) {
				return ms;
			}
		}
		
		return null;
	}

	public IMediaSource[] getSources() throws IOException {
		checkAndReload();
		return sources.toArray(new IMediaSource[sources.size()]);
	}

	public IMediaSource removeSource(IMediaSource source) throws IOException {
		checkAndReload();
		sources.remove(source);
		save();
		return source;
	}

	private void checkAndReload() throws IOException {
		if (xmlFile==null) {
			throw new IOException("File Not set.");
		}
		if (!xmlFile.exists()) {
			return;
		}
		if (xmlFile.lastModified() > lastModified) {
			load();
		}
	}

	private void load() throws IOException {
		sources.clear();
		DocumentBuilder parser;
		try {
			parser = docFactory.newDocumentBuilder();
			Document doc = parser.parse(xmlFile);
			
			NodeList nl = doc.getElementsByTagName("source");
			for (int i=0;i<nl.getLength();i++) {
				try {
					addSourceNode((Element) nl.item(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			lastModified = xmlFile.lastModified();
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	private void addSourceNode(Element item) throws IOException {
		String path = null;
		String name = item.getAttribute("name");
		if (name==null) {
			throw new IOException("Missing name attribute for source");
		}
		
		NodeList nl = item.getChildNodes();
		for (int i=0;i<nl.getLength();i++) {
			Node n = nl.item(i);
			if (n.getNodeType()==Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if ("locationUri".equals(e.getNodeName())) {
					path = e.getTextContent().trim();
				}
			}
		}
		
		if (path==null) {
			throw new IOException("Missing <path> element for media source: " + name);
		}
		
		IMediaSource ms = createSource(name, path);
		sources.add(ms);
	}

	private void save() throws IOException {
		if (!xmlFile.exists()) {
			xmlFile.getParentFile().mkdirs();
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter(xmlFile));
		
		pw.println("<sources>");
		for (IMediaSource ms : sources) {
			pw.printf("   <source name=\"%s\">", ms.getName());
			pw.printf("      <locationUri>%s</locationUri>", ms.getLocationUri());
			pw.println("   </source>");
		}
		pw.println("</sources>");
		
		pw.flush();
		pw.close();
		
		log.info("Saved Xml Media Sources: " + xmlFile.getAbsolutePath());
	}

	public IMediaSource createSource(String name, String uri) throws IOException {
		IMediaResource r = MediaResourceFactory.getInstance().createResource(uri);
		MediaSource ms = new MediaSource(name, r.getLocationUri());
		return ms;
	}
	
	public boolean containsSource(IMediaSource s) {
		try {
			for (IMediaSource ms : getSources()) {
				if (ms.getName().equalsIgnoreCase(s.getName())) return true;
				if (ms.getLocationUri().equals(s.getLocationUri())) return true;
			}
		} catch (Exception e) {
			// nevermind
		}
		return false;
	}
}
