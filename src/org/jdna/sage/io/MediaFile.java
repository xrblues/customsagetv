package org.jdna.sage.io;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sagex.api.MediaFileAPI;


public class MediaFile extends ImportableObject {
    private final Integer id;
    private long startTime=0;
    private long duration=0;
    private boolean archived=false;
    private String type=null;
    private LinkedList<MediaFileSegment> segments=new LinkedList<MediaFileSegment>(); // of Segments
    
    static final public String TYPE_MUSIC="Music";
    static final public String TYPE_TV="TV";
    static final public String TYPE_IMPORTEDVIDEO="ImportedVideo";
    static final public String TYPE_PICTURE="Picture";
    static final public String TYPE_IMPORTEDDVD="ImportedDVD";
    private static final String TYPE_IMPORTEDBLURAY = "ImportedBluRay";

    static Element createXmlElement(Document doc,Object mediafile) 
    throws InvocationTargetException,IllegalArgumentException {
        if ( mediafile == null ) 
            return null;
        if (! MediaFileAPI.IsMediaFileObject(mediafile))
            throw new IllegalArgumentException("Object type: \""+mediafile.getClass().getName()+"\" is not a MediaFile");
        
        // Find existing MF...
        Integer mfid=MediaFileAPI.GetMediaFileID(mediafile);
        Element mediafileNode=doc.createElement("mediafile");
        mediafileNode.setAttribute("sageDbId",mfid.toString());
        
        mediafileNode.setAttribute("type",GetMediaFileType(mediafile));
        
        Long startTimeMillis=MediaFileAPI.GetFileStartTime(mediafile);
        Date startDate=new Date(startTimeMillis.longValue());
        mediafileNode.setAttribute("startTime",iso9601Date.getIsoDate(startDate));
        
        Long durationMillis=MediaFileAPI.GetFileDuration(mediafile);
        mediafileNode.setAttribute("duration",Long.toString((durationMillis.longValue()+500)/1000));

        // add archived flag
        if ( MediaFileAPI.IsLibraryFile(mediafile)) 
            mediafileNode.appendChild(doc.createElement("archived"));
        
        Element segmentList=(Element)mediafileNode.appendChild(doc.createElement("segmentList"));
        File[] files=MediaFileAPI.GetSegmentFiles(mediafile);
        for ( int i=0;i<files.length;i++){
            Element segment=(Element)segmentList.appendChild(doc.createElement("segment"));

            startTimeMillis=MediaFileAPI.GetStartForSegment(mediafile,i);
            startDate=new Date(startTimeMillis.longValue());
            segment.setAttribute("startTime",iso9601Date.getIsoDate(startDate));
            durationMillis=MediaFileAPI.GetDurationForSegment(mediafile,i);
            segment.setAttribute("duration",Long.toString((durationMillis.longValue()+500)/1000));
            segment.setAttribute("filePath",files[i].getAbsolutePath());
        }
        return mediafileNode;
    }
    
    
    MediaFile(Element mediafile, Airing airing) throws XmlParseException{
        if ( mediafile.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE ){
            throw new XmlParseException("mediafile","Not an element node");
        }
        if ( ! mediafile.getNodeName().equals("mediafile")){
            throw new XmlParseException("mediafile","not an mediafile element");
        }
        
        String idStr=mediafile.getAttribute("sageDbId");
        if ( idStr != null && idStr.trim().length()>0) {
            try {
                id=new Integer(idStr);
            } catch (NumberFormatException e){
                throw new XmlParseException("mediafile","sageDbId \""+idStr+"\" is not a number",e);
            }
            idStr="mediafile:"+id;
        } else{
            id=null;
            if ( airing.getId() != null )
                idStr="mediafile for airing:"+airing.getId();
            else
                idStr="mediafile for show:"+airing.getShow().getExtId();
        }
        
        String tmp=mediafile.getAttribute("startTime");
        if ( tmp==null || tmp.trim().length()==0)
            startTime=airing.getStartTime();
        else {
            try {
                Date startDate=iso9601Date.parse(tmp);
                startTime=startDate.getTime();
            }catch (InvalidDateException e){
                throw new XmlParseException(idStr,"startTime \""+tmp+"\" is not valid Iso9601 date",e);
            }
        }
        tmp=mediafile.getAttribute("duration");
    
        if ( tmp==null|| tmp.trim().length()==0)
            duration=airing.getDuration();
        else {
            try {
                duration=Long.parseLong(tmp)*1000;
            }catch (NumberFormatException e){
                throw new XmlParseException(idStr,"duration \""+tmp+"\" is not a number",e);
            }
        }
        tmp=mediafile.getAttribute("type");
        if ( tmp == null || tmp.trim().length()==0)
            type=TYPE_TV;
        if (tmp.equalsIgnoreCase(TYPE_TV))
            type=TYPE_TV;
        else if ( tmp.equalsIgnoreCase(TYPE_IMPORTEDDVD))
            type=TYPE_IMPORTEDDVD;
        else if ( tmp.equalsIgnoreCase(TYPE_IMPORTEDBLURAY))
            type=TYPE_IMPORTEDBLURAY;
        else if ( tmp.equalsIgnoreCase(TYPE_IMPORTEDVIDEO))
            type=TYPE_IMPORTEDVIDEO;
        else if ( tmp.equalsIgnoreCase(TYPE_MUSIC))
            type=TYPE_MUSIC;
        else if (tmp.equalsIgnoreCase(TYPE_PICTURE))
            type=TYPE_PICTURE;
        else throw new XmlParseException(idStr,"type \""+tmp+"\" is not valid");

        archived=(SageXmlReader.getChildElementsByTagName(mediafile, "archived")!=null);

        
        Element []segmentElems=SageXmlReader.getChildElementsByTagName(mediafile, "segmentList");
        if ( segmentElems==null || segmentElems.length==0)
            throw new XmlParseException(idStr,"type \""+tmp+"\" - no segmentList");
        
        segmentElems=SageXmlReader.getChildElementsByTagName(segmentElems[0], "segment");
        if ( segmentElems.length==0){
            throw new XmlParseException(idStr,"type \""+tmp+"\" - no segments");
        }
        for (int i = 0; i < segmentElems.length; i++) {
            String path=segmentElems[i].getAttribute("filePath").trim();
            if ( path ==null || path.length()==0){
                throw new XmlParseException(idStr+" segment:"+i,"no filePath specified");
            }
            tmp=segmentElems[i].getAttribute("startTime");
            long segStartTime;
            if ( tmp==null|| tmp.trim().length()==0) {
                if ( segmentElems.length == 1) {
                    segStartTime=startTime;
                }else {
                    throw new XmlParseException(idStr+" segment:"+i,"startTime not specified");
                }
            } else {
                try {
                    Date startDate=iso9601Date.parse(tmp);
                    segStartTime=startDate.getTime();
                }catch (InvalidDateException e){
                    throw new XmlParseException(idStr,"startTime \""+tmp+"\" is not valid Iso9601 date",e);
                }
            }
            tmp=segmentElems[i].getAttribute("duration");
            long segDuration;
            if ( tmp==null|| tmp.trim().length()==0) {
                if ( segmentElems.length == 1) {
                    segDuration=duration;
                } else {
                    throw new XmlParseException(idStr+" segment:"+i,"duration not specified");
                }
            } else {
                try {
                    segDuration=Long.parseLong(tmp);
                }catch (NumberFormatException e){
                    throw new XmlParseException(idStr+" segment:"+i,"duration \""+tmp+"\" is not a number",e);
                }
            }

            segments.add(new MediaFileSegment(path,segStartTime,segDuration));
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
        if ( this.segments.size()>1){
            warnings="Cannot import all segmented files, only importing first file";
        }
        Object mediaFile=MediaFileAPI.AddMediaFile(
                new File(segments.getFirst().getFilename()),
                null
        );
        return new Object[]{mediaFile,warnings};
    }


    /**
     * finds the matching Sage object,
     * 
     * @return Object or null if not found.
     * @throws InvocationTargetException
     */
    public Object findSageObject() throws InvocationTargetException {
        if ( segments.isEmpty())
            return null;
        MediaFileSegment firstSegment=segments.getFirst();
        File firstFile=new File(firstSegment.getFilename());
        
        Object mediaFile=MediaFileAPI.GetMediaFileForFilePath(firstFile);
        return mediaFile;
    }
    
    
    
    public boolean equals(Object other) {
        if ( other instanceof MediaFile){
            MediaFile otherMf=(MediaFile)other;
            if ( this.type != otherMf.type
                 || java.lang.Math.abs(this.startTime-otherMf.startTime)>10000
                 || this.segments.size() != otherMf.segments.size() )
                return false;
            // check each segment
            Iterator<MediaFileSegment> thisIter = this.segments.iterator();
            Iterator<MediaFileSegment> otherIter = otherMf.segments.iterator();
            for (; thisIter.hasNext() && otherIter.hasNext() ;) {
                MediaFileSegment thisElement = thisIter.next();
                MediaFileSegment otherElement = otherIter.next();
                if ( ! thisElement.equals(otherElement))
                    return false;
            }
            // all segments equal
            return true;
        } else { 
            try {
                if ( ! MediaFileAPI.IsMediaFileObject(other)){
                    return false;
                }
                // compare to sage object
                // check type.
                if ( this.type != null && this.type.equals(GetMediaFileType(other)))
                    return false;
                // check startime
                Long otherStartTime=MediaFileAPI.GetFileStartTime(other);
                if ( otherStartTime == null 
                     || java.lang.Math.abs(this.startTime-otherStartTime.longValue())>10000)
                    return false;
                // check segments
                File[] otherSegments=MediaFileAPI.GetSegmentFiles(other);
                if ( otherSegments==null 
                        || otherSegments.length != this.segments.size())
                    return false;
                
                MediaFileSegment[] thisSegments=segments.toArray(new MediaFileSegment[0]);
                for (int i = 0; i < thisSegments.length; i++) {
                    File thisFile=new File(thisSegments[i].getFilename());
                    if ( ! thisFile.equals(otherSegments[i]))
                        return false;
                }
                return true;
            } catch (InvocationTargetException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return false;
    }


    public Integer getId() {
        return id;
    }


    public boolean isArchived() {
        return archived;
    }


    public long getDuration() {
        return duration;
    }


    public List<MediaFileSegment> getSegments() {
        return java.util.Collections.unmodifiableList(segments);
    }


    public long getStartTime() {
        return startTime;
    }


    public String getType() {
        return type;
    }
    
    static String GetMediaFileType(Object mediafile)
    throws InvocationTargetException
    {
        // Type: TV | ImportedVideo | ImportedDVD | Music | Picture
        if ( MediaFileAPI.IsVideoFile(mediafile)) {
            if ( MediaFileAPI.IsTVFile(mediafile)) {
                return TYPE_TV;
            } else {
                return TYPE_IMPORTEDVIDEO;
            }
        } else if ( MediaFileAPI.IsMusicFile(mediafile)) {
            return TYPE_MUSIC;
        } else if ( MediaFileAPI.IsPictureFile(mediafile)) {
            return TYPE_PICTURE;
        } else if ( MediaFileAPI.IsDVD(mediafile)) {
           return TYPE_IMPORTEDDVD;
        } else if ( MediaFileAPI.IsBluRay(mediafile)) {
            return TYPE_IMPORTEDBLURAY;
        } else {
            throw new IllegalArgumentException("invalid/unknown mediafile type");
        }
       
    }
}
