package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.debug.DebugService;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.api.enums.AiringAPIEnum;
import sagex.api.enums.ChannelAPIEnum;
import sagex.api.enums.MediaFileAPIEnum;
import sagex.api.enums.ShowAPIEnum;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.util.PropertiesUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DebugServicesImpl extends RemoteServiceServlet implements DebugService {
    private static final long serialVersionUID = 1L;

    public DebugServicesImpl() {
    }

    public Map<String, String> getMetadata(String source, GWTMediaFile file) {
        if ("Wiz.bin".equalsIgnoreCase(source)) {
            return wizbin(file);
        }
        
        if (".properties".equals(source)) {
            return properties(file);
        }

        if ("custom".equals(source)) {
            return custom(file);
        }

        if ("fanart".equals(source)) {
            return fanart(file);
        }
        
        return error("Source Not Implemented: " + source);
    }

    private Map<String, String> fanart(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sage==null) {
            return error("Not a sagetv media file");
        }

        return map;
    }

    private Map<String, String> custom(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sage==null) {
            return error("Not a sagetv media file");
        }
        
        String props = Configuration.GetProperty("custom_metadata_properties", null);
        if (props==null) {
            error("No Properties defined for: custom_metadata_properties");
        }
        
        String all[] = props.split("\\s*;\\s*");
        for (String s: all) {
            map.put(s, MediaFileAPI.GetMediaFileMetadata(sage, s));
        }

        return map;
    }

    private Map<String, String> properties(GWTMediaFile file) {
        Map<String,String> map = new HashMap<String, String>();
        Object sage = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
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
        
        return map;
    }

    private Map<String, String> wizbin(GWTMediaFile file) {
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
        
        return map;
    }

    private Map<String, String> error(String string) {
        Map<String,String> map = new java.util.HashMap<String, String>();
        map.put("Error", string);
        return map;
    }

}
