package org.jdna.media.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdna.media.IMediaSource;
import org.jdna.media.IMediaSourceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlMediaSourceProvider implements IMediaSourceProvider {
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
		return null;
	}

	public IMediaSource getSource(String name) throws IOException {
		checkAndReload();
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
		DirectoryMediaSource dms = new DirectoryMediaSource();
		String name = item.getAttribute("name");
		if (name==null) {
			throw new IOException("Missing name attribute for source");
		}
		dms.setName(name);
		
		NodeList nl = item.getChildNodes();
		for (int i=0;i<nl.getLength();i++) {
			Node n = nl.item(i);
			if (n.getNodeType()==Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if ("path".equals(e.getNodeName())) {
					// TODO: check to sage: paths, but now, assume all dir paths
					String path = e.getTextContent().trim();
					File file  = new File(path);
					if (!file.exists()) {
						throw new IOException("Invalid Path: " + path + " for media source: " + dms.getName());
					}
					dms.setDirectory(file);
				}
			}
		}
		if (dms.getPath()==null) {
			throw new IOException("Missing <path> element for media source: " + dms.getName());
		}
		
		sources.add(dms);
	}

	private void save() throws IOException {
		throw new IOException("Save not implemented");
	}
}
