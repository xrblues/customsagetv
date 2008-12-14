package org.jdna.media.metadata.impl.composite;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.impl.sage.SageVideoMetaDataPersistence;

public class CompositeMetadataProvider implements IMediaMetadataProvider {
	private static final Logger log = Logger.getLogger(CompositeMetadataProvider.class);
	
	private IProviderInfo info;
	private String searcherProviderId;
	private String detailsProviderId;
	private CompositeMetadataConfiguration conf;
	private boolean mergeDetails = false;
	
	public CompositeMetadataProvider(CompositeMetadataConfiguration conf) {
		info =  new ProviderInfo(conf.getId(), conf.getName(), conf.getDescription(), conf.getIconUrl());
		this.searcherProviderId = conf.getSearchProviderId();
		this.detailsProviderId = conf.getDetailProviderId();
		this.conf=conf;
		this.mergeDetails = conf.getFieldsFromSearchProvider()!=null;
		log.debug("Composite Provider Created with id: " + getInfo().getId() +"; search: " + searcherProviderId + "; details: " + detailsProviderId);
	}
	
	public IProviderInfo getInfo() {
		return info;
	}

	public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
		return getMetaDataByIMDBId(result.getIMDBId());
	}

	private IMediaMetadata mergeDetails(IMediaMetadata primary, IMediaMetadata secondary) {
		MediaMetadata md = new MediaMetadata(primary);
		md.setProviderId(getInfo().getId());
		if (secondary==null) return md;
		
		String fields[] = conf.getFieldsFromSearchProvider().split(";");
		for (String f : fields) {
			if (SageVideoMetaDataPersistence.ACTOR.equals(f)) {
				md.setActors(secondary.getActors());
			} else if (SageVideoMetaDataPersistence.DESCRIPTION.equals(f)) {
				md.setPlot(secondary.getPlot());
			} else if (SageVideoMetaDataPersistence.DIRECTOR.equals(f)) {
				md.setDirectors(secondary.getDirectors());
			} else if (SageVideoMetaDataPersistence.GENRE.equals(f)) {
				md.setGenres(secondary.getGenres());
			} else if (SageVideoMetaDataPersistence.RATED.equals(f)) {
				md.setMPAARating(secondary.getMPAARating());
			} else if (SageVideoMetaDataPersistence.RUNNING_TIME.equals(f)) {
				md.setRuntime(secondary.getRuntime());
			} else if (SageVideoMetaDataPersistence.TITLE.equals(f)) {
				md.setTitle(secondary.getTitle());
			} else if (SageVideoMetaDataPersistence.WRITER.equals(f)) {
				md.setWriters(secondary.getWriters());
			} else {
				log.error("Unsupported Merge Field in Composite Metadata Provider; " + f);
			}
		}
		
		// now find all fields that null in the primary, and set them from the seconday
		if (md.getGenres()==null || md.getGenres().length==0) md.setGenres(secondary.getGenres());
		if (md.getActors()==null || md.getActors().length==0) md.setActors(secondary.getActors());
		if (md.getDirectors()==null || md.getDirectors().length==0) md.setDirectors(secondary.getDirectors());
		if (md.getWriters()==null || md.getWriters().length==0) md.setWriters(secondary.getWriters());
		if (md.getAspectRatio()==null) md.setAspectRatio(secondary.getAspectRatio());
		if (md.getCompany()==null) md.setCompany(secondary.getCompany());
		if (md.getMPAARating()==null) md.setMPAARating(secondary.getMPAARating());
		if (md.getPlot()==null) md.setPlot(secondary.getPlot());
		if (md.getReleaseDate()==null) md.setReleaseDate(secondary.getReleaseDate());
		if (md.getRuntime()==null) md.setRuntime(secondary.getRuntime());
		if (md.getThumbnailUrl()==null) md.setThumbnailUrl(secondary.getThumbnailUrl());
		if (md.getTitle()==null) md.setTitle(secondary.getTitle());
		if (md.getUserRating()==null) md.setUserRating(secondary.getUserRating());
		if (md.getYear()==null) md.setYear(secondary.getYear());

		return md;
	}

	public IMediaMetadata getMetaData(String providerDataUrl) throws Exception {
		if (mergeDetails) {
			return mergeDetails(MediaMetadataFactory.getInstance().getProvider(detailsProviderId).getMetaData(providerDataUrl), MediaMetadataFactory.getInstance().getProvider(searcherProviderId).getMetaData(providerDataUrl));
		} else {
			return  mergeDetails(MediaMetadataFactory.getInstance().getProvider(detailsProviderId).getMetaData(providerDataUrl),null);
		}
	}

	public List<IMediaSearchResult> search(int searchType, String arg) throws Exception {
		log.debug("Searching using composite provider: " + getInfo().getId());
		return MediaMetadataFactory.getInstance().getProvider(searcherProviderId).search(searchType, arg);
	}

	public IMediaMetadata getMetaDataByIMDBId(String imdbId) throws Exception, UnsupportedOperationException {
		if (mergeDetails) {
			return mergeDetails(MediaMetadataFactory.getInstance().getProvider(detailsProviderId).getMetaDataByIMDBId(imdbId), MediaMetadataFactory.getInstance().getProvider(searcherProviderId).getMetaDataByIMDBId(imdbId));
		} else {
			return  mergeDetails(MediaMetadataFactory.getInstance().getProvider(detailsProviderId).getMetaDataByIMDBId(imdbId),null);
		}
	}
}
