package org.jdna.sage.io;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sagex.SageAPI;
import sagex.api.FavoriteAPI;
import sagex.api.Utility;

public class Favorite extends ImportableObject {
    private final int id;
    private String title=null;
    private String category=null;
    private String subCategory=null;
    private String person=null;
    private String personRole=null;
    private String keyword=null;
    private String channelName=null;
    private String channelNetwork=null;
    private String rating=null;
    private String parentalRating=null;
    private String year=null;
    private String timeslot=null;
    private boolean isFirstRun=false;
    private boolean isReRun=false;
    
    private String quality=null;
    private long startPadding=0;
    private long stopPadding=0;
    private int keepAtMost=0;
    private boolean isAutoDelete=false;
    
    

    // From DTD:
    //<!ELEMENT favorite (  title?,category?,subCategory?,person?,
    //                        keyword?,channelName?,channelNetwork?,rating?,
    //                        parentalRating?,year?,timeslot?,firstRun?,
    //                        reRun?,quality?,startPadding?,stopPadding?,
    //                        keepAtMost?,autoDelete? ) >
    //<!ATTLIST favorite favoriteId CDATA #IMPLIED >
    //<!ELEMENT keyword (#PCDATA)>
    //<!ELEMENT timeslot (#PCDATA)> <!-- Day " " timseslot-->
    //<!ELEMENT firstRun EMPTY> <!-- In a favorite - menads match first runs
    //                               In a Show - means thatthis show is a first-run  -->
    //<!ELEMENT reRun EMPTY>     <!-- In a favoriet - means match re-runs -->
    //<!ELEMENT quality (#PCDATA)>
    //<!ELEMENT startPadding (#PCDATA)>
    //<!ELEMENT stopPadding (#PCDATA)>
    //<!ELEMENT keepAtMost (#PCDATA)>
    //<!ELEMENT autoDelete EMPTY>

    static Element createXmlElement(Document doc,Object favorite)
    throws InvocationTargetException,IllegalArgumentException {
        if (! FavoriteAPI.IsFavoriteObject(favorite))
            throw new IllegalArgumentException("Object type: \""+favorite.getClass().getName()+"\" is not a favorite");
        int favoriteID=FavoriteAPI.GetFavoriteID(favorite);
        
        Element favoriteNode=doc.createElement("favorite");
        favoriteNode.setAttribute("favoriteId",String.valueOf(favoriteID));
        
        
        SageXmlWriter.AddStringApiResult("GetFavoriteTitle",favorite,doc, favoriteNode,"title");
        SageXmlWriter.AddStringApiResult("GetFavoriteCategory",favorite,doc, favoriteNode,"category");
        SageXmlWriter.AddStringApiResult("GetFavoriteSubCategory",favorite,doc, favoriteNode,"subCategory");

        String person=FavoriteAPI.GetFavoritePerson(favorite);
        if ( person != null && person.length()>0) {
            Element personNode=(Element)favoriteNode.appendChild(doc.createElement("person"));
            personNode.appendChild(doc.createTextNode(person));
            String role=FavoriteAPI.GetFavoritePersonRole(favorite);
            if ( role != null && role.length()>0)
                personNode.setAttribute("role",SageXML.GetUntranslatedRole(role));
            else 
                personNode.setAttribute("role","");
        }
        SageXmlWriter.AddStringApiResult("GetFavoriteKeyword",favorite,doc, favoriteNode,"keyword");
        SageXmlWriter.AddStringApiResult("GetFavoriteChannel",favorite,doc, favoriteNode,"channelName");
        SageXmlWriter.AddStringApiResult("GetFavoriteNetwork",favorite,doc, favoriteNode,"channelNetwork");
        SageXmlWriter.AddStringApiResult("GetFavoriteRated",favorite,doc, favoriteNode,"rating");
        SageXmlWriter.AddStringApiResult("GetFavoriteParentalRating",favorite,doc, favoriteNode,"parentalRating");
        SageXmlWriter.AddStringApiResult("GetFavoriteYear",favorite,doc, favoriteNode,"year");
        SageXmlWriter.AddStringApiResult("GetFavoriteTimeslot",favorite,doc, favoriteNode,"timeslot");

        if ( FavoriteAPI.IsFirstRuns(favorite))
            favoriteNode.appendChild(doc.createElement("firstRun"));
        if ( FavoriteAPI.IsReRuns(favorite))
            favoriteNode.appendChild(doc.createElement("reRun"));
        
        SageXmlWriter.AddStringApiResult("GetFavoriteQuality",favorite,doc, favoriteNode,"quality");  
       
        Long padding=FavoriteAPI.GetStartPadding(favorite);
        if ( padding != null && padding.longValue()!=0 )
            ((Element)favoriteNode.appendChild(doc.createElement("startPadding")))
                    .appendChild(doc.createTextNode(Long.toString(padding.longValue()/1000)));
        padding=FavoriteAPI.GetStopPadding(favorite);
        if ( padding != null && padding.longValue()!=0 )
            ((Element)favoriteNode.appendChild(doc.createElement("stopPadding")))
            .appendChild(doc.createTextNode(Long.toString(padding.longValue()/1000)));
        
        Integer keepmost=FavoriteAPI.GetKeepAtMost(favorite);
        if ( keepmost!= null && keepmost.intValue()>0)
            ((Element)favoriteNode.appendChild(doc.createElement("keepAtMost")))
                .appendChild(doc.createTextNode(keepmost.toString()));
        
        if ( FavoriteAPI.IsAutoDelete(favorite))
            favoriteNode.appendChild(doc.createElement("autoDelete"));
        
        return favoriteNode;
    }
    
