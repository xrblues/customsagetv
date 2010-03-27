package org.jdna.media.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.util.BaseBuilder;
import sagex.phoenix.util.XmlUtil;

public class FileMatcherManager {
    private static class FileMatcherXmlBuilder extends BaseBuilder {
        public FileMatcherXmlBuilder(String name) {
            super(name);
        }
        
        private List<FileMatcher> matchers = new ArrayList<FileMatcher>();
        private FileMatcher curMatcher = null;
        
        public List<FileMatcher> getMatchers() {
            return matchers;
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("match".equals(name)) {
                curMatcher = new FileMatcher();
            } else if ("regex".equals(name)) {
            } else if ("file".equals(name)) {
            } else if ("title".equals(name)) {
            } else if ("year".equals(name)) {
            } else if ("metadata".equals(name)) {
                curMatcher.setMetadata(new ID(XmlUtil.attr(attributes, "name", null),null));
                curMatcher.setMediaType(MediaType.toMediaType(XmlUtil.attr(attributes, "type", "movie")));
            } else if ("fanart".equals(name)) {
                curMatcher.setFanart(new ID(XmlUtil.attr(attributes, "name", null),null));
            } else if ("titles".equals(name)) {
            } else {
                warning("Unhandled element: " + name + " in file matcher xml");
            }
        }
        

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if ("match".equals(name)) {
                matchers.add(curMatcher);
                curMatcher = null;
            } else if ("regex".equals(name)) {
                curMatcher.setFileRegex(getData());
            } else if ("file".equals(name)) {
                curMatcher.setFile(new File(getData()));
            } else if ("title".equals(name)) {
                curMatcher.setTitle(getData());
            } else if ("year".equals(name)) {
                curMatcher.setYear(getData());
            } else if ("metadata".equals(name)) {
                curMatcher.getMetadata().setValue(getData());
            } else if ("fanart".equals(name)) {
                curMatcher.getFanart().setValue(getData());
            }
        }
    }
    
    private List<FileMatcher> buildMatchers(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser parser = saxFactory.newSAXParser();
        
        FileMatcherXmlBuilder handler = new FileMatcherXmlBuilder(xmlFile.getAbsolutePath());
        parser.parse(xmlFile, handler);

        return handler.getMatchers();
    }

    
    private Logger log = Logger.getLogger(FileMatcherManager.class);
    
    private File matcherXml = null;
    private List<FileMatcher> matchers = null;
    
    public FileMatcherManager(File matcherXml) {
        loadXml(matcherXml);
    }
    
    private void loadXml(File matcherXml2) {
        this.matcherXml = matcherXml2;
        
        if (this.matcherXml.exists()) {
            try {
                matchers = buildMatchers(matcherXml);
            } catch (Exception e) {
                log.warn("Failed to load matchers xml file: " + matcherXml2.getAbsolutePath(), e);
            }
        }
        
        if (matchers==null) {
            matchers = new ArrayList<FileMatcher>();
        }
    }
    
    public List<FileMatcher> getFileMatchers() {
        return matchers;
    }
    
    public void addMatcher(FileMatcher matcher) {
        getFileMatchers().add(matcher);
    }
    
    /**
     * For the given filePath, attempt to find a match that will identify the metadata for the given item.
     * 
     * @param filePath
     * @return
     */
    public FileMatcher getMatcher(String filePath) {
        if (filePath==null) return null;
        for (FileMatcher m : getFileMatchers()) {
            if (m.getFile()!=null) {
                File f = new File(filePath);
                if (f.equals(m.getFile())) {
                    return m;
                }
            }
            
            if (m.getFileRegex()!=null) {
                Matcher match = m.getFileRegex().matcher(filePath);
                if (match.find()) {
                    return m;
                }
            }
        }
        
        log.debug("No File Matchers for: " + filePath);
        return null;
    }
}
