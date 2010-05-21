package org.jdna.sage.io;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sagex.api.ShowAPI;
import sagex.api.Utility;

public class Show extends ImportableObject {
    final private String extId;
    private String title=null;
    private String episode=null;
    boolean isFirstRun=false;
    long duration=0;
    private String category=null;
    private String subCategory=null;
    private String description=null;
    private String rating=null;
    private List<String> advisoryList=null; //List of String;
    private List<String> peopleList=null;
    private List<String> peopleRolesList=null;
    private String year=null;
    private String language=null;
    long originalAirDate=0;
    private String misc[];
    private List<Airing> airingList=null; // {AiringID, Airing}
    
    // populated by create or find
    private Object sageShow=null;
    
    static Element createXmlElement(Document doc,Object show) 
    throws InvocationTargetException,IllegalArgumentException {

        if ( show == null ) 
            return null;
        if (! ShowAPI.IsShowObject(show))
            throw new IllegalArgumentException("Object type: \""+show.getClass().getName()+"\" is not a Show");
        
        String showid=ShowAPI.GetShowExternalID(show);
        
        Element showNode=doc.createElement("show");
        showNode.setAttribute("epgId",showid);

        SageXmlWriter.AddStringApiResult("GetShowTitle",show, doc, showNode, "title");
        SageXmlWriter.AddStringApiResult("GetShowEpisode",show, doc, showNode, "episode");
        
        if ( ShowAPI.IsShowFirstRun(show)) 
            showNode.appendChild(doc.createElement("firstRun"));
        
        Long durationMillis=ShowAPI.GetShowDuration(show);
        if ( durationMillis.longValue()>0){
            ((Element)showNode.appendChild(doc.createElement("duration")))
                .appendChild(doc.createTextNode(Long.toString((durationMillis.longValue()+500)/1000)));
        }

        SageXmlWriter.AddStringApiResult("GetShowCategory",show, doc, showNode, "category");
        SageXmlWriter.AddStringApiResult("GetShowSubCategory",show, doc, showNode, "subCategory");
        SageXmlWriter.AddStringApiResult("GetShowDescription",show, doc, showNode, "description");
        SageXmlWriter.AddStringApiResult("GetShowRated",show, doc, showNode, "rating");
        
        String advisoryList=ShowAPI.GetShowExpandedRatings(show);
        if ( advisoryList != null && advisoryList.length()>0) {
            String[] advisories=advisoryList.split("(,\\s|\\sand\\s)");
            Element advisoryListNode=(Element)showNode.appendChild(doc.createElement("advisoryList"));
            for ( int i=0;i<advisories.length;i++){
                ((Element)advisoryListNode.appendChild(doc.createElement("advisory")))
                    .appendChild(doc.createTextNode(advisories[i]));
            }
        }
        
        // people
        // Linked hash map preserves ordering
        Map<String, String[]> showRoles = new LinkedHashMap<String, String[]>();
        String[] roles = ShowAPI.GetRoleTypes();

        // find the roles in this show
        for (int i = 0; i < roles.length; i++)
        {
            String people = ShowAPI.GetPeopleInShowInRole(show, roles[i]);

            if ((people != null) && (people.length() > 0))
            {
                String[] peoplearr = people.split(",\\s*");
                if ((peoplearr != null) && (peoplearr.length > 0))
                {
                    showRoles.put(roles[i], peoplearr);
                }
            }
        }

        // create xml node for roles
        if (showRoles != null && showRoles.size() > 0)
        {
            Element peopleListNode = (Element) showNode.appendChild(doc.createElement("peopleList"));

            for (Map.Entry<String, String[]> showRole : showRoles.entrySet())
            {
                String[] peoplearr = showRole.getValue();
                for (int j = 0; j < peoplearr.length; j++)
                {
                    Element personNode = (Element) peopleListNode.appendChild(doc.createElement("person"));
                    personNode.appendChild(doc.createTextNode(peoplearr[j]));
                    personNode.setAttribute("role", SageXML.GetUntranslatedRole(showRole.getKey()/*roles[i]*/));
                }
            }
        }

        SageXmlWriter.AddStringApiResult("GetShowYear",show, doc, showNode, "year");
        SageXmlWriter.AddStringApiResult("GetShowLanguage",show, doc, showNode, "language");
              
        //OAD
        Long oadMillis=ShowAPI.GetOriginalAiringDate(show);
        if ( oadMillis.longValue()!=0) {
            Date oaDate=new Date(oadMillis.longValue());
            ((Element)showNode.appendChild(doc.createElement("originalAirDate")))
            .appendChild(doc.createTextNode(iso9601Date.getIsoDate(oaDate)));
        }
        SageXmlWriter.AddStringApiResult("GetShowMisc",show, doc, showNode, "misc");
        
        return showNode;
    }
    
