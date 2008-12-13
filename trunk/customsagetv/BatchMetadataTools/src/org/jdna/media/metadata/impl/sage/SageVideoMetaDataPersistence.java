package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataUtils;

public class SageVideoMetaDataPersistence implements IMediaMetadataPersistence {
	private static final Logger log = Logger.getLogger(SageVideoMetaDataPersistence.class);

	public static final String _SER_GENRE = "_serializedGenres";
	public static final String _SER_ACTORS = "_serializedActors";
	public static final String _SER_WRITERS = "_serializedWriters";
	public static final String _SER_DIRECTORS = "_serializedDirectors";
	public static final String _SER_TITLE = "_serializedTitle";
	public static final String _SER_DESCRIPTION = "_serializedDescription";

	public static final String _OTHER_PARTS = "_otherParts";

	public static final String _ASPECT_RATIO = "_aspectRatio";
	public static final String _RELEASE_DATE = "_releaseDate";
	public static final String _THUMBNAIL_URL = "_thumbnailUrl";
	public static final String _USER_RATING = "_userRating";
	public static final String _PROVIDER_ID = "_providerId";
	public static final String _PROVIDER_DATA_URL = "_providerDataUrl";
	public static final String _COMPANY = "_company";
	public static final String _DISC = "_disc";

	public static final String WRITER = "Writer";
	public static final String DIRECTOR = "Director";
	public static final String ACTOR = "Actor";
	public static final String DESCRIPTION = "Description";
	public static final String RUNNING_TIME = "RunningTime";
	public static final String TITLE = "Title";
	public static final String YEAR = "Year";
	public static final String GENRE = "Genre";
	public static final String RATED = "Rated";

	public SageVideoMetaDataPersistence() {
	}

	public String getDescription() {
		return "Export/Import in SageTV properties format";
	}

	public String getId() {
		return "sage";
	}

	private File getPropertyFile(IMediaResource mediaFile) {
		try {
			return new File(new URI(mediaFile.getLocalMetadataUri()));
		} catch (URISyntaxException e) {
			log.error("Failed to create File Uri!", e);
			return null;
		}
	}

	/**
	 * We only load the metadata for the first part of a MediaFile.
	 */
	private Properties loadProperties(IMediaResource mf) throws Exception {
		File propFile = getPropertyFile(mf);
		Properties props = new Properties();
		if (propFile != null && propFile.exists() && propFile.canRead()) {
			log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
			props.load(new FileInputStream(propFile));
		}
		return props;
	}

