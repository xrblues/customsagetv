package org.jdna.sage.io;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.api.Utility;

public class Airing extends ImportableObject {
    private final Integer id;
    private Show show;
    private long startTime=0;
    private long duration=0;
    private Integer channelId=null;
    private Integer favoriteId=null;
    private String parentalRating=null;
    private boolean isWatched=false;
    private boolean isDontLike=false;
    private boolean isManualRecord=false;
    private String extraDetails=null;
    private boolean isHDTV=false;
    private int partnum=0;
    private int totalparts=0;
    private boolean isStereo=false;
    private boolean isSubtitled=false;
    private boolean isClosedCaptioned=false;
    private boolean hasSap=false;
    private String premierFinale=null;
    private final MediaFile mediaFile;
    
    // populated by Find or CreateSageObject
    private Object sageAiring=null;

    
    static Element createXmlElement(Document doc,Object airing, Object recSchedule) 
    throws InvocationTargetException,IllegalArgumentException {
        if ( airing == null ) 
            return null;
        if (! AiringAPI.IsAiringObject(airing))
            throw new IllegalArgumentException("Object type: \""+airing.getClass().getName()+"\" is not an Airing");

        Element airingNode=doc.createElement("airing");
        int airingid=AiringAPI.GetAiringID(airing);
        
        airingNode.setAttribute("sageDbId",String.valueOf(airingid));
        
        long startTimeMillis=AiringAPI.GetAiringStartTime(airing);
        Date startDate=new Date(startTimeMillis);
        airingNode.setAttribute("startTime",iso9601Date.getIsoDate(startDate));
        
        long durationMillis=AiringAPI.GetAiringDuration(airing);
        airingNode.setAttribute("duration",Long.toString((durationMillis+500)/1000));
    
        
        SageXmlWriter.AddStringApiResult("GetParentalRating",airing, doc, airingNode,"parentalRating");
        
        if ( AiringAPI.IsWatched(airing)) 
            airingNode.appendChild(doc.createElement("watched"));
        if ( AiringAPI.IsDontLike(airing)) 
            airingNode.appendChild(doc.createElement("dontLike"));
        if ( AiringAPI.IsManualRecord(airing)) 
            airingNode.appendChild(doc.createElement("manualRecord"));

        if ( recSchedule!=null){
            Vector<?> isSched=(Vector<?>)SageAPI.call("DataUnion", new Object[]{recSchedule, airing});
            if ( isSched!=null && isSched.size()>0){
                Element schedNode=(Element)airingNode.appendChild(doc.createElement("recordSchedule"));
                long schedStartTimeMillis=AiringAPI.GetAiringStartTime(airing);
                Date schedStartDate=new Date(schedStartTimeMillis);
                schedNode.setAttribute("startTime",iso9601Date.getIsoDate(schedStartDate));
                long schedDurationMillis=AiringAPI.GetAiringDuration(airing);
                schedNode.setAttribute("duration",Long.toString((schedDurationMillis+500)/1000));
            }
        }
         
        if (AiringAPI.IsAiringHDTV(airing))
            airingNode.appendChild(doc.createElement("isHDTV"));
        
        String extrainf=AiringAPI.GetExtraAiringDetails(airing);
        if (extrainf==null )
            extrainf="";
        String[] extraInfArr=extrainf.split(", *");
        java.util.List<String> extraInfList=java.util.Arrays.asList(extraInfArr);
        String partofparts=(String) SageAPI.call("LocalizeString", new Object[]{"Part_Of_Parts"});
        partofparts.replaceAll("\\{0\\}", "([0-9]+)");
        partofparts.replaceAll("\\{1\\}", "([0-9]+)");
        try {
            java.util.regex.Pattern p=java.util.regex.Pattern.compile(partofparts);
            for (int extraNum=0;extraNum<extraInfArr.length; extraNum++){
                java.util.regex.Matcher m=p.matcher(extraInfArr[extraNum]);
                if ( m.find()){
                    ((Element)airingNode.appendChild(doc.createElement("partNofM")))
                        .appendChild(doc.createTextNode(m.group(1)+"/"+m.group(2)));
                }
            }
        } catch ( java.util.regex.PatternSyntaxException e ){}
        
        if (extraInfList.contains((String) SageAPI.call("LocalizeString",new Object[]{"Stereo"})))
            airingNode.appendChild(doc.createElement("stereo"));
        if (extraInfList.contains((String) SageAPI.call("LocalizeString",new Object[]{"Subtitled"})))
            airingNode.appendChild(doc.createElement("subtitled"));
        if (extraInfList.contains((String) SageAPI.call("LocalizeString",new Object[]{"Closed_Captioned"})))
            airingNode.appendChild(doc.createElement("closedCaptioned"));
        if (extraInfList.contains((String) SageAPI.call("LocalizeString",new Object[]{"SAP"})))
            airingNode.appendChild(doc.createElement("sap"));
        
        // premFinale
        String[][] AllPremiereFinale=SageXML.getAllPremiereFinale();
        for ( int premFinIndex=0;premFinIndex<AllPremiereFinale.length;premFinIndex++){
            if ( extraInfList.contains(AllPremiereFinale[premFinIndex][1])){
                ((Element)airingNode.appendChild(doc.createElement("premierFinale")))
                    .appendChild(doc.createTextNode(AllPremiereFinale[premFinIndex][0]));
                break;
            }   
        }
        return airingNode;
    }
    

