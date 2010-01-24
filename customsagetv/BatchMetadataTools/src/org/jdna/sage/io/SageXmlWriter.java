package org.jdna.sage.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.Configuration;
import sagex.api.FavoriteAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;


/**
 * Class to support writing XML data for Sage media files
 * See <a href="http://tools.assembla.com/sageplugins/wiki/SageXmlInfo">Sage XML Show Info in the Wiki</a>
 * 
 * <h2>Usage:</h2> 
 * <ul>
 *    <li>Construct;
 *    <li>Add Airing, Show, channel, favorite or MediaFile objects 
 *    with the add() method
 *    <li>Lists/arrays/collections of the above can also be added using the add() method
 *    <li>then call one of the write() methods to write the result to either
 *    an output file, or an output stream
 *  </ul>
 *  Duplicate objects will be ignored
 *  <p>
 *  Order of addition is normally reflected in order of output -- with the exception of multiple airings for the same show: 
 *  the second and subsequent airings will be added as children of the show used by the first airing.  
 *  <h2>STV usage</h2>
 *  <pre>
 *  xmlwriter=new_net_sf_sageplugins_sagexmlinfo_SageXmlWriter()
 *      net_sf_sageplugins_sagexmlinfo_SageXmlWriter_add(xmlwriter,object)
 *      net_sf_sageplugins_sagexmlinfo_SageXmlWriter_add(xmlwriter,object2)
 *      net_sf_sageplugins_sagexmlinfo_SageXmlWriter_add(xmlwriter,objectlist)
 *      net_sf_sageplugins_sagexmlinfo_SageXmlWriter_write(xmlwriter,new_java_io_File("c:/path/to/file");
 * </pre>
 *  
 * @author nielm
 * 
 */
public class SageXmlWriter {

    private Document doc;
    
    private Element channelListNode;
    private Element showListNode;
    private Element favoriteListNode;
    
    private TreeMap<Integer, Element> channels=new TreeMap<Integer, Element>(); // map of Integer(StationID) -> Channel Element 
    private TreeMap<Integer, Element> favorites=new TreeMap<Integer, Element>(); // map of Integer(FavoriteID) -> Favorite Element
    private TreeMap<String, Element> shows=new TreeMap<String, Element>();    // map of String(ShowExtID) -> Show Element
    private TreeMap<Integer, Element> airings=new TreeMap<Integer, Element>();  // map of Integer(AiringID) -> Airing Element
    private TreeMap<Integer, Element> mediaFiles=new TreeMap<Integer, Element>(); // map of Integer(MediaFileID) -> MediaFile element

   
    /**
     *  Constructor: creates the class ready for adding 
     *  show information.
     */
    public SageXmlWriter()
    throws Exception
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DOMImplementation impl=builder.getDOMImplementation();
        DocumentType sageshowinfoType = impl.createDocumentType("sageShowInfo", 
                SageXML.SAGEXML_DTD_PUBLIC, 
                SageXML.SAGEXML_DTD_URI);
        
        doc=impl.createDocument(null,"sageShowInfo",sageshowinfoType);
        Element root=doc.getDocumentElement();
        root.setAttribute("version",SageXML.SAGEXML_CURRENT_VERSION);

