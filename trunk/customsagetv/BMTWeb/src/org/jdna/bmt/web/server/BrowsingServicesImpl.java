package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTAiringDetails;
import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.media.GWTView;
import org.jdna.bmt.web.client.media.GWTViewCategories;
import org.jdna.bmt.web.client.ui.browser.BrowsingService;
import org.jdna.bmt.web.client.ui.browser.PersistenceOptionsUI;
import org.jdna.bmt.web.client.ui.browser.ProgressStatus;
import org.jdna.bmt.web.client.ui.browser.SearchQueryOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.bmt.web.client.util.NamedProperty;

import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.db.ParseException;
import sagex.phoenix.fanart.AdvancedFanartMediaRequestHandler;
import sagex.phoenix.metadata.BatchUpdateVisitor;
import sagex.phoenix.metadata.CastMember;
import sagex.phoenix.metadata.ICastMember;
import sagex.phoenix.metadata.IMediaArt;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.metadata.IMetadataProvider;
import sagex.phoenix.metadata.IMetadataSearchResult;
import sagex.phoenix.metadata.ISageCustomMetadataRW;
import sagex.phoenix.metadata.ISagePropertyRW;
import sagex.phoenix.metadata.ISeriesInfo;
import sagex.phoenix.metadata.MediaArt;
import sagex.phoenix.metadata.MediaArtifactType;
import sagex.phoenix.metadata.MediaType;
import sagex.phoenix.metadata.MetadataConfiguration;
import sagex.phoenix.metadata.MetadataException;
import sagex.phoenix.metadata.MetadataHints;
import sagex.phoenix.metadata.MetadataUtil;
import sagex.phoenix.metadata.PhoenixMetadataSupport;
import sagex.phoenix.metadata.persistence.PersistenceUtil;
import sagex.phoenix.metadata.provider.tvdb.TVDBMetadataProvider;
import sagex.phoenix.metadata.proxy.MetadataProxy;
import sagex.phoenix.metadata.search.FileMatcher;
import sagex.phoenix.metadata.search.ID;
import sagex.phoenix.metadata.search.MediaSearchResult;
import sagex.phoenix.metadata.search.MetadataSearchUtil;
import sagex.phoenix.metadata.search.SearchQuery;
import sagex.phoenix.metadata.search.SearchQuery.Field;
import sagex.phoenix.progress.NullProgressMonitor;
import sagex.phoenix.progress.TrackedItem;
import sagex.phoenix.util.DateUtils;
import sagex.phoenix.util.Hints;
import sagex.phoenix.util.url.UrlUtil;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.sage.SageMediaFile;
import sagex.phoenix.vfs.util.PathUtils;
import sagex.phoenix.vfs.views.ViewFactory;
import sagex.phoenix.vfs.views.ViewFolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BrowsingServicesImpl extends RemoteServiceServlet implements BrowsingService {
	private static final Logger log = Logger.getLogger(BrowsingServicesImpl.class);

	public GWTMediaResource[] browseChildren(GWTMediaFolder folder, int start, int pageSize) {
		log.info("Browsing Folder: " + folder.getTitle() + "; Start: " + start + "; Size: " + pageSize);
		IMediaFolder vfsFolder = null;
		vfsFolder = getFolderRef(folder);
		List<GWTMediaResource> files = new ArrayList<GWTMediaResource>();
		if (vfsFolder != null) {
			List<IMediaResource> res = vfsFolder.getChildren();
			if (res != null && res.size() >= 0 && res.size() > start) {
				int size = Math.min(res.size(), start + pageSize);
				for (int i = start; i < size; i++) {
					files.add(convertResource(res.get(i)));
				}
			}
		}

		return files.toArray(new GWTMediaResource[files.size()]);
	}

	protected GWTMediaResource convertResource(IMediaResource r) {
		return convertResource(r, getThreadLocalRequest());
	}

	public static GWTMediaResource convertResource(IMediaResource r, HttpServletRequest req) {
		if (r instanceof IMediaFolder) {
			GWTMediaFolder folder = new GWTMediaFolder(null, phoenix.media.GetTitle(r), ((IMediaFolder) r).getChildren().size());
			folder.setResourceRef(String.valueOf(r.hashCode()));
			setFolderRef((IMediaFolder) r, req);
			folder.setPath(PathUtils.getLocation(r));
			
			folder.setMinorTitle(((IMediaFolder) r).getChildren().size() + " items");

			int maxItems = 5;
			try {
				maxItems = NumberUtils.toInt(phoenix.config.GetProperty("bmt/web/maxItemsPerView"),30);
			} catch (Exception e) {
				e.printStackTrace();
			}

			folder.setPageSize(maxItems);

			if (r instanceof ViewFolder) {
				List<String> hints = ((ViewFolder)r).getPresentation().getHints();
				folder.getHints().addAll(hints);
			}
			
			try {
				ViewFolder par = (ViewFolder) r.getParent();
				
				if (par!=null && par.hasHint("series")) {
					Map<String,String> args= new HashMap<String, String>();
					args.put(AdvancedFanartMediaRequestHandler.PARAM_MEDIATYPE, MediaType.TV.name());
					args.put(AdvancedFanartMediaRequestHandler.PARAM_TITLE, folder.getTitle());
					args.put(AdvancedFanartMediaRequestHandler.PARAM_ARTIFACTTYPE, MediaArtifactType.POSTER.name());
					String trans = (String) phoenix.config.GetProperty("bmt/web/itemThumbnailTransform");
					if (!StringUtils.isEmpty(trans)) {
						args.put(AdvancedFanartMediaRequestHandler.PARAM_TRANS_TRANSFORM, trans);
						args.put(AdvancedFanartMediaRequestHandler.PARAM_TRANS_TAG, "bmtfanart");
					}
					String url = UrlUtil.buildURL("fanart", args); 
					
					folder.setThumbnailUrl(url);
				}
			} catch (Exception e) {
				log.warn("Failed to calculate folder icon.", e);
			}

			return folder;
		} else {
			GWTMediaFile file = new GWTMediaFile(null, r.getTitle());
			if (r instanceof IMediaFile) {
				if (r.isType(MediaResourceType.TV.value())) {
					IMetadata md = ((IMediaFile) r).getMetadata();
					String minTitle = (md.getEpisodeName());
					if (md.getSeasonNumber()>0) {
						minTitle += (" (s"+md.getSeasonNumber() + " e" + md.getEpisodeNumber() + ")");
					}
					file.setMinorTitle(minTitle);
				}
				Object sageMedia = r.getMediaObject();
				if (sageMedia != null) {
					int mfid = MediaFileAPI.GetMediaFileID(sageMedia);
					int arid = AiringAPI.GetAiringID(sageMedia);
					Map<String,String> args= new HashMap<String, String>();
					if (mfid>0) {
						args.put(AdvancedFanartMediaRequestHandler.PARAM_MEDIAFILE, String.valueOf(mfid));
					} else if (arid>0) {
						args.put(AdvancedFanartMediaRequestHandler.PARAM_MEDIAFILE, String.valueOf(arid));
					} else {
						if (MetadataUtil.isRecordedMovie((IMediaFile) r)) {
							args.put(AdvancedFanartMediaRequestHandler.PARAM_MEDIATYPE, MediaType.MOVIE.name());
						} else {
							args.put(AdvancedFanartMediaRequestHandler.PARAM_MEDIATYPE, MediaType.TV.name());
						}
						args.put(AdvancedFanartMediaRequestHandler.PARAM_TITLE, r.getTitle());
					}
					args.put(AdvancedFanartMediaRequestHandler.PARAM_ARTIFACTTYPE, MediaArtifactType.POSTER.name());

					String trans = (String) phoenix.config.GetProperty("bmt/web/itemThumbnailTransform");
					if (!StringUtils.isEmpty(trans)) {
						args.put(AdvancedFanartMediaRequestHandler.PARAM_TRANS_TRANSFORM, trans);
						args.put(AdvancedFanartMediaRequestHandler.PARAM_TRANS_TAG, "bmtfanart");
					}
					
					file.setThumbnailUrl(UrlUtil.buildURL("fanart", args));
					file.setSageMediaFileId(mfid);
					file.setAiringId(String.valueOf(arid));
					file.setShowId(ShowAPI.GetShowExternalID(sageMedia));
					File f = new File(phoenix.fanart.GetFanartBackgroundPath(sageMedia));
					f = f.getParentFile();
					file.setFanartDir(f.getAbsolutePath());
					file.getSageRecording().set(MediaFileAPI.IsTVFile(sageMedia));
					file.getIsLibraryFile().set(MediaFileAPI.IsLibraryFile(sageMedia));
					file.getIsWatched().set(AiringAPI.IsWatched(sageMedia));
					file.setDuration(AiringAPI.GetAiringDuration(sageMedia));
					ISeriesInfo info = phoenix.media.GetSeriesInfo((IMediaFile)r);
					if (info!=null) {
						file.setSeriesInfoId(info.getSeriesInfoID());
					}
					file.setVFSID(r.getId());
					file.setPlayable(! (r.isType(MediaResourceType.EPG_AIRING.value()) || r.isType(MediaResourceType.FOLDER.value())));
					
					File realfile = PathUtils.getFirstFile((IMediaFile) r);
					if (realfile!=null) {
						file.setSize(realfile.length());
					}
				} else {
					log.warn("Not a sage media object??");
				}
				log.debug("Setting Last Modified: " + file + "; " + r.lastModified());
				file.setLastModified(r.lastModified());
				file.setFormattedTitle(phoenix.media.GetFormattedTitle(r));
				
				// if an epg airing, then set the airing details
				if (r.isType(MediaResourceType.EPG_AIRING.value())) {
					Object airing = r.getMediaObject();
					Object chan = AiringAPI.GetChannel(airing);
					GWTAiringDetails det = new GWTAiringDetails();
					det.setChannel(ChannelAPI.GetChannelNumber(chan));
					det.setNetwork(ChannelAPI.GetChannelName(chan));
					det.setDuration(AiringAPI.GetAiringDuration(airing));
					det.setFirtRun(ShowAPI.IsShowFirstRun(airing));
					det.setStartTime(AiringAPI.GetAiringStartTime(airing));
					det.setManualRecord(AiringAPI.IsManualRecord(airing));
					file.setAiringDetails(det);
					IMetadata md = ((IMediaFile)r).getMetadata();
					det.setYear(md.getYear());
					det.setSeason(md.getSeasonNumber());
					det.setEpisode(md.getEpisodeNumber());
				}
			}
			file.setPath(PathUtils.getLocation(r));
			return file;
		}
	}

	private Map<String, IMediaFolder> getFolderRefs() {
		return getFolderRefs(getThreadLocalRequest());
	}

	private IMediaFolder getFolderRef(GWTMediaFolder folder) {
		return getFolderRefs().get(folder.getResourceRef());
	}

	public static Map<String, IMediaFolder> getFolderRefs(HttpServletRequest req) {
		Map<String, IMediaFolder> refs = (Map<String, IMediaFolder>) req.getSession().getAttribute("folderRefs");
		if (refs == null) {
			refs = new HashMap<String, IMediaFolder>();
			req.getSession().setAttribute("folderRefs", refs);
		}
		return refs;
	}

	public static IMediaFolder getFolderRef(GWTMediaFolder folder, HttpServletRequest req) {
		return getFolderRefs(req).get(folder.getResourceRef());
	}

	private void setFolderRef(IMediaFolder folder) {
		getFolderRefs().put(String.valueOf(folder.hashCode()), folder);
	}

	public static void setFolderRef(IMediaFolder folder, HttpServletRequest req) {
		getFolderRefs(req).put(String.valueOf(folder.hashCode()), folder);
	}

	private transient PhoenixMetadataSupport support = new PhoenixMetadataSupport();

	public List<GWTProviderInfo> getProviders() {
		MetadataConfiguration cfg = GroupProxy.get(MetadataConfiguration.class);
		
		Set<String> tvs = new TreeSet<String>();
		Set<String> movies = new TreeSet<String>();
		String tv = cfg.getTVProviders();
		String movie = cfg.getMovieProviders();
		if (!StringUtils.isEmpty(tv)) {
			String provs[] = tv.split(",");
			for (String s: provs) {
				if (s.trim().length()>0) {
					tvs.add(s.trim());
				}
			}
		}
		if (!StringUtils.isEmpty(movie)) {
			String provs[] = movie.split(",");
			for (String s: provs) {
				if (s.trim().length()>0) {
					movies.add(s.trim());
				}
			}
		}
		
		List<IMetadataProvider> providers = Phoenix.getInstance().getMetadataManager().getProviders();
		List<GWTProviderInfo> info = new ArrayList<GWTProviderInfo>();
		for (IMetadataProvider p : providers) {
			GWTProviderInfo pinfo = new GWTProviderInfo(p.getInfo());
			if (tvs.contains(p.getInfo().getId())) {
				pinfo.setUserDefault(true);
			}
			if (movies.contains(p.getInfo().getId())) {
				pinfo.setUserDefault(true);
			}
			info.add(pinfo);
		}
		return info;
	}

	public GWTMediaMetadata loadMetadata(GWTMediaFile mediaFile) {
		try {
			log.debug("Fetching Current Metadata for Item: " + mediaFile.getSageMediaFileId() + "; " + mediaFile.getTitle());

			Object file;
			if (mediaFile.getSageMediaFileId()==0) {
				file = AiringAPI.GetAiringForID(NumberUtils.toInt(mediaFile.getAiringId()));
			} else {
				file = MediaFileAPI.GetMediaFileForID(mediaFile.getSageMediaFileId());
			}
			if (file==null) {
				throw new Exception("Failed to get MediaFile for: " + mediaFile.getSageMediaFileId());
			}
			IMediaFile mf = new SageMediaFile(null, file);
			if (mf == null)
				throw new Exception("Invalid Media File: " + mediaFile);

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

	public static GWTMediaMetadata newMetadata(IMediaFile file, IMetadata source) {
		GWTMediaMetadata mi = new GWTMediaMetadata();
		if (source != null) {
			mi.getDescription().set(source.getDescription());
			mi.getDiscNumber().set(String.valueOf(source.getDiscNumber()));
			mi.getEpisodeName().set(source.getEpisodeName());
			mi.getEpisodeNumber().set(String.valueOf(source.getEpisodeNumber()));
			mi.getExtendedRatings().set(source.getExtendedRatings());

			// do not set an empty external id
			if (!StringUtils.isEmpty(source.getExternalID())) {
				mi.getExternalID().set(source.getExternalID());
			} else {
				log.warn("ExternalID was empty. Re-populating the ExternalID.  This is most likely because the user has ExternalID in their list of custom_metadata_fields.");
				if (file != null && file.getMetadata() != null) {
					Object smf = phoenix.media.GetSageMediaFile(file);
					mi.getExternalID().set(ShowAPI.GetShowExternalID(smf));
				}
			}
			mi.getIMDBID().set(source.getIMDBID());
			mi.getMediaProviderDataID().set(source.getMediaProviderDataID());
			mi.getMediaProviderID().set(source.getMediaProviderID());
			mi.getMediaTitle().set(source.getMediaTitle());
			mi.getMediaType().set(source.getMediaType());
			mi.getMisc().set(source.getMisc());
			if (source.getOriginalAirDate()!=null) {
				mi.getOriginalAirDate().set(DateUtils.formatDateTime(source.getOriginalAirDate().getTime()));
			}
			mi.getParentalRating().set(source.getParentalRating());
			mi.getRated().set(source.getRated());
			if (source.getRunningTime()>0) {
				mi.getRunningTime().set(String.valueOf(source.getRunningTime()));
			} else {
				long dur = AiringAPI.GetAiringDuration(file.getMediaObject());
				mi.getRunningTime().set(String.valueOf(dur));
			}
			mi.getSeasonNumber().set(String.valueOf(source.getSeasonNumber()));
			mi.getTitle().set(source.getRelativePathWithTitle());
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

			String g = StringUtils.join(source.getGenres(), "/");
			if (!StringUtils.isEmpty(g)) {
				mi.getGenres().set(g);
			}

			for (IMediaArt ma : source.getFanart()) {
				mi.getFanart().add(new GWTMediaArt(ma));
			}
		}

		if (file != null) {
			// depending on if the file is a Recording or Not, some fields are
			// readonly
			MetadataConfiguration config = GroupProxy.get(MetadataConfiguration.class);
			mi.getPreserveRecordingMetadata().set(config.getPreserverRecordingMetadata() && file.isType(MediaResourceType.RECORDING.value()) && !MetadataUtil.isImportedRecording(file));
		}

		return mi;
	}

	public List<GWTMediaSearchResult> searchForMetadata(GWTMediaFile item, SearchQueryOptions options) {
		List<GWTMediaSearchResult> results = new ArrayList<GWTMediaSearchResult>();
		try {
			MediaType type = MediaType.toMediaType(options.getType().get());

			// build a custom query from the options
			SearchQuery query = new SearchQuery(Phoenix.getInstance().getMetadataManager().getDefaultMetadataOptions());
			query.setMediaType(type);
			query.set(Field.QUERY, options.getSearchTitle().get());
			query.set(Field.YEAR, options.getYear().get());
			if (type == MediaType.TV) {
				query.set(Field.EPISODE_TITLE, options.getEpisodeTitle().get());
				query.set(Field.EPISODE, options.getEpisode().get());
				query.set(Field.SEASON, options.getSeason().get());
				query.set(Field.EPISODE_DATE, options.getAiredDate().get());
			}

			List<IMetadataSearchResult> sresults = Phoenix.getInstance().getMetadataManager().search(options.getProvider().get(), query);
			if (sresults == null)
				throw new Exception("Search Failed for: " + query);
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
			MediaSearchResult res = new MediaSearchResult(result);
			return newMetadata(phoenix.media.GetMediaFile(phoenix.media.GetSageMediaFile(result.getMediaFileId())), Phoenix.getInstance().getMetadataManager()
					.getMetdata(res));
		} catch (Exception e) {
			log.error("Metadata Retreival Failed!", e);
			throw new RuntimeException(e);
		}
	}

	public String scan(final GWTMediaFolder folder, final PersistenceOptionsUI options) {

		try {
			IMediaFolder realFolder = BrowsingServicesImpl.getFolderRef(folder, getThreadLocalRequest());
			Hints opts = Phoenix.getInstance().getMetadataManager().getDefaultMetadataOptions();
			opts.setBooleanHint(MetadataHints.SCAN_SUBFOLDERS, options.getIncludeSubDirs().get());
			opts.setBooleanHint(MetadataHints.UPDATE_FANART, options.getUpdateFanart().get());
			opts.setBooleanHint(MetadataHints.UPDATE_METADATA, options.getUpdateMetadata().get());
			opts.setBooleanHint(MetadataHints.SCAN_MISSING_METADATA_ONLY, options.getScanOnlyMissingMetadata().get());
			opts.setBooleanHint(MetadataHints.IMPORT_TV_AS_RECORDING, options.getImportTVAsRecordings().get());
			opts.setBooleanHint(MetadataHints.REFRESH, options.getRefresh().get());
			
			log.info("Scanning Folder: " + folder + " with options " + opts);
			
			return (String) support.startMetadataScan(realFolder, opts);
		} catch (Exception e) {
			log.warn("Failed to start metadata scan", e);
			throw new RuntimeException("Metadata Scan Failed: " + e.getMessage());
		}
	}

	public ProgressStatus[] getScansInProgress() {
		Object scans[] = support.getTrackers();

		List<ProgressStatus> all = new ArrayList<ProgressStatus>();
		for (Object id : scans) {
			all.add(getStatus((String) id));
		}
		return all.toArray(new ProgressStatus[] {});
	}

	public ProgressStatus getStatus(String id) {
		log.debug("Getting Progress Status for: " + id);
		ProgressStatus status = new ProgressStatus();
		status.setProgressId(id);


		if (id == null) {
			status.setIsDone(true);
		} else {
			status.setComplete(support.getMetadataScanComplete(id));
			status.setIsCancelled(support.isMetadataScanCancelled(id));
			status.setIsDone(!support.isMetadataScanRunning(id));
			status.setStatus(support.getMetadataScanStatus(id));
			status.setTotalWork(support.getTotalWork(id));
			status.setWorked(support.getWorked(id));
			status.setSuccessCount(support.getSuccessCount(id));
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
			if (i.getMessage() != null) {
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
		Hints options = Phoenix.getInstance().getMetadataManager().getDefaultMetadataOptions();
		// don't worry import as recordings here, since we'll import it
		// at the end of the logic section
		options.setBooleanHint(MetadataHints.IMPORT_TV_AS_RECORDING,false);
		options.setBooleanHint(MetadataHints.SCAN_MISSING_METADATA_ONLY, false);
		options.setBooleanHint(MetadataHints.SCAN_SUBFOLDERS, false);
		options.setBooleanHint(MetadataHints.PRESERVE_ORIGINAL_METADATA, file.getMetadata().getPreserveRecordingMetadata().get());
		
		Object sageMF = getSageMediaFile(file);
		if (sageMF==null) {
			log.warn("Failed to save metadata/fanart since file was not found by it's id or airingid");
			return new ServiceReply<GWTMediaFile>(2, "Failed to save metadata/fanart because file could not be located", file); 
		}
		IMediaFile mf = new SageMediaFile(null, sageMF);
		IMetadata md = MetadataProxy.newInstance();
		GWTMediaMetadata gmd = file.getMetadata();

		md.setDescription(gmd.getDescription().get());
		md.setDiscNumber(NumberUtils.toInt(gmd.getDiscNumber().get()));
		md.setEpisodeName(gmd.getEpisodeName().get());
		md.setEpisodeNumber(NumberUtils.toInt(gmd.getEpisodeNumber().get()));
		md.setExtendedRatings(gmd.getExtendedRatings().get());
		
		if (StringUtils.isEmpty(gmd.getExternalID().get())) {
			log.warn("ExternalID was empty: Using ExternalID from the Show.");
			md.setExternalID(ShowAPI.GetShowExternalID(sageMF));
		} else {
			md.setExternalID(gmd.getExternalID().get());
		}
		
		md.setIMDBID(gmd.getIMDBID().get());
		md.setMediaProviderDataID(gmd.getMediaProviderDataID().get());
		md.setMediaProviderID(gmd.getMediaProviderID().get());
		md.setMediaTitle(gmd.getMediaTitle().get());
		md.setMediaType(gmd.getMediaType().get());
		md.setMisc(gmd.getMisc().get());
		md.setOriginalAirDate(DateUtils.parseDate(gmd.getOriginalAirDate().get()));
		md.setRated(gmd.getRated().get());
		md.setParentalRating(gmd.getParentalRating().get());
		md.setRunningTime(DateUtils.parseRuntimeInMinutes(gmd.getRunningTime().get()));
		md.setSeasonNumber(NumberUtils.toInt(gmd.getSeasonNumber().get()));
		md.setRelativePathWithTitle(gmd.getTitle().get());
		md.setUserRating(MetadataSearchUtil.parseUserRating(gmd.getUserRating().get()));
		md.setYear(NumberUtils.toInt(gmd.getYear().get()));

		for (ICastMember cm : gmd.getActors()) {
			md.getActors().add(new CastMember(cm));
		}

		for (ICastMember cm : gmd.getDirectors()) {
			md.getDirectors().add(new CastMember(cm));
		}

		for (ICastMember cm : gmd.getGuests()) {
			md.getGuests().add(new CastMember(cm));
		}

		for (ICastMember cm : gmd.getWriters()) {
			md.getWriters().add(new CastMember(cm));
		}

		String genres = gmd.getGenres().get();
		if (!StringUtils.isEmpty(genres)) {
			md.getGenres().clear();
			String gens[] = genres.split("\\s*[/;,]\\s*");
			for (String g: gens ) {
				md.getGenres().add(g);
			}
		} else {
			md.getGenres().clear();
		}

		for (IMediaArt ma : gmd.getFanart()) {
			md.getFanart().add(new MediaArt(ma));
		}

		try {
			Phoenix.getInstance().getMetadataManager().updateMetadata(mf, md, options);

			// test if we are making a recording
			if (mf.isType(MediaResourceType.RECORDING.value()) && file.getSageRecording().get() == false) {
				IMediaFile mf2 = Phoenix.getInstance().getMetadataManager().unimportMediaFileAsRecording(mf);
				if (mf2 == null) {
					throw new MetadataException("Failed to unimport mediafile");
				}

				// reassign our local variables
				mf = mf2;
				file = (GWTMediaFile) convertResource(mf2);
			} else if (!mf.isType(MediaResourceType.RECORDING.value()) && file.getSageRecording().get()) {
				if (!Phoenix.getInstance().getMetadataManager().importMediaFileAsRecording(mf)) {
					throw new MetadataException("Unable to import as recording");
				}
			}

			// update the archive flag for tv.
			if (mf.isWatched() != file.getIsWatched().get()) {
				mf.setWatched(file.getIsWatched().get());
			}

			if (mf.isLibraryFile() != file.getIsLibraryFile().get()) {
				mf.setLibraryFile(file.getIsLibraryFile().get());
			}

			// update the formatted title
			file.setFormattedTitle(phoenix.media.GetFormattedTitle(mf));
			file.attachMetadata(newMetadata(mf, mf.getMetadata()));
		} catch (MetadataException e) {
			log.warn("Failed to save metadata for file: " + mf, e);
			return new ServiceReply<GWTMediaFile>(1, "Failed to save metadata/fanart: " + e.getMessage(), file);
		}

		return new ServiceReply<GWTMediaFile>(0, "ok", file);
	}

	public ArrayList<GWTMediaArt> getFanart(GWTMediaFile file, MediaArtifactType artifact) {
		Object sageMF = getSageMediaFile(file);
		if (sageMF == null) {
			log.error("Failed to get sage mediafile for: " + file);
			return null;
		}
		ArrayList<GWTMediaArt> files = new ArrayList<GWTMediaArt>();
		log.debug("Getting fanart: " + file.getType() + "; " + artifact + "; " + sageMF);
		String fanart[] = phoenix.fanart.GetFanartArtifacts(sageMF, null, null, artifact.name(), null, null);
		if (fanart != null) {
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

	public ServiceReply<GWTMediaArt> downloadFanart(GWTMediaFile file, MediaArtifactType artifact, GWTMediaArt ma) {
		Object sageMF = getSageMediaFile(file);
		if (sageMF == null) {
			return new ServiceReply<GWTMediaArt>(1, "Unknown media file");
		}
		
		String fanartDir = phoenix.fanart.GetFanartArtifactDir(sageMF, null, null, artifact.name(), null, null, true);
		log.debug("Fanart dir is " + fanartDir);
		File dir = new File(fanartDir);
		String name = new File(ma.getDownloadUrl()).getName();
		File local = new File(dir, name);
		log.debug("Downloaded file will be " + local);
		PersistenceUtil.writeImageFromUrl(ma.getDownloadUrl(), local);
		
		if (!local.exists()) {
			return new ServiceReply<GWTMediaArt>(2, "Failed to download image from url " + ma.getDownloadUrl());
		}
		
		if (!local.exists() || local.length()==0) {
			local.delete();
			return new ServiceReply(3, "Downloaded image was empty");
		}
		
		try {
			ImageIO.read(local);
		} catch (Exception e) {
			throw new RuntimeException("Downloaded Image was corrupt");
		}
		
		ma.setLocal(true);
		ma.setLocalFile(local.getAbsolutePath());
		ma.setDisplayUrl(makeLocalMediaUrl(local.getAbsolutePath()));
		// clear caches so that ui can be updated
		phoenix.fanart.ClearMemoryCaches();
		return new ServiceReply(ma);
	}

	public boolean deleteFanart(GWTMediaArt art) {
		File f = new File(art.getLocalFile());
		if (f.exists()) {
			log.info("Removing Fanart Image: " + f);
			boolean deleted = f.delete();
			if (deleted) {
				phoenix.fanart.ClearMemoryCaches();
			}
			return deleted;
		}
		return false;
	}

	public void makeDefaultFanart(GWTMediaFile file, MediaArtifactType type, GWTMediaArt art) {
		Object sageMF = getSageMediaFile(file);
		if (sageMF == null) {
			log.error("Failed to get sage mediafile for: " + file);
			return;
		}

		File img = new File(art.getLocalFile());
		if (img.exists()) {
			if (type == MediaArtifactType.POSTER) {
				phoenix.fanart.SetFanartPoster(sageMF, img);
			} else if (type == MediaArtifactType.BACKGROUND) {
				phoenix.fanart.SetFanartBackground(sageMF, img);
			} else if (type == MediaArtifactType.BANNER) {
				phoenix.fanart.SetFanartBanner(sageMF, img);
			}
			phoenix.fanart.ClearMemoryCaches();
		}
	}

	@Override
	public ServiceReply<GWTMediaFolder> searchMediaFiles(String search) {
		if (search==null) return new ServiceReply<GWTMediaFolder>(1, "No Search", null);
		
		if (search.split(" ").length<3) {
			// assume it's a title search
			search = "Title = '" + search + "'";
		}
		
		try {
			return new ServiceReply<GWTMediaFolder>(0,"ok",(GWTMediaFolder) convertResource(phoenix.umb.SearchMediaFiles(search)));
		} catch (Throwable t) {
			log.warn("Query Failed for " + search, t);
			if (t.getCause()!=null && t.getCause() instanceof ParseException) {
				return new ServiceReply<GWTMediaFolder>(2, t.getCause().getMessage(), null);
			} else {
				return new ServiceReply<GWTMediaFolder>(4, "Unknown Error", null);
			}
		}
	}

	private Object getSageMediaFile(GWTMediaFile file) {
		Object sageMF = phoenix.media.GetSageMediaFile(file.getSageMediaFileId());
		if (sageMF==null && !StringUtils.isEmpty(file.getAiringId())) {
			sageMF = AiringAPI.GetAiringForID(NumberUtils.toInt(file.getAiringId()));
		}
		return sageMF;
	}
	
	private IMediaFile getMediaRef(GWTMediaFile file) {
		Object sageMF = getSageMediaFile(file);
		IMediaFile mf = new SageMediaFile(null, sageMF);
		return mf;
	}
	
	private IMediaResource getResourceRef(GWTMediaResource res) {
		if (res instanceof GWTMediaFolder) {
			return getFolderRef((GWTMediaFolder) res);
		} else {
			return getMediaRef((GWTMediaFile) res);
		}
	}
	
	@Override
	public SearchQueryOptions discoverQueryOptions(GWTMediaFile file) {
		try {
			Object sageMF = getSageMediaFile(file);
			IMediaFile mf = phoenix.media.GetMediaFile(sageMF);
			SearchQuery query = Phoenix.getInstance().getSearchQueryFactory().createSageFriendlyQuery(mf, Phoenix.getInstance().getMetadataManager().getDefaultMetadataOptions());

			SearchQueryOptions options = new SearchQueryOptions();
			String title = query.get(Field.CLEAN_TITLE);
			options.getSearchTitle().set(title);
			options.getYear().set(query.get(Field.YEAR));
			options.getType().set(query.getMediaType().sageValue());

			options.getEpisodeTitle().set(query.get(Field.EPISODE_TITLE));
			options.getEpisode().set(query.get(Field.EPISODE));
			options.getSeason().set(query.get(Field.SEASON));
			options.getAiredDate().set(query.get(Field.EPISODE_DATE));
			
			if (query.getMediaType() == MediaType.TV) {
				// force tvdb
				options.getProvider().set(TVDBMetadataProvider.ID);
			}
			
			return options;
		} catch (Throwable t) {
			log.warn("discoverQueryOptions failed.", t);
			throw new RuntimeException(t.getMessage());
		}
	}

	@Override
	public ArrayList<GWTView> getViewCategories() {
		Set<String> tags = phoenix.umb.GetTags(false);
		ArrayList<GWTView> viewTags = new ArrayList<GWTView>();
		for (String s: tags) {
			viewTags.add(new GWTView(s, phoenix.umb.GetTagLabel(s)));
		}
		return viewTags;
	}

	@Override
	public GWTViewCategories getViews(String tag) {
		GWTViewCategories cats = null;
		

		Collection<ViewFactory> factories = null;
		if (tag==null || "all".equals(tag)) {
			cats = new GWTViewCategories(null, "All Views");
			factories = phoenix.umb.GetVisibleViews();
		} else if ("hidden".equals(tag)) {
			cats = new GWTViewCategories(tag, "Hidden Views");
			factories = phoenix.umb.GetHiddenViews();
		} else {
			cats = new GWTViewCategories(tag, phoenix.umb.GetTagLabel(tag));
			factories = phoenix.umb.GetViewFactories(tag);
		}
		
		for (ViewFactory f : factories) {
			cats.getViews().add(new GWTView(f.getName(), f.getLabel(), phoenix.umb.IsVisible(f)));
		}

		Collections.sort(cats.getViews(), new Comparator<GWTView>() {
			public int compare(GWTView o1, GWTView o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});

		return cats;
	}

	@Override
	public GWTMediaFolder getView(GWTView view) {
		log.info("Getting Folder for source: " + view.getId());

		try {
				ViewFactory factory = null;

				factory = Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactory(view.getId());
				if (factory ==null) {
					throw new Exception("No Factory for: " + view.getId());
				}
				
				IMediaFolder f = factory.create(null);
				if (f==null) {
					throw new Exception("Failed to create folder for " + view.getId());
				}
					
				setFolderRef(f);
				log.debug("Returning newly created folder: " + f.getTitle() + "; Size: " + f.getChildren().size());
				return (GWTMediaFolder) convertResource(f);
		} catch (Throwable t) {
			log.warn("Failed to get folder for source: " + view.getId(), t);
			throw new RuntimeException(t);
		}
	}

	@Override
	public String record(GWTMediaFile file) {
		String airingId = file.getAiringId();
		Object sageAiring = AiringAPI.GetAiringForID(NumberUtils.toInt(airingId));
		if (sageAiring==null) {
			return "Failed to record Airing because the Airing id was not valid " + airingId;
		}
		
		Object result = AiringAPI.Record(sageAiring);
		if (result instanceof String) {
			return (String)result;
		}
		
		if (result instanceof Boolean) {
			boolean b = (Boolean) result;
			if (b) {
				return "Recording was scheduled";
			} else {
				return "Recording was unable to be scheduled";
			}
		}
		
		return "Sage Message: " + result;
	}

	@Override
	public void setWatched(GWTMediaFile file, boolean watched) {
		Object sageAiring = AiringAPI.GetAiringForID(NumberUtils.toInt(file.getAiringId()));
		if (watched) {
			AiringAPI.SetWatched(sageAiring);
		} else {
			AiringAPI.ClearWatched(sageAiring);
		}
	}

	@Override
	public void cancelRecord(GWTMediaFile file) {
		AiringAPI.CancelRecord(AiringAPI.GetAiringForID(NumberUtils.toInt(file.getAiringId())));
	}

	@Override
	public ArrayList<NamedProperty<String>> getEditableMetadataFields(GWTMediaResource res) {
		ArrayList<String> keyset = new ArrayList<String>();
		
		// add in non-updateable files
		keyset.add("ExternalID");
		
		keyset.add("ScrapedBy");
		keyset.add("ScrapedDate");
		
		ArrayList<NamedProperty<String>> props = new ArrayList<NamedProperty<String>>();
		String keys[] = MetadataUtil.getPropertyKeys(ISagePropertyRW.class);
		for (String s: keys) {
			if (!keyset.contains(s)) {
				props.add(new NamedProperty<String>(s, ""));
				keyset.add(s);
			}
		}
		
		keys = MetadataUtil.getPropertyKeys(ISageCustomMetadataRW.class);
		for (String s: keys) {
			if (!keyset.contains(s)) {
				props.add(new NamedProperty<String>(s, ""));
				keyset.add(s);
			}
		}
		
		Collections.sort(props, new Comparator<NamedProperty<String>>() {
			@Override
			public int compare(NamedProperty<String> o1, NamedProperty<String> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		// populate mediafile details
		if (res instanceof GWTMediaFile) {
			IMediaFile mf = getMediaRef((GWTMediaFile) res);
			IMetadata md = mf.getMetadata();
			for (NamedProperty<String> np: props) {
				np.set(MetadataUtil.getProperty(md, np.getName()));
			}
		}
		
		return props;
	}

	@Override
	public boolean batchUpdateMetadata(GWTMediaResource res, ArrayList<NamedProperty<String>> props) {
		IMediaResource resource = getResourceRef(res);
		if (resource==null) {
			log.warn("Invalid Reference: " + res);
			return false;
		}
		
		Map<String, String> update = new HashMap<String, String>();
		for (NamedProperty<String> p: props) {
			log.info("Batch Updating: " + p.getName() + " with value: " + p.get() + "; Clearing? " + p.isVisible());
			update.put(p.getName(), p.get());
		}
		
		log.debug("Begin Batch Updating...");
		resource.accept(new BatchUpdateVisitor(update), NullProgressMonitor.INSTANCE, IMediaResource.DEEP_UNLIMITED);
		log.debug("End Batch Updating");
		return true;
	}

	@Override
	public ServiceReply<Boolean> addMediaTitle(HashMap<String, String> fields) {
		ServiceReply<Boolean> reply = new ServiceReply<Boolean>();
		
		FileMatcher fm = new FileMatcher();
		fm.setMediaType(MediaType.toMediaType(fields.get("mediatype")));
		try {
			fm.setFileRegex(Pattern.compile(fields.get("regex")));
		} catch (Exception e) {
			reply.setData(false);
			reply.setCode(1);
			reply.setMessage("Invalid File Pattern: " + fields.get("regex"));
			return reply;
		}
		fm.setMetadata(new ID(fields.get("providerid"), fields.get("dataid")));
		
		try {
			Phoenix.getInstance().getMediaTitlesManager().addRegexMatcher(fm);
		} catch (Exception e) {
			reply.setData(false);
			reply.setCode(2);
			reply.setMessage("Failed to add matcher: " + e.getMessage());
			return reply;
		}
		
		try {
			Phoenix.getInstance().getMediaTitlesManager().saveMatchers();
		} catch (IOException e) {
			reply.setData(false);
			reply.setCode(3);
			reply.setMessage("Failed to save matchers: " + e.getMessage());
			return reply;
		}
		
		reply.setData(true);
		return reply;
	}

	@Override
	public ArrayList<String> loadFiles(String dir, String mask) {
		ArrayList<String> files = new ArrayList<String>();
		Collection<File> coll = (FileUtils.listFiles(new File(dir), mask.split(","), true));
		if (coll!=null&&coll.size()>0) {
			for (File f: coll) {
				files.add(f.getAbsolutePath());
			}
		}
		return files;
	}

	@Override
	public void toggleViewVisibility(GWTView view) {
		ViewFactory vf = Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactory(view.getId());
		if (vf==null) return;
		phoenix.umb.SetVisible(vf, !phoenix.umb.IsVisible(vf));
	}

	@Override
	public ArrayList<String> getFanartFiles(GWTMediaFile file) {
		ArrayList<String> files = new ArrayList<String>();
		IMediaFile mf = getMediaRef(file);
		if (mf==null) {
			log.warn("Invalid Mediafile: " + file);
			return files;
		}
		
		addFanartFiles(files, mf, MediaArtifactType.POSTER);
		addFanartFiles(files, mf, MediaArtifactType.BACKGROUND);
		addFanartFiles(files, mf, MediaArtifactType.BANNER);
		addFanartFiles(files, mf, MediaArtifactType.EPISODE);
		
		return files;
	}

	private void addFanartFiles(ArrayList<String> files, IMediaFile mf,	MediaArtifactType artifact) {
		String list[] = phoenix.fanart.GetFanartArtifacts(mf, null, null, artifact.name(), null, null);
		if (list!=null && list.length>0) {
			files.addAll(Arrays.asList(list));
		}
	}
}