    Airing(Element airing, Show show) throws XmlParseException{
        if ( show==null ){
            throw new XmlParseException("airing","needs a show");
        }
        this.show=show;
        
        if ( airing.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE ){
            throw new XmlParseException("airing","Not an element node");
        }
        if ( ! airing.getNodeName().equals("airing")){
            throw new XmlParseException("airing","not an airing element");
        }
        String idStr=airing.getAttribute("sageDbId");

        if ( idStr == null || idStr.trim().length()==0) {
            id=null;
            idStr="airing for show:"+show.getExtId();
        }else{
            try { 
                id=new Integer(idStr);
                idStr="airing id:"+idStr+" for show:"+show.getExtId();;
            } catch (NumberFormatException e){
                throw new XmlParseException("airing","sageDbId \""+idStr+"\" is not a number",e);
            }
        }
        
       
            
       
        String tmp=airing.getAttribute("startTime");
        
        if ( tmp==null || tmp.trim().length()==0)
           startTime=-1;
        try {
            Date startDate=iso9601Date.parse(tmp);
            startTime=startDate.getTime();
        }catch (InvalidDateException e){
            throw new XmlParseException(idStr,"startTime \""+tmp+"\" is not valid Iso9601 date",e);
        }
      
        tmp=airing.getAttribute("duration");
        if ( tmp==null || tmp.trim().length()==0)
            duration=-1;
        try {
            duration=Long.parseLong(tmp)*1000;
        }catch (NumberFormatException e){
            throw new XmlParseException(idStr,"duration \""+tmp+"\" is not a number",e);
        }
        
        
        tmp=airing.getAttribute("channelId");
        if ( tmp!=null && tmp.trim().length()>0){
            try {
                channelId=new Integer(tmp);
            }catch (NumberFormatException e){
                throw new XmlParseException(idStr,"channelId \""+tmp+"\" is not a number",e);
            }
        }
        tmp=airing.getAttribute("favoriteId");
        if ( tmp!=null && tmp.trim().length()>0){
            try {
                favoriteId=new Integer(tmp);
            }catch (NumberFormatException e){
                throw new XmlParseException(idStr,"favoriteId \""+tmp+"\" is not a number",e);
            }
        }   

        parentalRating=SageXmlReader.getChildTextElementValue(airing, "parentalRating");

        isWatched=(SageXmlReader.getChildElementsByTagName(airing, "watched")!=null);
        isDontLike=(SageXmlReader.getChildElementsByTagName(airing, "dontLike")!=null);
        isManualRecord=(SageXmlReader.getChildElementsByTagName(airing, "manualRecord")!=null);


        isHDTV=(SageXmlReader.getChildElementsByTagName(airing, "isHDTV")!=null);
        isStereo=(SageXmlReader.getChildElementsByTagName(airing, "stereo")!=null);;
        isSubtitled=(SageXmlReader.getChildElementsByTagName(airing, "subtitled")!=null);;
        isClosedCaptioned=(SageXmlReader.getChildElementsByTagName(airing, "closedCaptioned")!=null);;
        hasSap=(SageXmlReader.getChildElementsByTagName(airing, "sap")!=null);;
        
        extraDetails=SageXmlReader.getChildTextElementValue(airing, "extraDetails");
        
        premierFinale=SageXmlReader.getChildTextElementValue(airing, "premierFinale");
        String xmlPartNofM=SageXmlReader.getChildTextElementValue(airing, "partNofM");
        if (xmlPartNofM!=null){
            String parts[]=xmlPartNofM.split("/");
            if ( parts.length!=2){
                throw new XmlParseException(idStr,"partNofM \""+xmlPartNofM+"\" not correctly formatted (1/2)");
            }
            try {
                totalparts=Integer.parseInt(parts[1]);
                partnum=Integer.parseInt(parts[0]);
            }catch (NumberFormatException e){
                throw new XmlParseException(idStr,"partNofM \""+xmlPartNofM+"\" does not contain numbers",e);
            }
            if ( partnum>totalparts){
                throw new XmlParseException(idStr,"partNofM \""+xmlPartNofM+"\" partnum too large");
            }
        }
        
        Element[] mediaFiles=SageXmlReader.getChildElementsByTagName(airing, "mediafile");
        if ( mediaFiles!=null && mediaFiles.length>0)
            mediaFile=new MediaFile(mediaFiles[0],this);
        else
            mediaFile=null;
    }

