package org.jdna.bmt.web.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.media.GWTFactoryInfo;
import org.jdna.bmt.web.client.media.GWTFactoryInfo.SourceType;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.browser.BrowsingService;
import org.jdna.bmt.web.client.ui.browser.PersistenceOptionsUI;
import org.jdna.bmt.web.client.ui.browser.ProgressStatus;
import org.jdna.bmt.web.client.ui.browser.SearchQueryOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.metadata.CastMember;
import sagex.phoenix.metadata.ICastMember;
import sagex.phoenix.metadata.IMediaArt;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.metadata.IMetadataProvider;
import sagex.phoenix.metadata.IMetadataSearchResult;
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
import sagex.phoenix.metadata.search.MediaSearchResult;
import sagex.phoenix.metadata.search.MetadataSearchUtil;
import sagex.phoenix.metadata.search.SearchQuery;
import sagex.phoenix.metadata.search.SearchQuery.Field;
import sagex.phoenix.metadata.search.SearchQueryFactory;
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
			folder.setMinorTitle(folder.getPath());

			int maxItems = 5;
			try {
				maxItems = (Integer) phoenix.config.GetProperty("bmt/web/maxItemsPerView");
			} catch (Exception e) {
				e.printStackTrace();
			}

			folder.setPageSize(maxItems);

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
					int id = MediaFileAPI.GetMediaFileID(sageMedia);
					String url = "media/poster/" + id;

					String transform = (String) phoenix.config.GetProperty("bmt/web/itemThumbnailTransform");
					if (transform != null) {
						url += ("?transform=" + transform);
					}
					file.setThumbnailUrl(url);
					file.setSageMediaFileId(id);
					file.setAiringId(String.valueOf(AiringAPI.GetAiringID(sageMedia)));
					file.setShowId(ShowAPI.GetShowExternalID(sageMedia));
					File f = new File(phoenix.api.GetFanartBackgroundPath(sageMedia));
					f = f.getParentFile();
					file.setFanartDir(f.getAbsolutePath());
					file.getSageRecording().set(MediaFileAPI.IsTVFile(sageMedia));
					file.getIsLibraryFile().set(MediaFileAPI.IsLibraryFile(sageMedia));
					file.getIsWatched().set(AiringAPI.IsWatched(sageMedia));
					
					ISeriesInfo info = phoenix.media.GetSeriesInfo((IMediaFile)r);
					if (info!=null) {
						file.setSeriesInfoId(info.getSeriesInfoID());
					}
					file.setVFSID(r.getId());
				} else {
					log.warn("Not a sage media object??");
				}
				log.debug("Setting Last Modified: " + file + "; " + r.lastModified());
				file.setLastModified(r.lastModified());
				file.setFormattedTitle(phoenix.media.GetFormattedTitle(r));
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

	public GWTMediaFolder getFolderForSource(GWTFactoryInfo source, GWTMediaFolder folder) {
		log.info("Getting Folder for source: " + source);

		try {
			if (source.getSourceType() == SourceType.View) {
				ViewFactory factory = null;
				if (source.getSourceType() != SourceType.View) {
					throw new Exception("Not a valid View Source: " + source.getId());
				}
				
				factory = Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactory(source.getId());
				if (factory ==null) {
					throw new Exception("No Factory for: " + source.getId());
				}
				
				IMediaFolder f = factory.create(null);
				if (f==null) {
					throw new Exception("Failed to create folder for " + source.getId());
				}
					
				setFolderRef(f);
				log.debug("Returning newly created folder: " + f.getTitle() + "; Size: " + f.getChildren().size());
				return (GWTMediaFolder) convertResource(f);
			}
		} catch (Throwable t) {
			log.warn("Failed to get folder for source: " + source.getId(), t);
		}

		return null;
	}

	public GWTFactoryInfo[] getFactories(SourceType sourceType) {
		List<GWTFactoryInfo> sources = new ArrayList<GWTFactoryInfo>();

		if (sourceType == SourceType.View) {
			List<ViewFactory> factories = phoenix.umb.GetViewFactories();
			for (ViewFactory f : factories) {
				if (f.isVisible()) {
					GWTFactoryInfo s = new GWTFactoryInfo(sourceType, f.getName(), f.getLabel(), f.getDescription());
					sources.add(s);
				}
			}
		} else {
			log.warn("Invalid/unhandled source type:  " + sourceType);
		}

		Collections.sort(sources, new Comparator<GWTFactoryInfo>() {
			public int compare(GWTFactoryInfo o1, GWTFactoryInfo o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});

		return sources.toArray(new GWTFactoryInfo[sources.size()]);
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

	public GWTMediaMetadata newMetadata(IMediaFile file, IMetadata source) {
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
			mi.getOriginalAirDate().set(DateUtils.formatDate(source.getOriginalAirDate()));
			mi.getParentalRating().set(source.getParentalRating());
			mi.getRated().set(source.getRated());
			mi.getRunningTime().set(String.valueOf(source.getRunningTime()));
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

			String g = StringUtils.join(source.getGenres(), ",");
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
		// don't allow the persistence engine to do this, since we'll do it,
		// later
		options.setBooleanHint(MetadataHints.IMPORT_TV_AS_RECORDING,false);
		options.setBooleanHint(MetadataHints.SCAN_MISSING_METADATA_ONLY, false);
		options.setBooleanHint(MetadataHints.SCAN_SUBFOLDERS, false);

		
		Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
		IMediaFile mf = new SageMediaFile(null, sageMF);
		IMetadata md = MetadataProxy.newInstance();
		GWTMediaMetadata gmd = file.getMetadata();

		System.out.println("** EpisodeName: " + gmd.getEpisodeName().get());
		
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
		md.setRunningTime(NumberUtils.toLong(gmd.getRunningTime().get()));
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
			String gens[] = genres.split("\\s*,\\s*");
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
			System.out.println("** EpisodeName2: " + md.getEpisodeName());
			Phoenix.getInstance().getMetadataManager().updateMetadata(mf, md, null);
			System.out.println("** EpisodeName3: " + md.getEpisodeName());

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
			System.out.println("** EpisodeName3: " + file.getMetadata().getEpisodeName().get());
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

	public GWTMediaArt downloadFanart(GWTMediaFile file, MediaArtifactType artifact, GWTMediaArt ma) {
		Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
		if (sageMF == null) {
			log.error("Failed to get sage mediafile for: " + file);
			return null;
		}
		String fanartDir = phoenix.api.GetFanartArtifactDir(sageMF, null, null, artifact.name(), null, null, true);
		File dir = new File(fanartDir);
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

		File img = new File(art.getLocalFile());
		if (img.exists()) {
			if (type == MediaArtifactType.POSTER) {
				phoenix.api.SetFanartPoster(sageMF, img);
			} else if (type == MediaArtifactType.BACKGROUND) {
				phoenix.api.SetFanartBackground(sageMF, img);
			} else if (type == MediaArtifactType.BANNER) {
				phoenix.api.SetFanartBanner(sageMF, img);
			}
		}
	}

	@Override
	public GWTMediaFolder searchMediaFiles(String search) {
		return (GWTMediaFolder) convertResource(phoenix.umb.SearchMediaFiles(search));
	}

	@Override
	public SearchQueryOptions discoverQueryOptions(GWTMediaFile file) {
		try {
			Object sageMF = phoenix.media.GetSageMediaFile(file.getSageMediaFileId());
			IMediaFile mf = phoenix.media.GetMediaFile(sageMF);
			SearchQuery query = SearchQueryFactory.getInstance().createSageFriendlyQuery(mf, Phoenix.getInstance().getMetadataManager().getDefaultMetadataOptions());

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
}