    Show(Element show, Set<?> existingIds) throws XmlParseException
    {
        if ( show.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE ){
            throw new XmlParseException("show","Not an element node");
        }
        if ( ! show.getNodeName().equals("show")){
            throw new XmlParseException("show","not an show element");
        }
        String ShowID=show.getAttribute("epgId");
        if ( ShowID == null || ShowID.trim().length()==0) {
            
            try {
                // generate unused EPGID
                do {
                    // keep gen'd EPGID less than 12 chars
                    ShowID="EPex"+Integer.toHexString((int)(java.lang.Math.random()*0xFFFFFFF));
                } while (existingIds.contains(ShowID) ||  ShowAPI.GetShowForExternalID(ShowID)!=null);
            } catch (Exception e) {
                throw new XmlParseException("show","no epgId attribute present, and cannot generate one",e);
            }
        }
        extId=ShowID;
        
        title=SageXmlReader.getChildTextElementValue(show,"title");
        episode=SageXmlReader.getChildTextElementValue(show,"episode");
        isFirstRun=SageXmlReader.getChildElementsByTagName(show,"firstRun")!=null;
        
        String tmp=SageXmlReader.getChildTextElementValue(show,"duration");
        if ( tmp != null && tmp.trim().length()>0){
            try { duration=Long.parseLong(tmp)*1000; }
            catch (NumberFormatException e){
                throw new XmlParseException("show Id "+extId,"duration: \""+tmp+"\" is not a number",e);
            }
        }
        
        category=SageXmlReader.getChildTextElementValue(show,"category");
        subCategory=SageXmlReader.getChildTextElementValue(show,"subCategory");
        description=SageXmlReader.getChildTextElementValue(show,"description");
        
        // ratings are localized in the core.
        rating=SageXmlReader.getChildTextElementValue(show,"rating");
        
        Element[] advisoryListNodes=SageXmlReader.getChildElementsByTagName(show,"advisoryList");
        if (advisoryListNodes!=null && advisoryListNodes.length >0 ){
            advisoryList=new java.util.LinkedList<String>();
            advisoryListNodes=SageXmlReader.getChildElementsByTagName(advisoryListNodes[0],"advisory");
            for ( int advnum=0; advnum<advisoryListNodes.length; advnum++){
                advisoryList.add(SageXmlReader.getChildTextValues(advisoryListNodes[advnum]));
            }
        }

        Element[]  peopleListNodes=SageXmlReader.getChildElementsByTagName(show,"peopleList");
        if (peopleListNodes!=null && peopleListNodes.length >0 ){
            peopleList=new java.util.LinkedList<String>();
            peopleRolesList=new java.util.LinkedList<String>();
            peopleListNodes=SageXmlReader.getChildElementsByTagName(peopleListNodes[0],"person");
            for ( int personnum=0; personnum<peopleListNodes.length; personnum++){
                String person=SageXmlReader.getChildTextValues(peopleListNodes[personnum]);
                String xmlRole=peopleListNodes[personnum].getAttribute("role");
                String role=null;
                if ( role==null || role.equals(xmlRole) ){
                    // translation failed -- remove '_'
                    role=xmlRole.replace('_', ' ');
                }
                peopleList.add(person);
                peopleRolesList.add(role);
            }
        }
        
        year=SageXmlReader.getChildTextElementValue(show,"year");
        language=SageXmlReader.getChildTextElementValue(show,"language");
        tmp=SageXmlReader.getChildTextElementValue(show,"originalAirDate");
        if ( tmp != null && tmp.trim().length()>0){
            try {
                Date oad=iso9601Date.parse(tmp);
                originalAirDate=oad.getTime();
            }catch (InvalidDateException e){
                throw new XmlParseException("show Id "+extId,"originalAirDate: \""+tmp+"\" is not a valid date",e);
            }
        }
        misc=new String[] {SageXmlReader.getChildTextElementValue(show,"misc")};

        Element[] airingListNodes=SageXmlReader.getChildElementsByTagName(show,"airing");

        if ( airingListNodes != null && airingListNodes.length>0){
            airingList=new java.util.LinkedList<Airing>();
            for (int airingNum=0;airingNum<airingListNodes.length; airingNum++ ){
                Element airingElem=(Element)airingListNodes[airingNum];
                Airing airing=new Airing(airingElem,this);
                airingList.add(airing);
            }
        }
    }
    