    /**
     * Constructor from a DOM element.
     */
    Favorite(Element favorite) throws XmlParseException {
        if ( favorite.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE ){
            throw new XmlParseException("favorite","Not an element node");
        }
        if ( ! favorite.getNodeName().equals("favorite")){
            throw new XmlParseException("favorite","not an favorite element");
        }
        String idStr=favorite.getAttribute("favoriteId");
        if ( idStr == null|| idStr.trim().length()==0)
            throw new XmlParseException("favorite","no favoriteId attribute present");
        try {
            id=Integer.parseInt(idStr);
        } catch (NumberFormatException e){
            throw new XmlParseException("favorite","favoriteId \""+idStr+"\" is not a number",e);
        }
        
        title=SageXmlReader.getChildTextElementValue(favorite,"title");
        category=SageXmlReader.getChildTextElementValue(favorite,"category");
        subCategory=SageXmlReader.getChildTextElementValue(favorite,"subCategory");

        Element[] personNodes=SageXmlReader.getChildElementsByTagName(favorite, "person");
        if ( personNodes!=null && personNodes.length>0){
            person=SageXmlReader.getChildTextElementValue(favorite,"person");        
            personRole=((Element)personNodes[0]).getAttribute("role");
            if ( personRole != null && personRole.trim().length()>0)
            {
                personRole=personRole.trim();
            } else {
                personRole=null;
            }
                
        }
        
        keyword=SageXmlReader.getChildTextElementValue(favorite,"keyword");
        channelName=SageXmlReader.getChildTextElementValue(favorite,"channelName");
        channelNetwork=SageXmlReader.getChildTextElementValue(favorite,"channelNetwork");           
        rating=SageXmlReader.getChildTextElementValue(favorite,"rating");

        parentalRating=SageXmlReader.getChildTextElementValue(favorite,"parentalRating");
        year=SageXmlReader.getChildTextElementValue(favorite,"year");
        timeslot=SageXmlReader.getChildTextElementValue(favorite,"timeslot");
        isFirstRun=SageXmlReader.getChildElementsByTagName(favorite, "firstRun")!=null;
        isReRun=SageXmlReader.getChildElementsByTagName(favorite, "reRun")!=null;
        quality=SageXmlReader.getChildTextElementValue(favorite,"quality");
        
        String tmp=SageXmlReader.getChildTextElementValue(favorite,"startPadding");
        if ( tmp!= null)
            try { startPadding=Integer.parseInt(tmp); }
            catch (NumberFormatException e){
                throw new XmlParseException("favorite Id "+id,"startPadding: \""+tmp+"\"is not a number",e);
            }
            
        tmp=SageXmlReader.getChildTextElementValue(favorite,"stopPadding");
        if ( tmp!= null)
            try { stopPadding=Integer.parseInt(tmp); }
            catch (NumberFormatException e){
                throw new  XmlParseException("favorite Id "+id,"stopPadding: \""+tmp+"\"is not a number",e);
            }

        tmp=SageXmlReader.getChildTextElementValue(favorite,"keepAtMost");
        if ( tmp!= null)
            try { keepAtMost=Integer.parseInt(tmp); }
            catch (NumberFormatException e){
                throw new  XmlParseException("favorite Id "+id,"keepAtMost: \""+tmp+"\"is not a number",e);
            }

        isAutoDelete=SageXmlReader.getChildElementsByTagName(favorite,"autoDelete")!=null;
    }

