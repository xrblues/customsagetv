package org.jdna.sage.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class to support parsing and reading XML data for Sage media files
 * based on the sageshowinfo.dtd
 * See <a href="http://tools.assembla.com/sageplugins/wiki/SageXmlInfo">Sage XML Show Info in the Wiki</a>
 * 
 * <h2>Usage:</h2> 
 * <ul>
 *    <li>Construct;
 *    <li>call a read() method
 *    <li>check isReadOk(), or catch an exception from read()
 *    <li>retrieve the imported data using the getXxxx() calls
 *  </ul>
 *  Duplicate objects will be ignored
 *  
 * @author nielm
 * 
 */
public class SageXmlReader {
    
   
    public SageXmlReader(File localDTD) {
        if ( localDTD!=null && localDTD.canRead())
            this.localDTD=localDTD;
    }
    
    static final int MAX_PARSING_ERRORS=100;
    

    File localDTD=null;
    String lastError=null;
    boolean readOk=false;
    Map<Integer, Channel> channels=new java.util.TreeMap<Integer, Channel>();
    Map<Integer, Favorite> favorites=new java.util.TreeMap<Integer, Favorite>();;
    Map<String, Show> shows=new java.util.TreeMap<String, Show>();
    List<Airing> airingsWithMediaFiles=new java.util.LinkedList<Airing>();
    List<Airing> airingsWithoutMediaFiles=new java.util.LinkedList<Airing>();

    /**
     * Get the imported channels
     * 
     * @return a java.util.Map of Integer ChannelID<==>{@link Channel} Objects
     */
    public Map<Integer, Channel> getChannels() {
        return java.util.Collections.unmodifiableMap(channels);
    }

    /**
     * Get the imported Favorites
     * 
     * @return a java.util.Map of Integer FavoriteID<==>{@link Favorite} Objects
     */
    public Map<Integer, Favorite> getFavorites() {
        return java.util.Collections.unmodifiableMap(favorites);
    }
    /**
     * Get the imported Shows
     * 
     * @return a java.util.Collection of {@link Show} Objects
     */
    public java.util.Collection<Show> getShows() {
        return java.util.Collections.unmodifiableCollection(shows.values());
    }
    /**
     * Get the imported Airings with {@link MediaFiles}
     * 
     * @return a java.util.List of {@link Airing} Objects that have a {@link MediaFile}
     */
    public List<Airing> getAiringsWithMediaFiles() {
        return java.util.Collections.unmodifiableList(airingsWithMediaFiles);
    }
    /**
     * Get the imported Airings without MediaFiles
     * 
     * @return a java.util.List of {@link Airing} Objects
     */
    public List<Airing> getAiringsWithoutMediaFiles() {
        return java.util.Collections.unmodifiableList(airingsWithoutMediaFiles);
    }
    
    
    /**
     * Gets a String representing the last read error
     */
    public String getLastError() {
        return lastError;
    }
    /**
     * Gets a Returns the status of the read
     */
    public boolean isReadOk() {
        return readOk;
    }

    /**
     * Read and parse XML data from a File
     * 
     *  @return true if successful, Exception otherwise
     */
    public boolean read(File inf) throws Exception {
        try {
            InputStream ins = new FileInputStream(inf);
            return read(ins);
        } catch (Exception e) {
            lastError=e.toString();
            throw e;
        }
    }
    /**
     * Read and parse XML data from an InputStream
     * 
     *  @return true if successful, Exception otherwise
     */
    public boolean read(InputStream ins) throws Exception {
        return process(ins);
    }
    
    /**
     * Read and parse XML data from a String
     * 
     *  @return true if successful, Exception otherwise
     */
    public boolean read(String inp) throws Exception {
        return process(inp);
    }
    