    public Integer getId() {
        return id;
    }

    public Show getShow() {
        return show;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }


    public Integer getChannelId() {
        return channelId;
    }


    public long getDuration() {
        return duration;
    }

    /**
     * @deprecated
     * @return
     */
    public String getExtraDetails() {
        return extraDetails;
    }


    public Integer getFavoriteId() {
        return favoriteId;
    }


    public boolean hasSap() {
        return hasSap;
    }


    public boolean isClosedCaptioned() {
        return isClosedCaptioned;
    }


    public boolean isDontLike() {
        return isDontLike;
    }


    public boolean isHDTV() {
        return isHDTV;
    }


    public boolean isManualRecord() {
        return isManualRecord;
    }


    public boolean isStereo() {
        return isStereo;
    }


    public boolean isSubtitled() {
        return isSubtitled;
    }


    public boolean isWatched() {
        return isWatched;
    }


    public String getParentalRating() {
        return parentalRating;
    }


    public int getPartnum() {
        return partnum;
    }


    public String getPremierFinale() {
        return premierFinale;
    }


    public long getStartTime() {
        return startTime;
    }


    public int getTotalparts() {
        return totalparts;
    }

    /**
     * 
     * Creates the Sage object for this imported data
     * 
     * @param forceUnviewableChannel -- links airing to an unviewable channel
     * @param forceTVAiring -- assigns and removes a mediafile for this airing so that it is searchable
     * @return Object[2]: {Object, String warnings}
     * @throws Exception
     * @throws InvocationTargetException
     */
    public Object[] createSageObject(boolean forceUnviewableChannel, boolean forceTVAiring) throws Exception, InvocationTargetException {
        String warnings=null;
        if ( findSageObject(forceUnviewableChannel) != null )
            warnings="Overwriting existing Airing";
        if (channelId!=null && channelId.intValue()!=0) {
            Object channel=ChannelAPI.GetChannelForStationID(channelId);
            if ( channel==null){
                warnings=(warnings==null?"":warnings+", ")+"ignoring unknown channel ID:"+channelId;
                channelId=null;
            }
            if ( ! ChannelAPI.IsChannelViewable(channel) 
                  && ! forceUnviewableChannel){
                warnings=(warnings==null?"":warnings+", ")+"ignoring unviewable channel ID:"+channelId;
                channelId=null;
            }
        }
        if ( startTime <0)
            
            throw new XmlParseException("airing "+(id==null?"of":"id "+id+" for")+" show:"+show.getExtId(),"startTime not specified");
        if ( duration <0)
            throw new XmlParseException("airing "+(id==null?"of":"id "+id+" for")+" show:"+show.getExtId(),"duration not specified");

        // PremierFinale is localized
        String sagePremierFinale=premierFinale;
        if ( sagePremierFinale!=null ) {
            sagePremierFinale=(String) SageAPI.call("LocalizeString",new Object[]{premierFinale});
            if (sagePremierFinale==null || premierFinale.equals(sagePremierFinale) )
                sagePremierFinale=premierFinale.replace('_', ' ');
        }
        
        if ( SageXML.getSageMajorVersion() > 4.99) {
            sageAiring=SageAPI.call("AddAiringDetailed",new Object[]{
                    show.getExtId(),
                    channelId,
                    new Long(startTime),
                    new Long(duration),
                    new Integer(partnum),
                    new Integer(totalparts),
                    parentalRating,
                    new Boolean(isHDTV),
                    new Boolean(isStereo),
                    new Boolean(isClosedCaptioned),
                    new Boolean(hasSap),
                    new Boolean(isSubtitled),
                    sagePremierFinale});
        } else {
            sageAiring=SageAPI.call("AddAiring",new Object[]{
                    show.getExtId(),
                    channelId,
                    new Long(startTime),
                    new Long(duration)});
        }
        
        if ( forceTVAiring){
            // workaround for bug in 6.0/6.1 -- airing needs to be attached to a media file
            File tmpFile=File.createTempFile("temp_import", ".avi");
            Object sageMediaFile=SageAPI.call("AddMediaFile",new Object[]{tmpFile,""});
            if ( sageMediaFile!=null){
                if ( MediaFileAPI.SetMediaFileAiring(sageMediaFile,sageAiring)){
                    SageAPI.call("DeleteFile",new Object[] {sageMediaFile});
                } else {
                    warnings=(warnings==null?"":warnings+", ")+",could not make airing a TV airing (failed SetMediaFileAiring)";
                }
            } else {
                // failed to create MF
                warnings=(warnings==null?"":warnings+", ")+",could not make airing a TV airing (failed AddMediaFile)";
            }
            if ( tmpFile.exists()){
                if ( ! tmpFile.delete() )
                    tmpFile.deleteOnExit();
            }
        }
            
        
        
        if ( isWatched)
            AiringAPI.SetWatched(sageAiring);
        if ( isDontLike)
            AiringAPI.SetDontLike(sageAiring);
        if ( isManualRecord )
            AiringAPI.Record(sageAiring);
        
        return new Object[]{sageAiring,warnings};
    }


     
    public boolean equals(Object airing) {
        if (airing instanceof Airing) {
            Airing compare = (Airing) airing;
            
            // null channel IDs are always different
            if ( this.channelId==null || compare.channelId==null )
                return false;
            
            // compare shows, and compare start times to within 10s
            return(
                    (this.show.equals(compare.show)
                    || this.show.getExtId().equals(compare.show.getExtId()))
                    && this.channelId.equals(compare.channelId)
                    && java.lang.Math.abs(this.startTime-compare.startTime)<10000
            );
        }
        // check to see if it is a Sage airing
        try {
            if ( ! AiringAPI.IsAiringObject(airing)
                 &&  ! MediaFileAPI.IsMediaFileObject(airing)){
                return false;
            }
            // compare showID
            String airingEpgId=ShowAPI.GetShowExternalID(airing);
            if ( airingEpgId!=null && show.getExtId().equals(airingEpgId)){
                // compare starttime within 10s
                Long airingStartTime=AiringAPI.GetAiringStartTime(airing);
                if ( airingStartTime!=null &&
                        java.lang.Math.abs(airingStartTime.longValue()-startTime)<10000 ){
                    // comparefor channelID
                    Object airingChannel=AiringAPI.GetChannel(airing);
                    if ( airingChannel ==null ){
                        // null channels always compare false
                        return false;
                    }
                    Integer airingChannelId=ChannelAPI.GetStationID(airingChannel);
                    if ( airingChannelId.intValue()==0)
                        // null channels always compare false
                        return false;
                    if ( (airingChannelId ==null && channelId==null)
                            || ( channelId!=null 
                                 && airingChannelId!=null 
                                 && airingChannelId.intValue()== channelId.intValue())){
                        // same showID, starttime, channel
                        return true;
                    }
                }
            }
        }catch (Exception e){
            System.out.println("net.sf.sageplugins.sagexmlinfo.Airing.equals: "+e);
            e.printStackTrace();
        }
        return false;
    }