    /**
     * 
     * Attempt to create a Sage favorite object from the XML data node
     * 
     * @return Object[2] containing:
     *      retval[0]=Sage Favorite Object
     *      retval[1]='\n' separated list of warnings
     *
     * @throws InvocationTargetException when an API call fails
     *         Exception when Sage fails to create fave.
     */
    public Object[] createSageObject()
    throws Exception,InvocationTargetException {
        StringBuffer warnings=new StringBuffer();
        
     // roles and ratings are localized in the core.
        String sageRole=personRole;
        if ( sageRole!=null){
            // roles in Sage are localized...
            try {
                sageRole=(String) SageAPI.call("LocalizeString", new Object[]{personRole});
            }catch (Exception e){}
            if ( sageRole==null || sageRole.equals(personRole) ){
                // translation failed -- remove '_'
                sageRole=personRole.replace('_', ' ');
            }
        }
                

        String sageRating=rating;
        if ( sageRating!=null) {
            sageRating=(String) SageAPI.call("LocalizeString", new Object[]{rating});
            if ( sageRating==null || sageRating==rating)
                sageRating=rating.replace('_', ' ');
        }
            
        try { 
            Object favorite=FavoriteAPI.AddFavorite(
                    title, 
                    new Boolean(isFirstRun), 
                    new Boolean(isReRun), 
                    category, 
                    subCategory, 
                    person, 
                    sageRole,
                    sageRating,
                    year,
                    parentalRating,
                    channelNetwork,
                    channelName,
                    timeslot,
                    keyword);
            if ( favorite == null ){
                throw new Exception("Favorite failed to be created");
            }
            FavoriteAPI.SetDontAutodelete(favorite,new Boolean(!isAutoDelete));
            if ( isAutoDelete!= FavoriteAPI.IsAutoDelete(favorite))
                warnings
                    .append("Failed to set favorite AutoDelete to ")
                    .append(isAutoDelete)
                    .append('\n');

            if ( quality!=null ) {
                FavoriteAPI.SetFavoriteQuality(favorite,quality);
                String value=FavoriteAPI.GetFavoriteQuality(favorite);
                if ( value ==null || ! quality.equals(value))
                    warnings
                    .append("Failed to set favorite quality to ")
                    .append(quality)
                    .append('\n');
            }
                    
            FavoriteAPI.SetKeepAtMost(favorite,new Integer(keepAtMost));
            if ( keepAtMost != FavoriteAPI.GetKeepAtMost(favorite))
                warnings
                .append("Failed to set favorite keepAtMost to ")
                .append(keepAtMost)
                .append('\n');
            
            FavoriteAPI.SetStartPadding(favorite,new Long(startPadding));
            if ( startPadding != FavoriteAPI.GetStartPadding(favorite))
                warnings
                .append("Failed to set favorite startPadding to ")
                .append(startPadding)
                .append('\n');
            
            FavoriteAPI.SetStopPadding(favorite,new Long(stopPadding));
            if ( stopPadding != (FavoriteAPI.GetStopPadding(favorite)))
                warnings
                .append("Failed to set favorite stopPadding to ")
                .append(stopPadding)
                .append('\n');
            
            if ( warnings.length()>0)
                return  new Object[] {favorite,warnings.toString().trim()};
            else
                return  new Object[] {favorite,null};
            
        } catch (InvocationTargetException e) {
            System.out.println("error creating favorite "+e+" -- "+e.getCause());
            e.printStackTrace(System.out);
            throw e;
        }
    }
    /**
     * Finds a Sage favorite object matching this one in the XML
     * 
     * @return the matching Sage Favorite object, or null
     */
    public Object findSageObject()
    throws InvocationTargetException {
        Object faves=FavoriteAPI.GetFavorites();
        faves=filterbyStringMethod(faves,"GetFavoriteTitle",title);
        faves=filterbyStringMethod(faves,"GetFavoriteCategory",category);
        faves=filterbyStringMethod(faves,"GetFavoriteSubCategory",subCategory);
        if ( person!=null && personRole != null){
            faves=filterbyStringMethod(faves,"GetFavoritePerson",person);
            faves=filterbyStringMethod(faves,"GetFavoritePersonRole",personRole);
        }
        faves=filterbyStringMethod(faves,"GetFavoriteKeyword",keyword);
        faves=filterbyStringMethod(faves,"GetFavoriteChannel",channelName);
        faves=filterbyStringMethod(faves,"GetFavoriteNetwork",channelNetwork);
        faves=filterbyStringMethod(faves,"GetFavoriteRated",rating);
        faves=filterbyStringMethod(faves,"GetFavoriteParentalRating",parentalRating);
        faves=filterbyStringMethod(faves,"GetFavoriteYear",year);
        faves=filterbyStringMethod(faves,"GetFavoriteTimeslot",timeslot);
        
        faves=SageAPI.call("FilterByBoolMethod",new Object[]{faves,"IsFirstRuns",new Boolean(isFirstRun)});
        faves=SageAPI.call("FilterByBoolMethod",new Object[]{faves,"IsReRuns",new Boolean(isReRun)});

        if ( Utility.Size(faves)>0){
            return Utility.GetElement(faves, 0);
        }

        return null;
    }
    
    /**
     * utility function to check and filter list by value.
     * null values are not filtered.
     * @param list
     * @param method
     * @param value
     * @return
     * @throws InvocationTargetException
     */
    private Object filterbyStringMethod(Object list, String method, String value)
    throws InvocationTargetException 
    {
        if ( value==null)
            return list;
        return SageAPI.call("FilterByMethod",new Object[]{list,method,value,Boolean.TRUE});
    }

    public String getCategory() {
        return category;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelNetwork() {
        return channelNetwork;
    }

    public int getId() {
        return id;
    }

    public boolean isAutoDelete() {
        return isAutoDelete;
    }

    public boolean isFirstRun() {
        return isFirstRun;
    }

    public boolean isReRun() {
        return isReRun;
    }

    public int getKeepAtMost() {
        return keepAtMost;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getParentalRating() {
        return parentalRating;
    }

    public String getPerson() {
        return person;
    }

    public String getPersonRole() {
        return personRole;
    }

    public String getQuality() {
        return quality;
    }

    public String getRating() {
        return rating;
    }

    public long getStartPadding() {
        return startPadding;
    }

    public long getStopPadding() {
        return stopPadding;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }
}