    /**
     * Creates the Sage object for this imported data
     * 
     * @return Object[2]: {Object, String warnings}
     * @throws Exception
     * @throws InvocationTargetException
     */
    public Object[] createSageObject() throws Exception, InvocationTargetException {
        String warnings=null;
        if ( findSageObject() != null )
            warnings="Overwriting existing Show with epgID "+extId;
        
        // roles and ratings are localized in the core.
        String[] sageroles=(String[])(peopleRolesList==null?null:peopleRolesList.toArray(new String[0]));
        if ( sageroles!=null)
            for (int i = 0; i < sageroles.length; i++) {
                String role=sageroles[i];
                sageroles[i]=Utility.LocalizeString(role);
                
                if (sageroles[i]==null|| sageroles[i].equals(role) ){
                    // translation failed -- remove '_'
                    sageroles[i]=role.replace('_', ' ');
                }
            }

        String sageRating=rating;
        if ( sageRating!=null) {
            sageRating=Utility.LocalizeString(rating);
            if ( sageRating==null || sageRating==rating)
                sageRating=rating.replace('_', ' ');
        }
        
        sageShow=
            ShowAPI.AddShow(
                           title,
                           new Boolean(isFirstRun),
                           episode,
                           description,
                           new Long(duration),
                           category,
                           subCategory,
                           (peopleList==null?null:peopleList.toArray(new String[0])),//PeopleList
                           sageroles,//Roles for PeopleList
                           sageRating,//Rated
                           (advisoryList==null?null:advisoryList.toArray(new String[0])),//Expanded Rating
                           year,//Year
                           null,//ParentalRatings
                           misc,//MiscList
                           extId,//ExternalID
                           language,//Language
                           new Long(originalAirDate) //OriginalAirDate
                           );
        return new Object[] {sageShow,warnings};
    }


    public boolean equals(Object compare) {
        if( compare instanceof Show) {
            return this.extId.equals(((Show)compare).extId);
        }
        try {
            if ( ShowAPI.IsShowObject(compare)){
                String sageShowId=ShowAPI.GetShowExternalID(compare);
                if ( sageShowId != null)
                    return sageShowId.equals(this.extId);
            }
        } catch (Exception e){
            System.out.println("net.sf.sageplugins.sagexmlinfo.Show.equals: "+e);
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
    public Object findSageObject() throws InvocationTargetException {
        if ( sageShow != null )
            return sageShow;
        
        sageShow=ShowAPI.GetShowForExternalID(extId);
//        // sanity check returned object
//        String sageTitle=SageApi.StringApi("GetShowTitle", new Object[]{sageObject});
//        if ( sageTitle==null || ! sageTitle.equals(title))
//            return null;
//        
//        String sageEpisode=SageApi.StringApi("GetShowEpisode", new Object[]{sageObject});
//        if ((episode==null && sageEpisode!=null) 
//                ||  sageEpisode==null 
//                || !sageEpisode.equals(this.episode))
//            return null;
//        
        return sageShow;
    }
    
    public String getExtId() {
        return extId;
    }
    public List<String> getAdvisoryList() {
        return Collections.unmodifiableList(advisoryList);
    }
    public List<Airing> getAiringList() {
        return Collections.unmodifiableList(airingList);
    }
    public String getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }
    public long getDuration() {
        return duration;
    }
    public String getEpisode() {
        return episode;
    }
    public boolean isFirstRun() {
        return isFirstRun;
    }
    public String getLanguage() {
        return language;
    }
    public String[] getMisc() {
        return misc;
    }
    public long getOriginalAirDate() {
        return originalAirDate;
    }
    public List<String> getPeopleList() {
        return Collections.unmodifiableList(peopleList);
    }
    public String getRating() {
        return rating;
    }
    public String getSubCategory() {
        return subCategory;
    }
    public String getTitle() {
        return title;
    }
    public String getYear() {
        return year;
    }
}