    private boolean process(Object input) throws Exception {
        Document doc=null;
        readOk=false;
        lastError=null;
        int numerrors=0;
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            
            // Create the builder and parse the file
            DocumentBuilder builder = factory.newDocumentBuilder();
            SageXMLResolver resolver=new SageXMLResolver(localDTD);
            builder.setEntityResolver(resolver);
            SageXMLErrorHander handler=new SageXMLErrorHander();
            builder.setErrorHandler(handler);
            if ( input instanceof String){
                doc=builder.parse(new InputSource(new java.io.StringReader((String)input)));
            } else if ( input instanceof java.io.InputStream){
                doc=builder.parse((java.io.InputStream)input);
            } else {
                throw new IllegalArgumentException("invalid argument class "+input.getClass());
            }
            if (handler.errors>0) {
                lastError=handler.errors+" XML Errors during parsing\n"+handler.errorStrings;
                return false;
            }
            if (handler.warnings>0)
                lastError=handler.warnings+" Warnings during parsing\n"+handler.errorStrings;
        } 
        catch (SAXParseException e) 
        {
            // A parsing error occurred; the xml input is not valid
            String location = "line " + Integer.toString(e.getLineNumber());
            if (e.getColumnNumber() >= 0)
                location += " col " + Integer.toString(e.getColumnNumber());
            lastError="Failed to parse XML\n" +
                    "error at "+location+": "+e.getMessage();
            return false;
        } 
        catch (SAXException e) 
        {
            // A parsing error occurred; the xml input is not valid
            lastError="Failed to parse XML "+e.toString();
            return false;
        }
        catch (ParserConfigurationException e) 
        {
            System.err.println("An unexpected ParserConfigurationException occured: " + e);
            e.printStackTrace();
            lastError="Internal Failure -- see logging";
            return false;
        } 
        catch (IOException e) 
        {
            System.err.println("An unexpected IO error occured when parsing " + e);
            e.printStackTrace();
            lastError="Failed to read XML "+e.toString();
            return false;
        }
        if ( doc == null ) {
            lastError="Failed to read XML";
            return false;
        }
        if ( ! doc.getDoctype().getPublicId().startsWith(SageXML.SAGEXML_DTD_PUBLIC_BASE)){
            lastError="Not a SageXML document: Invalid XML document type: "+doc.getDoctype().getPublicId();
            return false;
        }
        if ( ! doc.getDocumentElement().getNodeName().equals("sageShowInfo") )
        {
            lastError="Not a SageXML document: Invalid root element: "+doc.getDocumentElement().getNodeName();
            return false;
        }
        String version=doc.getDocumentElement().getAttribute("version");
        java.util.List<String> supportedVersList=java.util.Arrays.asList(SageXML.SAGEXML_SUPPORTED_VERSIONS);
        if ( !supportedVersList.contains(version)) {
            lastError="SageXML version "+version+" is not supported (requires one of "+supportedVersList+")";
            return false;
        }
        
        // parse doc into classes
        StringBuffer errorsBuff=new StringBuffer();
        
        // first get channels
        Element[] channelListNodes=getChildElementsByTagName(doc.getDocumentElement(),"channelList");
        if ( channelListNodes!=null && channelListNodes.length>0) {
            Element[] channelElems=getChildElementsByTagName(channelListNodes[0],"channel");
            if ( channelElems!= null && channelElems.length>0 ) {
                for ( int i = 0 ; i < channelElems.length; i ++ ){
                    try {
                        Channel channel=new Channel((Element)channelElems[i]);
                        channels.put(new Integer(channel.getId()),channel);
                    } catch (XmlParseException e) {
                        errorsBuff.append(e.toString());
                        errorsBuff.append('\n');
                        numerrors++;
                        if ( numerrors>MAX_PARSING_ERRORS){
                            lastError="Too many parsing errors:\n"+errorsBuff.toString();
                            return false;
                        }
                    }
                }
            }
        }
        
        // then faves
        favorites=new java.util.TreeMap<Integer, Favorite>();
        Element[]  faveListNodes=getChildElementsByTagName(doc.getDocumentElement(),"favoriteList");
        if ( faveListNodes!=null && faveListNodes.length>0) {
            Element[]  faveElems=getChildElementsByTagName(faveListNodes[0],"favorite");
            if ( faveElems!= null && faveElems.length>0 ) {
                for ( int i = 0 ; i < faveElems.length; i ++ ){
                    try {
                        Favorite favorite=new Favorite((Element)faveElems[i]);
                        favorites.put(new Integer(favorite.getId()),favorite);
                    } catch (XmlParseException e) {
                        errorsBuff.append(e.toString());
                        errorsBuff.append('\n');
                        numerrors++;
                        if ( numerrors>MAX_PARSING_ERRORS){
                            lastError="Too many parsing errors:\n"+errorsBuff.toString();
                            return false;
                        }
                    }
                }
            }
        }
        
