package org.jdna.sage.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.lang.math.NumberUtils;

import sagex.SageAPI;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.api.Utility;

public class RecordingXmlGenerator implements Runnable {
    private static final String VERSION="1.1";
    
    public RecordingXmlGenerator() {
    }
    public void run(){
        try {
            System.out.println("RecordingXmlGenerator: Started RecordingXmlGenerator thread version "+VERSION+" using SageXML version "+SageXML.SAGEXML_CURRENT_VERSION);
            // Low priority task
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            
            int monitorInterval=NumberUtils.toInt(Configuration.GetProperty("nielm/xmlinfo/monitorRecordingsInterval","300"));//5 mins
            System.out.println("RecordingXmlGenerator: Monitoring recordings every "+Integer.toString(monitorInterval)+" seconds");
            
            // Map of Sage MediaFileID -> MediaFileInfoCopy
            TreeMap<Integer, MediaFileInfoCopy> recordingsInfo=new TreeMap<Integer, MediaFileInfoCopy>(); 
            TreeMap<Integer, MediaFileInfoCopy> newRecordingsInfo=new TreeMap<Integer, MediaFileInfoCopy>();
            while (true) {
                System.out.println("RecordingXmlGenerator: Updating XML files for recordings");
                // at end of loop, newRecordingsInfo contains new/updated/unchanged media files 
                newRecordingsInfo.clear();
                
                Object filelist=MediaFileAPI.GetMediaFiles();
                filelist=SageAPI.call("FilterByBoolMethod",new Object[]{filelist, "IsTVFile", Boolean.TRUE});
                
                // add/update exitsing media files
                for ( int i=0; i < Utility.Size(filelist) ; i ++ ){
                    Object mediafile=Utility.GetElement(filelist,i);
                    try {
                        // get cached info and compare with current info
                        Integer mediafileID=MediaFileAPI.GetMediaFileID(mediafile);
                        MediaFileInfoCopy currInfo=new MediaFileInfoCopy(mediafile);
                        MediaFileInfoCopy oldInfo=recordingsInfo.get(mediafileID);
                        if ( oldInfo == null || !oldInfo.equals(currInfo)){
                            try {
                                String filename=writeXmlForRecording(mediafile);
                                currInfo.xmlFileName=filename;
                                newRecordingsInfo.put(mediafileID,currInfo);
                                recordingsInfo.remove(mediafileID);
                            }catch (FileNotFoundException e){
                                System.out.println("RecordingXmlGenerator: Unable to write xml for medifile ID "+mediafileID.toString()+" -- "+e.getMessage());
                                if ( oldInfo != null){
                                    // error writing updated info: preserve old information for next run-through
                                    newRecordingsInfo.put(mediafileID,oldInfo);
                                    recordingsInfo.remove(mediafileID);
                                }
                            }
                        } else {
                            // Information unchanged -- preserve
                            newRecordingsInfo.put(mediafileID,oldInfo);
                            recordingsInfo.remove(mediafileID);
                        }
                    } catch (Throwable e){
                        System.out.println("RecordingXmlGenerator: Unable to write xml for file: "+mediafile.toString()+" -- exception:"+e.toString());
                        e.printStackTrace();
                        if ( e.getCause()!= null) {
                            System.out.println("RecordingXmlGenerator: Caused by: "+e.getCause().toString());
                            e.getCause().printStackTrace();
                        }
                    }
                }

                // now  newRecordingsInfo contains new/updated/unchanged info
                // recordingsInfo contains info for removed files -- remove any XML files for these removed media files
                Iterator<Integer> it = recordingsInfo.keySet().iterator();
                while ( it.hasNext()){
                    Integer key=it.next();
                    MediaFileInfoCopy info=recordingsInfo.get(key);
                    if ( info != null && info.xmlFileName!=null){
                        File xmlfile=new File(info.xmlFileName);
                        if ( xmlfile.exists()){
                            System.out.println("RecordingXmlGenerator: Deleting: "+info.xmlFileName);
                            if ( ! xmlfile.delete() ){
                                System.out.println("RecordingXmlGenerator: Unable to Delete: "+info.xmlFileName);
                                newRecordingsInfo.put(key,info);
                            } 
                        }
                    }
                }
                // switch new/old recordings info
                TreeMap<Integer, MediaFileInfoCopy> tmp=recordingsInfo;
                recordingsInfo=newRecordingsInfo;
                newRecordingsInfo=tmp;
                newRecordingsInfo.clear();

                System.out.println("RecordingXmlGenerator: Sleeping for "+monitorInterval+" seconds");
                Thread.sleep(monitorInterval*1000);
            }
        }catch (Throwable e) {
            System.out.println("RecordingXmlGenerator: Unhandled exception in RecordingXmlGenerator thread: "+e.toString());
            e.printStackTrace();
            if ( e.getCause()!= null) {
                System.out.println("caused by "+e.getCause().toString());
                e.getCause().printStackTrace();
            }
        }
            
    }
    
    private String  writeXmlForRecording(Object mediafile)
    throws Exception
    {        
        // generate file name from first segment
        File[] files=MediaFileAPI.GetSegmentFiles(mediafile);
        if ( files == null || files.length==0){
            throw new FileNotFoundException("MediaFile has no files");
        }
        if ( !files[0].exists()){
            throw new FileNotFoundException(files[0].getAbsolutePath()+" File does not exist on disk");
        }
        System.out.println("RecordingXmlGenerator: Writing XML information for file "+files[0].getAbsolutePath());
        
        File xmlfilename=new File(files[0].getAbsolutePath()+".xml");
        File tmpfilename=new File(files[0].getAbsolutePath()+".xmltmp");
        SageXmlWriter writer=new SageXmlWriter();
        writer.add(mediafile);
        try { 
            if ( writer.write(tmpfilename)){
                if ( xmlfilename.exists()) {
                    xmlfilename.delete();
                }
                if ( tmpfilename.renameTo(xmlfilename) ){
                    if ( files[files.length-1].exists())
                        xmlfilename.setLastModified(files[files.length-1].lastModified());
                    return xmlfilename.getAbsolutePath();
                } else {
                    throw new FileNotFoundException("unable to rename "+tmpfilename.getAbsolutePath()+" to "+xmlfilename.getAbsolutePath());
                }
            }
        }finally{
            tmpfilename.delete();
        }
        throw new Exception("should not be here");
    }
}
