package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.app.SupportOptions;
import org.jdna.bmt.web.client.ui.debug.DebugService;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.api.enums.AiringAPIEnum;
import sagex.api.enums.ChannelAPIEnum;
import sagex.api.enums.MediaFileAPIEnum;
import sagex.api.enums.ShowAPIEnum;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.metadata.MetadataUtil;
import sagex.phoenix.tools.support.Tools;
import sagex.phoenix.tools.support.Troubleshooter;
import sagex.phoenix.util.PropertiesUtils;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.sage.SageMediaFile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DebugServicesImpl extends RemoteServiceServlet implements DebugService {
    private transient Logger log = Logger.getLogger(DebugServicesImpl.class);
    
    private static final long serialVersionUID = 1L;

    public DebugServicesImpl() {
    }

    public ServiceReply<Map<String, String>> getMetadata(String source, GWTMediaFile file) {
        if ("Wiz.bin".equalsIgnoreCase(source)) {
            return wizbin(file);
        }
        
        if (".properties".equals(source)) {
            return properties(file);
        }

        if ("custom".equals(source)) {
            return custom(file);
        }

        if ("GWTMediaFile".equals(source)) {
            return getMediaFileData(file);
        }

        if ("metadata".equals(source)) {
            return getMediaFileMetaData(file);
        }

        if ("sage7metadata".equals(source)) {
            return sage7metadata(file);
        }
        
        return error("Source Not Implemented: " + source);
    }

    private ServiceReply<Map<String, String>> sage7metadata(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        IMediaFile mf = new SageMediaFile(null, sage);
        IMetadata md = mf.getMetadata();
        
        Map<String,String> props = new HashMap<String, String>();
        IMetadata newMD = MetadataUtil.createMetadata(props);
        try {
			MetadataUtil.copyMetadata(md, newMD);
		} catch (Exception e) {
			return error("Failed to create metadata!");
		}
        
        for (Map.Entry<String,String> me : props.entrySet()) {
        	if (me.getValue()!=null) {
        		map.put(String.valueOf(me.getKey()), String.valueOf(me.getValue()));
        	}
        }
        
        return new ServiceReply<Map<String,String>>(map);
	}

	private ServiceReply<Map<String, String>> getMediaFileMetaData(GWTMediaFile file) {
		return sage7metadata(file);
    }

    private ServiceReply<Map<String, String>> getMediaFileData(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("getAiringId", file.getAiringId());
        map.put("getMinorTitle", file.getMinorTitle());
        map.put("getPath", file.getPath());
        map.put("getShowId", file.getShowId());
        map.put("getThumbnailUrl", file.getThumbnailUrl());
        map.put("getTitle", file.getTitle());
        map.put("getSageMediaFileId", String.valueOf(file.getSageMediaFileId()));
        map.put("isReadOnly", String.valueOf(file.isReadOnly()));
        return new ServiceReply<Map<String,String>>(map);
    }

    private ServiceReply<Map<String, String>> custom(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sage==null) {
            return error("Not a sagetv media file");
        }
        
        String props = Configuration.GetProperty("custom_metadata_properties", null);
        if (props==null) {
            return error("No Properties defined for: custom_metadata_properties");
        }
        
        String all[] = props.split("\\s*;\\s*");
        for (String s: all) {
            map.put(s, MediaFileAPI.GetMediaFileMetadata(sage, s));
        }

        return new ServiceReply<Map<String,String>>(map);
    }

    private ServiceReply<Map<String, String>> properties(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        Object sage = phoenix.media.GetSageMediaFile(file.getSageMediaFileId());
        if (sage==null) {
            return error("Not a sagetv media file");
        }
        
        File f = MediaFileAPI.GetFileForSegment(sage, 0);
        File propFile = FanartUtil.resolvePropertiesFile(f);
        if (propFile==null) {
            return error("No Property File for: " + f.getAbsolutePath());
        }
        
        if (!propFile.exists()) {
            return error("No Property File: " + propFile.getAbsolutePath());
        }
        
        Properties props = new Properties();
        try {
            PropertiesUtils.load(props, propFile);
        } catch (IOException e) {
            return error("Error Loading Properties: " + e.getMessage());
        }
        
        for (Object s: props.keySet()) {
            map.put(String.valueOf(s), String.valueOf(props.getProperty(String.valueOf(s))));
        }
        
        return new ServiceReply<Map<String,String>>(map);
    }

    private ServiceReply<Map<String, String>> wizbin(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sage==null) {
            return error("Not a sagetv media file");
        }
        
        String names[] = {
                MediaFileAPIEnum.IsBluRay.name(),
                MediaFileAPIEnum.IsCompleteRecording.name(),
                MediaFileAPIEnum.IsDVD.name(),
                MediaFileAPIEnum.IsLibraryFile.name(),
                MediaFileAPIEnum.IsThumbnailLoaded.name(),
                MediaFileAPIEnum.IsTVFile.name(),
                MediaFileAPIEnum.IsVideoFile.name(),
                MediaFileAPIEnum.GetMediaFileID.name(),
                MediaFileAPIEnum.GetMediaTitle.name(),
                MediaFileAPIEnum.GetNumberOfSegments.name(),
                AiringAPIEnum.GetAiringDuration.name(),
                AiringAPIEnum.GetAiringTitle.name(),
                AiringAPIEnum.GetExtraAiringDetails.name(),
                AiringAPIEnum.GetParentalRating.name(),
                AiringAPIEnum.IsAiringHDTV.name(),
                AiringAPIEnum.IsDontLike.name(),
                AiringAPIEnum.IsFavorite.name(),
                AiringAPIEnum.IsManualRecord.name(),
                AiringAPIEnum.IsWatched.name(),
                AiringAPIEnum.IsWatchedCompletely.name(),
                ShowAPIEnum.GetShowCategory.name(),
                ShowAPIEnum.GetShowDescription.name(),
                ShowAPIEnum.GetShowDuration.name(),
                ShowAPIEnum.GetShowEpisode.name(),
                ShowAPIEnum.GetShowExpandedRatings.name(),
                ShowAPIEnum.GetShowExternalID.name(),
                ShowAPIEnum.GetShowMisc.name(),
                ShowAPIEnum.GetShowParentalRating.name(),
                ShowAPIEnum.GetShowRated.name(),
                ShowAPIEnum.GetShowSubCategory.name(),
                ShowAPIEnum.GetShowTitle.name(),
                ShowAPIEnum.GetShowYear.name(),
                ShowAPIEnum.IsShowEPGDataUnique.name(),
                ShowAPIEnum.IsShowFirstRun.name(),
                ShowAPIEnum.IsShowReRun.name()
        };

        for (String s: names) {
            try {
                map.put(s, String.valueOf(SageAPI.call(s, new Object[] {sage})));
            } catch (Exception e) {
                map.put(s,"Error: " + e.getMessage());
            }
        }
        
        Object ch = AiringAPI.GetChannel(sage);
        if (ch!=null) {
            map.put(ChannelAPIEnum.GetChannelDescription.name(),ChannelAPI.GetChannelDescription(ch));
            map.put(ChannelAPIEnum.GetChannelName.name(),ChannelAPI.GetChannelName(ch));
            map.put(ChannelAPIEnum.GetChannelNetwork.name(),ChannelAPI.GetChannelNetwork(ch));
            map.put(ChannelAPIEnum.GetChannelNumber.name(),ChannelAPI.GetChannelNumber(ch));
            map.put(ChannelAPIEnum.GetStationID.name(),String.valueOf(ChannelAPI.GetStationID(ch)));
        }
        
        String peeps[] = ShowAPI.GetPeopleAndCharacterListInShowInRole(sage, "Actor");
        if (peeps!=null) {
        	int i=1;
        	for (String p : peeps) {
        		map.put("Actor: " + i++, p);
        	}
        } else {
        	map.put("No Actors", "GetPeopleAndCharacterListInShowInRole() returned nothing");
        }
        
        return new ServiceReply<Map<String,String>>(map);
    }

    private ServiceReply<Map<String, String>> error(String string) {
    	return new ServiceReply<Map<String,String>>(1, string);
    }

    public long updateTimestamp(GWTMediaFile file) {
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        SageMediaFile smf = new SageMediaFile(null, sage);
        smf.touch(smf.lastModified()+SageMediaFile.MIN_TOUCH_ADJUSTMENT);
        return smf.lastModified();
    }

    public String createSupportRequest(SupportOptions options) {
        try {
            File out = Troubleshooter.createSupportZip(options.getComment().get(),
                    options.getIncludeLogs().get(),
                    options.getIncludeProperties().get(),
                    options.getIncludeSageImports().get());
            if (out==null) throw new Exception("Failed to create support file");
            return out.getCanonicalPath();
        } catch (Exception e) {
            log("Failed to create support file!", e);
            throw new RuntimeException(e);
        }
    }

    public int removeMetadataProperties() {
        return Tools.removeMetadataProperties(Configuration.GetVideoLibraryImportPaths());
    }

    public void backupWizBin() {
        try {
            Troubleshooter.backupWizBin();
        } catch (Exception e) {
            log.warn("Backup Failed!", e);
            throw new RuntimeException(e);
        }
    }

    public String[] getWizBinBackups() {
        File dir = new File("backups");
        File files[] = dir.listFiles();
        List<String> backups = new ArrayList<String>();
        if (files!=null && files.length>0) {
            for (File f : files) {
                backups.add(f.getAbsolutePath());
            }
        }
        return backups.toArray(new String[] {});
    }
}