        // then shows with their child Airings/MediaFiles.
        Element[] showListNodes=getChildElementsByTagName(doc.getDocumentElement(),"showList");
        if ( showListNodes!=null && showListNodes.length>0) {
            Element[] showElems=getChildElementsByTagName(showListNodes[0],"show");
            if ( showElems!= null && showElems.length>0 ) {
                for ( int i = 0 ; i < showElems.length; i ++ ){
                    try {
                        Show show=new Show((Element)showElems[i],shows.keySet());
                        shows.put(show.getExtId(),show);

                        // append any airings/mediafiles to lists
                        if ( show.getAiringList()!=null ){
                            for (Airing airing : show.getAiringList()){
                                if ( airing.getMediaFile()!=null){
                                    airingsWithMediaFiles.add(airing);
                                } else {
                                    airingsWithoutMediaFiles.add(airing);
                                }
                            }
                                
                        }
                            
                    } catch (XmlParseException e) {
                        errorsBuff.append(e.toString());
                        errorsBuff.append('\n');
                        numerrors++;
                        if ( numerrors>MAX_PARSING_ERRORS){
                            lastError="Too many parsing errors:\n"+errorsBuff.toString();
                            return false;
                        }
                    }
                }
            }
        }
        if ( errorsBuff.length()>0)
           lastError=errorsBuff.toString();
        readOk=true;
        return readOk;
    }
    
    /**
     * helper function to get text children of a node.
     * <code>for [Element][child]#text[/child][/element]
     * @param node
     * @param childName
     * @return String/null
     */
    static String getChildTextElementValue(Element node, String childName){
        Element[] children=getChildElementsByTagName(node,childName);
        if ( children!=null && children.length >0){
            return getChildTextValues(children[0]);
        }
        return null;
    }
    /**
     * helper function to concatenate all text child nodes of an element
     * @param node
     * @return String/null
     */
    static String getChildTextValues(Element node){
        NodeList children=node.getChildNodes();
        if ( children==null )
            return null;
        StringBuffer stb=new StringBuffer();
        for ( int child=0; child < children.getLength(); child++){
            Node childNode=children.item(child);
            if ( childNode.getNodeType()==Node.TEXT_NODE){
                stb.append(childNode.getNodeValue());
                stb.append(" ");
            }
        }
        String retval=stb.toString().trim();
        if ( retval.length()==0)
            return null;
        else 
            return retval;
    }
    /**
     * helper function to get a NodeList of child elements by tag name
     * @param node
     * @param childName
     * @return
     */
    static  Element[] getChildElementsByTagName(Element node, String tagName){
        NodeList children=node.getChildNodes();
        if ( children == null)
            return null;
        LinkedList<Element> list=new LinkedList<Element>();
        for (int nodeNum=0;nodeNum<children.getLength(); nodeNum++){
            Node child=children.item(nodeNum);
            if ( child.getNodeType()==Node.ELEMENT_NODE 
                 && ((Element)child).getTagName().equals(tagName))
                list.add((Element)child);
        }
        if ( list.size()>0){
            return list.toArray(new Element[0]);
        }
        return null;
    }
}
class SageXMLErrorHander extends org.xml.sax.helpers.DefaultHandler {
    public int errors;
    public int warnings;
    public String errorStrings="";
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException arg0) throws SAXException {
        errors++;
        String location = "line " + Integer.toString(arg0.getLineNumber());
        if (arg0.getColumnNumber() >= 0)
            location += " col " + Integer.toString(arg0.getColumnNumber());
        errorStrings += "XML Parse error at line " + location + ": "
                + arg0.getMessage() + "\n";
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException arg0) throws SAXException {
        String location = "line " + Integer.toString(arg0.getLineNumber());
        if (arg0.getColumnNumber() >= 0)
            location += " col " + Integer.toString(arg0.getColumnNumber());
        errorStrings += "Fatal XML Parse error at line " + location + ": "
                + arg0.getMessage() + "\n";
        throw arg0;
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException arg0) throws SAXException {
        warnings++;
        String location = "line " + Integer.toString(arg0.getLineNumber());
        if (arg0.getColumnNumber() >= 0)
            location += " col " + Integer.toString(arg0.getColumnNumber());
        errorStrings += "XML Parse Warning at line " + location + ": "
                + arg0.getMessage() + "\n";
    }
}

class SageXMLResolver implements EntityResolver {
    File localDTD;
    SageXMLResolver(File localDTD){
        this.localDTD=localDTD;
    }
  public InputSource resolveEntity (String publicId, String systemId)
  {
      try {
          if ( publicId.startsWith(SageXML.SAGEXML_DTD_PUBLIC_BASE)) {
              InputStream is; 

              if ( localDTD!=null && localDTD.canRead()){
                  is=new FileInputStream(localDTD);
                  return new InputSource(is);
              } else {
                  // check version and load from JAR or filesys
                  String version=publicId.substring(SageXML.SAGEXML_DTD_PUBLIC_BASE.length()).replaceAll("//.*$", "");
                  java.util.List<String> supportedVersList=java.util.Arrays.asList(SageXML.SAGEXML_SUPPORTED_VERSIONS);
                  if ( supportedVersList.contains(version)) {

                      ClassLoader cl = this.getClass().getClassLoader();
                      is = cl.getResourceAsStream(SageXML.SAGEXML_DTD_LOCAL);
                      if ( is!=null ){
                          return new InputSource(is);
                      } else {
                          // JAR read failed, attempt filesystem
                          File dtdFile=new File(SageXML.SAGEXML_DTD_LOCAL);
                          if ( dtdFile.canRead()) {

                              is=new FileInputStream(dtdFile);

                              return new InputSource(is);
                          }
                      }
                  }
              }
          }
      } catch (FileNotFoundException e) {}
      return null;//default behaviour
  }
}
