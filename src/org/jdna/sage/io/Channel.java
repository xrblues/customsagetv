package org.jdna.sage.io;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sagex.api.ChannelAPI;

public class Channel extends ImportableObject {
    
    static Element createXmlElement(Document doc,Object channel) throws InvocationTargetException,IllegalArgumentException  {
        if ( channel == null ) 
            return null;
        if (! ChannelAPI.IsChannelObject(channel))
            throw new IllegalArgumentException("Object type: \""+channel.getClass().getName()+"\" is not a Channel");
        
        Integer channelID=ChannelAPI.GetStationID(channel);
        Element channelNode=doc.createElement("channel");
        channelNode.setAttribute("channelId",channelID.toString());
        
        SageXmlWriter.AddStringApiResult("GetChannelName",channel,doc, channelNode,"channelName");
        SageXmlWriter.AddStringApiResult("GetChannelDescription",channel,doc, channelNode,"channelDescription");
        SageXmlWriter.AddStringApiResult("GetChannelNetwork",channel,doc, channelNode,"channelNetwork");
        SageXmlWriter.AddStringApiResult("GetChannelNumber",channel,doc, channelNode,"channelNumber");
        
        return channelNode;
    }
    
    /// Parsing constructor
    Channel(Element channel)
    throws XmlParseException
    {
        if ( channel.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE ){
            throw new XmlParseException("channel","Not an element node");
        }
        if ( ! channel.getNodeName().equals("channel")){
            throw new XmlParseException("channel","not an channel element");
        }
        String idStr=channel.getAttribute("channelId");
        if ( idStr == null || idStr.trim().length()==0)
            throw new XmlParseException("channel","no channelId attribute present");
        try {
            id=Integer.parseInt(idStr);
        } catch (NumberFormatException e){
            throw new XmlParseException("channel","channelId \""+idStr+"\" is not a number",e);
        }
        /*
         * <!ELEMENT channel ( channelName?,
                    channelDescription?,
                    channelNetwork?,
                    channelNumber?)>
         */
        name=SageXmlReader.getChildTextElementValue(channel,"channelName");
        description=SageXmlReader.getChildTextElementValue(channel,"channelDescription");
        network=SageXmlReader.getChildTextElementValue(channel,"channelNetwork");
        number=SageXmlReader.getChildTextElementValue(channel,"channelNumber");
    }
    private String name;
    private String description;
    private String network;
    private String number;
    private final int id;
    public String getDescription() {
        return description;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getNetwork() {
        return network;
    }
    public String getNumber() {
        return number;
    }

    public Object[] createSageObject() throws Exception, InvocationTargetException {
        throw new Exception("Cannot create channels");
    }

    /**
     * finds the matching Sage object,
     * 
     * @return Object or null if not found.
     * @throws InvocationTargetException
     */
    public Object findSageObject() throws InvocationTargetException {
        return ChannelAPI.GetChannelForStationID(id);
    }
    
}