	public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, boolean overwriteThumbnail) throws IOException {
		if (md.getTitle() == null)
			throw new IOException("MetaData doesn't contain title.  Will not Save/Update.");

		if (mediaFile.getType() != IMediaFile.TYPE_FILE) {
			throw new IOException("Can only store metadata for IMedaiFile.TYPE_FILE objects.  Not a valid file: " + mediaFile.getLocationUri());
		}

		ConfigurationManager cm = ConfigurationManager.getInstance();

		// now copy this metadata...
		Properties props;
		try {
			props = loadProperties(mediaFile);
		} catch (Exception e) {
			log.error("There was an error trying reload the existing properties.  We may lose some data.", e);
			props = new Properties();
		}

		// Store other props and serializedProps
		props.put(_COMPANY, encodeString(md.getCompany()));
		props.put(_PROVIDER_DATA_URL, encodeString(md.getProviderDataUrl()));
		props.put(_PROVIDER_ID, encodeString(md.getProviderId()));
		props.put(_USER_RATING, encodeString(md.getUserRating()));

		// we only copy/update the thumbnail url IF the update thumbnail has
		// been set.
		// this prevents us from frivously updating a custom set thumbnail url
		if (overwriteThumbnail || props.getProperty(_THUMBNAIL_URL) == null || props.getProperty(_THUMBNAIL_URL).trim().length()>0) {
			props.put(_THUMBNAIL_URL, encodeString(md.getThumbnailUrl()));
		}
		props.put(_RELEASE_DATE, encodeString(md.getReleaseDate()));
		props.put(_ASPECT_RATIO, encodeString(md.getAspectRatio()));

		// serialze some fields
		props.put(_SER_GENRE, encodeString(serializeStrings(md.getGenres())));
		props.put(_SER_ACTORS, encodeString(serializeCast(md.getActors())));
		props.put(_SER_WRITERS, encodeString(serializeCast(md.getWriters())));
		props.put(_SER_DIRECTORS, encodeString(serializeCast(md.getDirectors())));
		props.put(_SER_TITLE, encodeString(md.getTitle()));
		props.put(_SER_DESCRIPTION, encodeString(md.getPlot()));

		// Sage recognized properties
		props.put(TITLE, encodeString(md.getTitle()));
		props.put(YEAR, encodeString(md.getYear()));
		props.put(GENRE, encodeString(encodeGenres(md.getGenres())));
		props.put(RATED, encodeString(md.getMPAARating()));
		props.put(RUNNING_TIME, encodeString(md.getRuntime()));
		props.put(ACTOR, encodeString(encodeActors(md.getActors(), cm.getSageMetadataConfiguration().getActorMask())));
		props.put(WRITER, encodeString(encodeWriters(md.getWriters())));
		props.put(DIRECTOR, encodeString(encodeDirectors(md.getDirectors())));
		props.put(DESCRIPTION, encodeString(md.getPlot()));

		// lastly encode the description, to ensure that all other props are
		// set.
		props.put(DESCRIPTION, encodeDescription(md, cm.getSageMetadataConfiguration().getDescriptionMask(), props));

		// do the actual save
		save((IMediaFile) mediaFile, md, props, overwriteThumbnail);
	}

	private String serializeCast(ICastMember[] members) {
		if (members != null && members.length > 0) {
			String cast[] = new String[members.length];
			for (int i = 0; i < members.length; i++) {
				cast[i] = serializeCastMember(members[i]);
			}
			return serializeStrings(cast);
		} else {
			return null;
		}
	}

	private String serializeCastMember(ICastMember cm) {
		StringBuffer sb = new StringBuffer();
		// type|name|part|url
		sb.append(cm.getType()).append("|").append(cm.getName()).append("|").append(cm.getPart()).append("|").append(cm.getProviderDataUrl());
		return sb.toString();
	}

	private String serializeParts(IMediaFile mediaFile) {
		if (mediaFile.isStacked()) {
			List<IMediaResource> l = mediaFile.getParts();
			String parts[] = new String[l.size()];
			for (int i = 0; i < parts.length; i++) {
				parts[i] = l.get(i).getLocationUri();
			}
			return serializeStrings(parts);
		} else {
			return null;
		}
	}

	private String serializeStrings(String[] strings) {
		if (strings == null)
			return null;

		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (String s : strings) {
			if (!first)
				sb.append(";");
			sb.append(s);
			first = false;
		}
		return sb.toString();
	}

	private void save(IMediaFile mediaFileParent, IMediaMetadata md, Properties props, boolean overwriteThumbnail) {
		// in the event that this is a grouped/stacked MediaFile, we need to
		// write the metadata for each part.
		if (mediaFileParent.isStacked()) {
			int i = 1;
			String localThumb = null;
			for (IMediaResource mf : mediaFileParent.getParts()) {
				if (mf instanceof IMediaFile) {
					// Update the title with the title mask before saving multi
					// part movies
					// store the title
					props.setProperty(TITLE, props.getProperty(_SER_TITLE));
					// set the disc # in the props
					props.setProperty(_DISC, String.valueOf(i++));
					// update it using the mask
					props.setProperty(TITLE, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getMultiCDTitleMask(), props));
					// for multiple parts, we try tro re-use the thumbnail to
					// reduce the amount of downloading...
					localThumb = save(props, (IMediaFile) mf, md, localThumb, overwriteThumbnail);
				} else {
					log.error("Unknown Media File type for: " + mf.getLocationUri() + "; " + mf.getClass().getName());
				}
			}
		} else {
			// store the title
			props.setProperty(TITLE, props.getProperty(_SER_TITLE));
			// update it using the mask
			props.setProperty(TITLE, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTitleMask(), props));
			save(props, mediaFileParent, md, null, overwriteThumbnail);
		}
	}

	private String save(Properties props, IMediaFile mf, IMediaMetadata md, String localThumbFile, boolean overwriteThumbnail) {
		File partFile = getPropertyFile(mf);
		try {
			log.debug("Saving Sage video metadata properties: " + partFile.getAbsolutePath());

			// update local values for this instance
			File thumbFile = getThumbnailFile(mf);
			props.store(new FileOutputStream(partFile), "Sage Video Metadata for " + mf.getLocationUri());

			// now download and save the thumbnail, if it does not exist
			thumbFile = getThumbnailFile(mf);
			if (!thumbFile.exists() || overwriteThumbnail) {
				try {
					if (localThumbFile == null) {
						localThumbFile = md.getThumbnailUrl();
					}
					MediaMetadataUtils.writeImageFromUrl(localThumbFile, thumbFile);
					log.debug("Stored Thumbanil: " + thumbFile.getAbsolutePath() + " from " + localThumbFile);

					// next time, use the local file for the thumbnail url,
					// saves
					// excessive downloading for multipart cds
					localThumbFile = thumbFile.toURI().toURL().toExternalForm();
				} catch (Exception e) {
					log.error("Failed to save/download thumbnail: " + localThumbFile + " to: " + thumbFile.getAbsolutePath() + "; But the rest of the property data has been saved.", e);
				}
			}

			// update the file data/time on the mediafile
			mf.touch();
			log.debug("Touched Media File: " + mf.getLocationUri() + " so that sage to reload the metadata.");
		} catch (IOException e) {
			log.error("Failed to save properties: " + partFile.getAbsolutePath(), e);
		}

		return localThumbFile;
	}

	private static String encodeString(String s) {
		return (s == null) ? "" : s.trim();
	}

	private static String encodeWriters(ICastMember[] writers) {
		if (writers == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (ICastMember c : writers) {
			sb.append(c.getName()).append(";");
		}
		return sb.toString();
	}

	private static String encodeDirectors(ICastMember[] directors) {
		if (directors == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (ICastMember c : directors) {
			sb.append(c.getName()).append(";");
		}
		return sb.toString();
	}

	private static String encodeActors(ICastMember[] actors, String mask) {
		if (actors == null)
			return "";

		if (mask == null)
			mask = "{0} -- {1};";

		StringBuffer sb = new StringBuffer();
		for (ICastMember c : actors) {
			sb.append(MessageFormat.format(mask, c.getName(), c.getPart()));
		}
		return sb.toString();
	}

	private static String encodeDescription(IMediaMetadata md, String mask, Properties props) {
		if (mask == null)
			mask = "${Description}";
		return MediaMetadataUtils.format(mask, props);
	}

	private static String encodeGenres(String[] genres) {
		if (genres == null)
			return "";

		int genreLevels = ConfigurationManager.getInstance().getSageMetadataConfiguration().getGenreLevels();
		if (genreLevels==-1) genreLevels=genres.length;
		int max = Math.min(genres.length, genreLevels);
		
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<max;i++) {
			if (i>0) sb.append("/");
			sb.append(genres[i]);
		}

		return sb.toString();
	}

	public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
		File propFile = getPropertyFile(mediaFile);

		if (propFile != null && propFile.exists()) {
			try {
				MediaMetadata md = new MediaMetadata();
				log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
				Properties props = loadProperties(mediaFile);

				md.setActors(deserializeCast(props.getProperty(_SER_ACTORS)));
				md.setAspectRatio(props.getProperty(_ASPECT_RATIO));
				md.setCompany(props.getProperty(_COMPANY));
				md.setDirectors(deserializeCast(props.getProperty(_SER_DIRECTORS)));
				md.setGenres(deserializeStrings(props.getProperty(_SER_GENRE, props.getProperty(GENRE))));
				md.setMPAARating(props.getProperty(RATED));
				md.setPlot(props.getProperty(_SER_DESCRIPTION, props.getProperty(DESCRIPTION)));
				md.setProviderDataUrl(props.getProperty(_PROVIDER_DATA_URL));
				md.setProviderId(props.getProperty(_PROVIDER_ID));
				md.setReleaseDate(props.getProperty(_RELEASE_DATE));
				md.setRuntime(props.getProperty(RUNNING_TIME));
				md.setThumbnailUrl(props.getProperty(_THUMBNAIL_URL));
				md.setTitle(props.getProperty(_SER_TITLE, props.getProperty(TITLE)));
				md.setUserRating(props.getProperty(_USER_RATING));
				md.setWriters(deserializeCast(props.getProperty(_SER_WRITERS)));
				md.setYear(props.getProperty(YEAR));
				md.setUpdated(propFile.lastModified() > mediaFile.lastModified());
				return md;
			} catch (Exception e) {
				log.error("Failed to load sage properties: " + propFile.getAbsolutePath(), e);
				return null;
			}
		} else {
			log.info("No Metadata for file: " + propFile.getAbsolutePath());
			return null;
		}
	}

	private String[] deserializeStrings(String s) {
		if (s == null)
			return null;
		return s.split(";");
	}

	private CastMember[] deserializeCast(String s) {
		if (s == null)
			return null;
		try {
			String m[] = deserializeStrings(s);
			CastMember all[] = new CastMember[m.length];
			for (int i = 0; i < m.length; i++) {
				all[i] = deserializeCastMember(m[i]);
			}

			return all;
		} catch (Exception e) {
			log.error("Failed to deserialize the string: " + s, e);
			return null;
		}
	}

	private CastMember deserializeCastMember(String cmStr) {
		CastMember cm = new CastMember();

		String flds[] = cmStr.split("\\|");
		try {
			// type|name|part|url
			cm.setType(Integer.parseInt(flds[0]));
			cm.setName(flds[1]);
			cm.setPart(flds[2]);
			cm.setProviderDataUrl(flds[3]);

		} catch (Throwable e) {
			log.warn("Failed to parse cast member from string: " + cmStr);
			// for (String f : flds) {
			// log.debug(String.format("Field: [%s]", f));
			// }
		}

		return cm;
	}

	public File getThumbnailFile(IMediaFile mediaFile) {
		try {
			return new File(new URI(mediaFile.getLocalThumbnailUri()));
		} catch (URISyntaxException e) {
			log.error("Failed to get local thumbnail file to media file!", e);
			return null;
		}
	}
	
	public static Map<String, String> metadataToSageTVMap(IMediaMetadata md) {
		Map<String, String> props = new HashMap<String, String>();
		// Sage recognized properties
		props.put(TITLE, encodeString(md.getTitle()));
		props.put(YEAR, encodeString(md.getYear()));
		props.put(GENRE, encodeString(encodeGenres(md.getGenres())));
		props.put(RATED, encodeString(md.getMPAARating()));
		props.put(RUNNING_TIME, encodeString(md.getRuntime()));
		props.put(ACTOR, encodeString(encodeActors(md.getActors(), ConfigurationManager.getInstance().getSageMetadataConfiguration().getActorMask())));
		props.put(WRITER, encodeString(encodeWriters(md.getWriters())));
		props.put(DIRECTOR, encodeString(encodeDirectors(md.getDirectors())));
		props.put(DESCRIPTION, encodeString(md.getPlot()));
		props.put(DESCRIPTION, encodeString(md.getPlot()));
		return props;
	}
}