    /**
     * finds the matching Sage object,
     * 
     * @return Object or null if not found.
     * @throws InvocationTargetException
     */
    public Object findSageObject(boolean findOnUnviewableChannel) throws InvocationTargetException {
        // first see if we already know it...
       if ( sageAiring !=null )
           return sageAiring;
       
       if ( channelId == null )
           // null channel IDs are always different, so can never match existing airings
           return null;
       
       // first try by ID. 
        if ( id!=null){
            sageAiring=AiringAPI.GetAiringForID(id);
            if (sageAiring!=null ){
                if ( ! equals(sageAiring))
                    sageAiring=null;
            }
        } else {
            // not found by ID, has channel Id 
            // find by Airings of ShowID
            Object sageShow=show.findSageObject();
            if ( sageShow!=null){
                Object airings=ShowAPI.GetAiringsForShow(sageShow, 0);
                for ( int i=0; i<Utility.Size(airings);i++){
                    sageAiring=Utility.GetElement(airings, i);
                    if ( this.equals(sageAiring))
                        break;
                }
            }
        }
        if (  sageAiring!=null && !findOnUnviewableChannel ){
            // check channel is viewable
            Object channel=AiringAPI.GetChannel(sageAiring);
            if ( ! ChannelAPI.IsChannelViewable(channel)) {
                sageAiring=null;
            }
        }
        return sageAiring;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }
}
