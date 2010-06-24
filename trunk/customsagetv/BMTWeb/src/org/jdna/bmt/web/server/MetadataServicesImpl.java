package org.jdna.bmt.web.server;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.browser.MetadataService;
import org.jdna.bmt.web.client.ui.browser.PersistenceOptionsUI;
import org.jdna.bmt.web.client.ui.browser.ProgressStatus;
import org.jdna.bmt.web.client.ui.browser.SearchQueryOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.Property;

import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.metadata.CastMember;
import sagex.phoenix.metadata.ICastMember;
import sagex.phoenix.metadata.IMediaArt;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.metadata.IMetadataOptions;
import sagex.phoenix.metadata.IMetadataProvider;
import sagex.phoenix.metadata.IMetadataSearchResult;
import sagex.phoenix.metadata.MediaArt;
import sagex.phoenix.metadata.MediaArtifactType;
import sagex.phoenix.metadata.MediaType;
import sagex.phoenix.metadata.MetadataConfiguration;
import sagex.phoenix.metadata.MetadataException;
import sagex.phoenix.metadata.MetadataOptions;
import sagex.phoenix.metadata.PhoenixMetadataSupport;
import sagex.phoenix.metadata.persistence.PersistenceUtil;
import sagex.phoenix.metadata.proxy.MetadataProxy;
import sagex.phoenix.metadata.search.MetadataSearchUtil;
import sagex.phoenix.metadata.search.SearchQuery;
import sagex.phoenix.metadata.search.SearchQuery.Field;
import sagex.phoenix.progress.TrackedItem;
import sagex.phoenix.util.DateUtils;
import sagex.phoenix.util.url.UrlUtil;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.sage.SageMediaFile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MetadataServicesImpl extends RemoteServiceServlet implements MetadataService {
	PhoenixMetadataSupport support = new PhoenixMetadataSupport();
	
    private Logger log = Logger.getLogger(MetadataServicesImpl.class);

    public MetadataServicesImpl() {
    }
    
    public List<GWTProviderInfo> getProviders() {
        List<IMetadataProvider> providers = Phoenix.getInstance().getMetadataManager().getProviders();
        List<GWTProviderInfo> info = new ArrayList<GWTProviderInfo>();
        for (IMetadataProvider p : providers) {
            info.add(new GWTProviderInfo(p.getInfo()));
        }
        return info;
    }

    public GWTMediaMetadata loadMetadata(GWTMediaFile mediaFile) {
        try {
            log.debug("Fetching Current Metadata for Item: " + mediaFile.getSageMediaFileId() + "; " + mediaFile.getTitle());

            IMediaFile mf = new SageMediaFile(null, phoenix.api.GetSageMediaFile(mediaFile.getSageMediaFileId()));
            if (mf==null) throw new Exception("Invalid Media File: " + mediaFile);
            
            GWTMediaMetadata md = newMetadata(mf, mf.getMetadata());
            if (StringUtils.isEmpty(md.getMediaType().get())) {
            	if (mf.isType(MediaResourceType.TV.value())) {
            		md.getMediaType().set("TV");
            	} else {
            		md.getMediaType().set("Movie");
            	}
            }
            return md;
        } catch (Throwable e) {
            log.error("Failed to get metadata: " + mediaFile.getSageMediaFileId() + "; " + mediaFile.getTitle(), e);
            throw new RuntimeException(e);
        }
    }

    public GWTMediaMetadata newMetadata(IMediaFile file, IMetadata source) {
        GWTMediaMetadata mi = new GWTMediaMetadata();
        if (source != null) {
        	mi.getDescription().set(source.getDescription());
        	mi.getDiscNumber().set(String.valueOf(source.getDiscNumber()));
        	mi.getEpisodeName().set(source.getEpisodeName());
        	mi.getEpisodeNumber().set(String.valueOf(source.getEpisodeNumber()));
        	mi.getExtendedRatings().set(source.getExtendedRatings());
        	mi.getExternalID().set(source.getExternalID());
        	mi.getIMDBID().set(source.getIMDBID());
        	mi.getMediaProviderDataID().set(source.getMediaProviderDataID());
        	mi.getMediaProviderID().set(source.getMediaProviderID());
        	mi.getMediaTitle().set(source.getMediaTitle());
        	mi.getMediaType().set(source.getMediaType());
        	mi.getMisc().set(source.getMisc());
        	mi.getOriginalAirDate().set(DateUtils.formatDate(source.getOriginalAirDate()));
        	mi.getParentalRating().set(source.getParentalRating());
        	mi.getRated().set(source.getRated());
        	mi.getRunningTime().set(String.valueOf(source.getRunningTime()));
        	mi.getSeasonNumber().set(String.valueOf(source.getSeasonNumber()));
        	mi.getTitle().set(source.getTitle());
        	mi.getUserRating().set(String.valueOf(source.getUserRating()));
        	mi.getYear().set(String.valueOf(source.getYear()));

        	for (ICastMember cm : source.getActors()) {
        		mi.getActors().add(new GWTCastMember(cm));
        	}

        	for (ICastMember cm : source.getDirectors()) {
        		mi.getDirectors().add(new GWTCastMember(cm));
        	}

        	for (ICastMember cm : source.getGuests()) {
        		mi.getGuests().add(new GWTCastMember(cm));
        	}
        	
        	for (ICastMember cm : source.getWriters()) {
        		mi.getWriters().add(new GWTCastMember(cm));
        	}
        	
        	for (String g: source.getGenres()) {
        		mi.getGenres().add(new Property<String>(g));
        	}
        	
        	for (IMediaArt ma: source.getFanart()) {
        		mi.getFanart().add(new GWTMediaArt(ma));
        	}
        }
        
        if(file!=null) {
            // depending on if the file is a Recording or Not, some fields are readonly
        	MetadataConfiguration config = GroupProxy.get(MetadataConfiguration.class);
        	mi.getPreserveRecordingMetadata().set(config.getPreserverRecordingMetadata());
        }
    	
        return mi;
    }

    public List<GWTMediaSearchResult> searchForMetadata(GWTMediaFile item, SearchQueryOptions options) {
        List<GWTMediaSearchResult> results = new ArrayList<GWTMediaSearchResult>();
        try {
            MediaType type = MediaType.toMediaType(options.getType().get());

            // build a custom query from the options
            SearchQuery query = new SearchQuery();
            query.setMediaType(type);
            query.set(Field.QUERY, options.getSearchTitle().get());
            query.set(Field.YEAR, options.getYear().get());
            if (type == MediaType.TV) {
                query.set(Field.EPISODE_TITLE,options.getEpisodeTitle().get());
                query.set(Field.EPISODE,options.getEpisode().get());
                query.set(Field.SEASON,options.getSeason().get());
            }
            
            List<IMetadataSearchResult> sresults = Phoenix.getInstance().getMetadataManager().search(query);
            if (sresults==null) throw new Exception("Search Failed for: " + query);
            for (IMetadataSearchResult r : sresults) {
                GWTMediaSearchResult res = new GWTMediaSearchResult();
                res.setId(r.getId());
                res.setMediaType(r.getMediaType());
                res.setProviderId(r.getProviderId());
                res.setScore(r.getScore());
                res.setTitle(r.getTitle());
                res.setUrl(r.getUrl());
                res.setYear(r.getYear());
                res.setMediaFileId(item.getSageMediaFileId());
                res.getExtra().putAll(r.getExtra());
                results.add(res);
            }
        } catch (Exception e) {
            log.warn("WebUI Search failed for: " + options.getSearchQuery(), e);
        }
        return results;
    }

    public GWTMediaMetadata getMetadata(GWTMediaSearchResult result, GWTPersistenceOptions mdOptions) {
        try {
        	log.debug("Getting Metadata for result: " + result);
            return newMetadata(null, Phoenix.getInstance().getMetadataManager().getMetdata(result));
        } catch (Exception e) {
            log.error("Metadata Retreival Failed!", e);
            throw new RuntimeException(e);
        }
    }


    public String scan(final GWTMediaFolder folder, final PersistenceOptionsUI options) {
        log.debug("Scanning Folder: " + folder);
        IMediaFolder realFolder = BrowsingServicesImpl.getFolderRef(folder, getThreadLocalRequest());
        IMetadataOptions opts = new MetadataOptions();
        opts.setIsScanningOnlyMissingMetadata(options.getScanOnlyMissingMetadata().get());
        opts.setIsScanningSubFolders(options.getIncludeSubDirs().get());
        return (String) support.startMetadataScan(realFolder, opts);
    }

    public ProgressStatus[] getScansInProgress() {
    	Object scans[] = support.getTrackers();
    	
        List<ProgressStatus> all = new ArrayList<ProgressStatus>();
        for (Object id : scans) {
            all.add(getStatus((String)id));
        }
        return all.toArray(new ProgressStatus[] {});
    }

    public ProgressStatus getStatus(String id) {
        log.debug("Getting Progress Status for: " + id);
        ProgressStatus status = new ProgressStatus();
        status.setProgressId(id);
        
        if (id==null) {
            log.debug("No Tracker for Id: " + id);
            status.setIsDone(true);
        } else {
            status.setComplete(support.getMetadataScanComplete(id));
            status.setIsCancelled(support.isMetadataScanCancelled(id));
            status.setIsDone(!support.isMetadataScanRunning(id));
            status.setStatus(support.getMetadataScanStatus(id));
            status.setTotalWork(support.getTotalWork(id));
            status.setWorked(support.getWorked(id));
            status.setSuccessCount(support.getSkippedCount(id));
            status.setFailedCount(support.getFailedCount(id));
            status.setLabel(support.getMetadataScanLabel(id));
            status.setDate(support.getMetadataScanLastUpdated(id));
        }
        return status;
    }
    
    public void cancelScan(String id) {
    	support.cancelMetadataScan(id);
    }
    
    public void removeScan(String progressId) {
        cancelScan(progressId);
        support.removeMetadataScan(progressId);
    }

    public GWTMediaResource[] getProgressItems(String progressId, boolean b) {
        log.debug("Getting Items for progress: " + progressId);
        
        LinkedList<TrackedItem<IMediaFile>> items = null;
        if (b) {
            items = support.getSuccessfulItems(progressId);
        } else {
            items = support.getFailedItems(progressId);
        }

        List<GWTMediaResource> gwtitems = new ArrayList<GWTMediaResource>();
        for (TrackedItem<IMediaFile> i : items) {
            GWTMediaResource res = BrowsingServicesImpl.convertResource(i.getItem(), getThreadLocalRequest());
            gwtitems.add(res);
            if (i.getMessage()!=null) {
                res.setMessage(i.getMessage());
            }
        }
        GWTMediaResource all[] = (gwtitems.toArray(new GWTMediaResource[] {}));
        log.debug("Created Reply with " + all.length + " items.");
        return all;
    }

    private String makeLocalMediaUrl(String url) {
        return "media/get?i=" + UrlUtil.encode(url);
    }

    
    public ServiceReply<GWTMediaFile> saveMetadata(GWTMediaFile file, PersistenceOptionsUI uiOptions) {
    	IMediaFile mf = new SageMediaFile(null, phoenix.api.GetSageMediaFile(file.getSageMediaFileId()));
    	IMetadata md = MetadataProxy.newInstance();
    	GWTMediaMetadata gmd = file.getMetadata();
    	
    	md.setDescription(gmd.getDescription().get());
    	md.setDiscNumber(NumberUtils.toInt(gmd.getDiscNumber().get()));
    	md.setEpisodeName(gmd.getEpisodeName().get());
    	md.setEpisodeNumber(NumberUtils.toInt(gmd.getEpisodeNumber().get()));
    	md.setExtendedRatings(gmd.getExtendedRatings().get());
    	md.setExternalID(gmd.getExternalID().get());
    	md.setIMDBID(gmd.getIMDBID().get());
    	md.setMediaProviderDataID(gmd.getMediaProviderDataID().get());
    	md.setMediaProviderID(gmd.getMediaProviderID().get());
    	md.setMediaTitle(gmd.getMediaTitle().get());
    	md.setMediaType(gmd.getMediaType().get());
    	md.setMisc(gmd.getMisc().get());
    	md.setOriginalAirDate(DateUtils.parseDate(gmd.getOriginalAirDate().get()));
    	md.setParentalRating(gmd.getParentalRating().get());
    	md.setRated(gmd.getRated().get());
    	md.setRunningTime(NumberUtils.toLong(gmd.getRunningTime().get()));
    	md.setSeasonNumber(NumberUtils.toInt(gmd.getSeasonNumber().get()));
    	md.setTitle(gmd.getTitle().get());
    	md.setUserRating(MetadataSearchUtil.parseUserRating(gmd.getUserRating().get()));
    	md.setYear(NumberUtils.toInt(gmd.getYear().get()));

    	for (ICastMember cm: gmd.getActors()) {
    		md.getActors().add(new CastMember(cm));
    	}

    	for (ICastMember cm: gmd.getDirectors()) {
    		md.getDirectors().add(new CastMember(cm));
    	}
    	
    	for (ICastMember cm: gmd.getGuests()) {
    		md.getGuests().add(new CastMember(cm));
    	}

    	for (ICastMember cm: gmd.getWriters()) {
    		md.getWriters().add(new CastMember(cm));
    	}
    	
    	for (Property<String> s: gmd.getGenres()) {
    		md.getGenres().add(s.get());
    	}
    	
    	for (IMediaArt ma : gmd.getFanart()) {
    		md.getFanart().add(new MediaArt(ma));
    	}

    	try {
			Phoenix.getInstance().getMetadataManager().updateMetadata(mf, md, null);
			file.attachMetadata(newMetadata(mf, mf.getMetadata()));
		} catch (MetadataException e) {
			log.warn("Failed to save metadata for file: " + mf, e);
			return new ServiceReply<GWTMediaFile>(1, "Failed to save metadata/fanart: " + e.getMessage(), file);
		}
		
		return new ServiceReply<GWTMediaFile>(0, "ok", file);
    }

    public ArrayList<GWTMediaArt> getFanart(GWTMediaFile file, MediaArtifactType artifact) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sageMF == null) {
            log.error("Failed to get sage mediafile for: " + file);
            return null;
        }
        ArrayList<GWTMediaArt> files = new ArrayList<GWTMediaArt>();
        log.debug("Getting fanart: " + file.getType() + "; " + artifact + "; " + sageMF);
        String fanart[] = phoenix.api.GetFanartArtifacts(sageMF, null, null, artifact.name(), null, null);
        if (fanart!=null) {
            for (String fa : fanart) {
                GWTMediaArt ma = new GWTMediaArt();
                ma.setDeleted(false);
                ma.setLocal(true);
                ma.setLocalFile(fa);
                ma.setDisplayUrl(makeLocalMediaUrl(fa));
                files.add(ma);
            }
        }
        return files;
    }

    public GWTMediaArt downloadFanart(GWTMediaFile file, MediaArtifactType artifact, GWTMediaArt ma) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sageMF == null) {
            log.error("Failed to get sage mediafile for: " + file);
            return null;
        }
        String fanartDir = phoenix.api.GetFanartArtifactDir(sageMF, null, null, artifact.name(), null, null, true);
        File dir =new File(fanartDir);
        String name = new File(ma.getDownloadUrl()).getName();
        File local = new File(dir, name);
        PersistenceUtil.writeImageFromUrl(ma.getDownloadUrl(), local);
        ma.setLocal(true);
        ma.setLocalFile(local.getAbsolutePath());
        ma.setDisplayUrl(makeLocalMediaUrl(local.getAbsolutePath()));
        return ma;
    }

    public boolean deleteFanart(GWTMediaArt art) {
        File f = new File(art.getLocalFile());
        if (f.exists()) {
            log.info("Removing Fanart Image: " + f);
            return f.delete();
        }
        return false;
    }

    public void makeDefaultFanart(GWTMediaFile file, MediaArtifactType type, GWTMediaArt art) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sageMF == null) {
            log.error("Failed to get sage mediafile for: " + file);
            return;
        }
        
        File img=new File(art.getLocalFile());
        if (img.exists()) {
            if (type == MediaArtifactType.POSTER) {
                phoenix.api.SetFanartPoster(sageMF, img);
            } else if (type==MediaArtifactType.BACKGROUND) {
                phoenix.api.SetFanartBackground(sageMF, img);
            } else if (type==MediaArtifactType.BANNER) {
                phoenix.api.SetFanartBanner(sageMF, img);
            }
        }
    }
}