        channelListNode=(Element)root.appendChild(doc.createElement("channelList"));
        favoriteListNode=(Element)root.appendChild(doc.createElement("favoriteList"));
        showListNode=(Element)root.appendChild(doc.createElement("showList"));
    }

    /**
     * Write the XML to the file specified using the default utf-8 charset
     * 
     * @param outf File object for output
     * @return true if successful; throws exception otherwise
     * @throws Exception
     */
    public boolean write(File outf) throws Exception {
        return write(outf,Configuration.GetProperty("nielm/xmlinfo/charset","utf-8"));
    }
    
    /**
     * Write the XML to the file specified using the specified charset
     * 
     * @param outf File object for output
     * @param charset Charset to use
     * @return true if successful; throws exception otherwise
     * @throws Exception
     */
    public boolean write(File outf,String charset) throws Exception {
        OutputStream outs=new BufferedOutputStream(new FileOutputStream(outf));
        try { 
            write(outs,charset);
        } finally {
            outs.close();
        }
        return true;
    }
    /**
     * Write the XML to the output stream outs using the default utf-8 charset
     * 
     * @param outs OutputStream to write to
     * @throws Exception
     */
    public void write(OutputStream outs) throws Exception {
        write(outs,Configuration.GetProperty("nielm/xmlinfo/charset","utf-8"));
    }
    /**
     * Write the XML to the output stream outs using the specified charset
     * 
     * @param outs OutputStream to write to
     * @param charset charset to use
     * @throws Exception
     */
    public void write(OutputStream outs,String charset) throws Exception {
        Result result=new StreamResult(new OutputStreamWriter(outs,charset));
        Source source = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        
        // handle JRE 1.4/1.5 indenting 
        if(!System.getProperty("java.version").startsWith("1.4"))
            tf.setAttribute("indent-number", new Integer(4));
        
        Transformer xformer =tf.newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        xformer.setOutputProperty(OutputKeys.METHOD, "xml");
        xformer.setOutputProperty(OutputKeys.ENCODING, charset);
        // 1.2 -- always add doctype with public ID and URI hint
        xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, SageXML.SAGEXML_DTD_URI);
        xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, SageXML.SAGEXML_DTD_PUBLIC);
        xformer.transform(source, result);
    }
    
    /**
     * Add an object or array/collection of objects to the XML output
     * <p>
     * this function automatically determines the object type, and then calls
     * one of the other addType() functions
     * 
     * @param item - object to add -- may be a Sage Airing / MediaFile / Show / Favorite / Channel 
     * or any array or collection of these objects
     * @throws InvocationTargetException - for Sage API errors
     * @throws IllegalArgumentException - for invalid object type
     */
    public void add(Object item) throws InvocationTargetException,IllegalArgumentException  {
        if ( item == null ) 
            return;
        if (item instanceof Collection)
            addCollection((Collection<?>)item);
        else if (item.getClass().isArray())
            addArray((Object[])item);
        else if (ChannelAPI.IsChannelObject(item))
            addChannel(item);
        else if (FavoriteAPI.IsFavoriteObject(item))
            addFavorite(item);
        else if (MediaFileAPI.IsMediaFileObject(item))
            addMediaFile(item);
        else if (AiringAPI.IsAiringObject(item))
            addAiring(item);
        else if (ShowAPI.IsShowObject(item))
            addShow(item);
        else throw new IllegalArgumentException("Object type: \""+item.getClass().getName()+"\" not supported - can only add channel/MF/airing/show objects, or a collection/array of these");
    }
    /**
     * Add a collection of objects to the XML output
     * 
     * @param collection - collection of Sage Airing / MediaFile / Show / Favorite / Channel objects to add
     * @throws InvocationTargetException - for Sage API errors
     * @throws IllegalArgumentException - for invalid object type
     */
    public void addCollection(Collection<?> collection) throws InvocationTargetException, IllegalArgumentException {
        Iterator<?> it = collection.iterator();
        while ( it.hasNext()) {
            add(it.next());
        }
    }
    
    /**
     * Add an array of objects to the XML output.
     
     * @param array -- array of show/airing/mediafile/channel/favorite objects to add
     * @throws InvocationTargetException - for Sage API errors
     * @throws IllegalArgumentException - for invalid object type
     */
    public void addArray(Object[] array) throws InvocationTargetException,IllegalArgumentException {
        for ( int i=0; i<array.length;i++){
            add(array[i]);
        }
    }
    
    /**
     * addChannel -- add a channel to the XML output
     * 
     * @param Object channel object to add
     * @return Integer stationID Sage ID of the channel added 
     * @throws InvocationTargetException -- for sage API errors
     * @throws IllegalArgumentException -- if not a channel object
     */
    public Integer addChannel(Object channel) throws InvocationTargetException,IllegalArgumentException  {
        if ( channel == null ) 
            return null;
        if (! ChannelAPI.IsChannelObject(channel))
            throw new IllegalArgumentException("Object type: \""+channel.getClass().getName()+"\" is not a Channel");
        
        Integer channelID=ChannelAPI.GetStationID(channel);
        if ( channels.get(channelID) != null ) {
           //already entered, ignore this one
           return channelID;
        }
        
        Element channelNode=Channel.createXmlElement(doc,channel);
        channelListNode.appendChild(channelNode);
        channels.put(channelID,channelNode);
        return channelID;
    }
 
    /**
     * Add a favorite to the XML output
     * 
     * @param Object favorite object to add
     * @return Integer favoriteID -- sage ID for the added favorite
     * @throws InvocationTargetException -- for sage API errors
     * @throws IllegalArgumentException -- if not a favorite object
     */
    public Integer addFavorite(Object favorite) throws InvocationTargetException,IllegalArgumentException  {
        if ( favorite == null ) 
            return null;
        if (! FavoriteAPI.IsFavoriteObject(favorite))
            throw new IllegalArgumentException("Object type: \""+favorite.getClass().getName()+"\" is not a favorite");
        
        Integer favoriteID=FavoriteAPI.GetFavoriteID(favorite);
        if ( favorites.get(favoriteID) != null ) {
           //already entered, ignore this one
           return favoriteID;
        }       
        Element favoriteNode=Favorite.createXmlElement(doc,favorite);
        favoriteListNode.appendChild(favoriteNode);
        favorites.put(favoriteID,favoriteNode);
        return favoriteID;
    }
 
    
    /**
     * Add a media file to the XML output
     * <p>
     * Automatically adds an airing/show/channel/favorite referenced by this media file
     * 
     * @param Object mediafile -- file to add
     * @return Integer mediafileID -- Sage internal ID for this media file
     * @throws InvocationTargetException -- for sage API errors
     * @throws IllegalArgumentException -- if not a Media File object
     */
    public Integer  addMediaFile(Object mediafile) throws InvocationTargetException,IllegalArgumentException  {
        if ( mediafile == null ) 
            return null;
        if (! MediaFileAPI.IsMediaFileObject(mediafile))
            throw new IllegalArgumentException("Object type: \""+mediafile.getClass().getName()+"\" is not a MediaFile");
        
        // Find existing MF...
        Integer mfid=MediaFileAPI.GetMediaFileID(mediafile);
        if ( mediaFiles.get(mfid) != null ) {
           //already entered, ignore this one
           return mfid;
        }
        
        // Add Airing for this MF
        Object airing=MediaFileAPI.GetMediaFileAiring(mediafile);
        Integer airingId=addAiring(airing);
        
        // Find Airing for MF
        Element airingNode=airings.get(airingId);
        if ( airingNode != null) {
            Element mediafileNode=MediaFile.createXmlElement(doc, mediafile);
            airingNode.appendChild(mediafileNode);
            mediaFiles.put(mfid,mediafileNode);
        }
        return mfid;
    }
    
    /**
     * Add an airing to the XML output
     * <p>
     * Auto-adds show/channel and favorite referenced by this airing
     * 
     * @param Object airing -- airing object to add
     * @return Integer airingID internal sage ID for this airing.
     * @throws InvocationTargetException  -- for sage API errors
     * @throws IllegalArgumentException -- if not an Airing object
     */
    public Integer addAiring(Object airing) throws InvocationTargetException,IllegalArgumentException  {
        if ( airing == null ) 
            return null;
        if (! AiringAPI.IsAiringObject(airing))
            throw new IllegalArgumentException("Object type: \""+airing.getClass().getName()+"\" is not an Airing");
        
        //      Find existing Airing...
        Integer airingid=AiringAPI.GetAiringID(airing);
        if ( airings.get(airingid) != null ) {
           //already entered, ignore this one
           return airingid;
        }
        // Add Show for this airing
        Object show=AiringAPI.GetShow(airing);
        String showID=addShow(show);
        
        // Find show for this airing
        Element showNode=shows.get(showID);
        if ( showNode != null) {
            Object recSched=Global.GetScheduledRecordings();
            Element airingNode=Airing.createXmlElement(doc, airing,recSched);
            showNode.appendChild(airingNode);
            airings.put(airingid,airingNode);
            
            // add channel
            Object channel=AiringAPI.GetChannel(airing);
            if ( channel != null) {
                Integer channelID=addChannel(channel);
                airingNode.setAttribute("channelId",channelID.toString());
            }

            // add favorite
            if (AiringAPI.IsFavorite(airing)){
                Object favorite=FavoriteAPI.GetFavoriteForAiring(airing);
                if (favorite!=null){
                    Integer faveID=addFavorite(favorite);
                    airingNode.setAttribute("favoriteId",faveID.toString());
                }
            }
        }
        return airingid;
    }
    
    /**
     * Add a show to the XML output
     * 
     * @param Object show
     * @return String EPGID external ID for this show as supplied by the listings provider
     * @throws InvocationTargetException  -- for sage API errors
     * @throws IllegalArgumentException -- if not a Media File object
     */
    public String addShow(Object show) throws InvocationTargetException,IllegalArgumentException  {
        if ( show == null ) 
            return null;
        if (! ShowAPI.IsShowObject(show))
            throw new IllegalArgumentException("Object type: \""+show.getClass().getName()+"\" is not a Show");
        
        // Find existing Show...
        String showid=ShowAPI.GetShowExternalID(show);
        if ( shows.get(showid) != null ) {
           //already entered, ignore this one
           return showid;
        }
        
        Element showNode=Show.createXmlElement(doc, show);
        showListNode.appendChild(showNode);
        shows.put(showid,showNode);

        return showid;
    }

    static void AddStringApiResult(String api,Object obj, Document doc, Element parent, String childname) throws InvocationTargetException {
        String result=(String)SageAPI.call(api,new Object[] {obj});
        if ( result == null || result.length()==0)
           return;
        result=result.replaceAll("\r\n","\n");
        result=result.replaceAll("\n\n*","\n\n");
        ((Element)parent.appendChild(doc.createElement(childname)))
            .appendChild(doc.createTextNode(result));
    }
}

