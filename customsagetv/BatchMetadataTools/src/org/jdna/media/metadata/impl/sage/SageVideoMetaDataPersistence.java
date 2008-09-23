package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.impl.DVDMediaFolder;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataPersistence;
import org.jdna.media.metadata.VideoMetaData;
import org.jdna.media.metadata.VideoMetaDataUtils;

public class SageVideoMetaDataPersistence implements IVideoMetaDataPersistence {
	private static final Logger log = Logger.getLogger(SageVideoMetaDataPersistence.class);

	public static final String _SER_GENRE = "_serializedGenres";
	public static final String _SER_ACTORS = "_serializedActors";
	public static final String _SER_WRITERS = "_serializedWriters";
	public static final String _SER_DIRECTORS = "_serializedDirectors";
	public static final String _SER_TITLE = "_serializedTitle";
	public static final String _SER_DESCRIPTION = "_serializedDescription";

	public static final String _LOCAL_THUMBNAIL = "_localThumnail";
	public static final String _LOCAL_MEDIAFILE = "_localMediaFile";
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

	public static final String DESCRIPTION_MASK_KEY = "descriptionMask";
	public static final String ACTOR_MASK_KEY = "actorMask";
	public static final String DOWNLOAD_THUMBNAIL_KEY = "downloadThumbnail";

	public SageVideoMetaDataPersistence() {
	}

	public String getDescription() {
		return "Export/Import in SageTV properties format";
	}

	public String getId() {
		return "sage";
	}

	private File getPropertyFile(IMediaFile mediaFile) {
		File f = new File(URI.create(mediaFile.getLocationUri()));
		if (mediaFile instanceof DVDMediaFolder) {
			return new File(f.getParentFile(), f.getName() + ".properties");
		} else {
			return new File(f.getParentFile(), mediaFile.getName() + ".properties");
		}
	}

	/**
	 * We only load the metadata for the first part of a MediaFile.
	 */
	private Properties loadProperties(IMediaFile mf) {
		File propFile = getPropertyFile(mf);
		Properties props = new Properties();
		if (propFile != null && propFile.exists()) {
			try {
				log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
				props.load(new FileInputStream(propFile));
			} catch (IOException e) {
				log.error("Failed to load sage properties: " + propFile.getAbsolutePath(), e);
			}
		}
		return props;
	}

	public void storeMetaData(IVideoMetaData md, IMediaFile mediaFile) throws IOException {
		if (md.getTitle() == null)
			throw new IOException("MetaData doesn't contain title.  Will not Save/Update.");

		ConfigurationManager cm = ConfigurationManager.getInstance();

		// now copy this metadata...
		Properties props = loadProperties(mediaFile);

		// Store other props and serializedProps
		props.put(_COMPANY, encodeString(md.getCompany()));
		props.put(_PROVIDER_DATA_URL, encodeString(md.getProviderDataUrl()));
		props.put(_PROVIDER_ID, encodeString(md.getProviderId()));
		props.put(_USER_RATING, encodeString(md.getUserRating()));
		props.put(_THUMBNAIL_URL, encodeString(md.getThumbnailUrl()));
		props.put(_RELEASE_DATE, encodeString(md.getReleaseDate()));
		props.put(_ASPECT_RATIO, encodeString(md.getAspectRatio()));

		// serialze some fields
		props.put(_SER_GENRE, encodeString(serializeStrings(md.getGenres())));
		props.put(_SER_ACTORS, encodeString(serializeCast(md.getActors())));
		props.put(_SER_WRITERS, encodeString(serializeCast(md.getWriters())));
		props.put(_SER_DIRECTORS, encodeString(serializeCast(md.getDirectors())));
		props.put(_SER_TITLE, encodeString(md.getTitle()));
		props.put(_SER_DESCRIPTION, encodeString(md.getPlot()));
		props.put(_LOCAL_MEDIAFILE, encodeString(mediaFile.getLocationUri()));
		// props.put(_LOCAL_THUMBNAIL, getThumbnailFile(mediaFile));
		if (mediaFile.isStacked())
			props.put(_OTHER_PARTS, encodeString(serializeParts(mediaFile)));

		// Sage recognized properties
		props.put(TITLE, encodeString(md.getTitle()));
		props.put(YEAR, encodeString(md.getYear()));
		props.put(GENRE, encodeString(encodeGenres(md.getGenres())));
		props.put(RATED, encodeString(md.getMPAARating()));
		props.put(RUNNING_TIME, encodeString(md.getRuntime()));
		props.put(ACTOR, encodeString(encodeActors(md.getActors(), cm.getProperty(this.getClass().getName(), ACTOR_MASK_KEY, "{0} -- {1};\n"))));
		props.put(WRITER, encodeString(encodeWriters(md.getWriters())));
		props.put(DIRECTOR, encodeString(encodeDirectors(md.getDirectors())));
		props.put(DESCRIPTION, encodeString(md.getPlot()));

		// lastly encode the description, to ensure that all other props are
		// set.
		props.put(DESCRIPTION, encodeDescription(md, cm.getProperty(this.getClass().getName(), DESCRIPTION_MASK_KEY, "${Description}\nUser Rating: ${_userRating}\n"), props));

		// do the actual save
		save(mediaFile, md, props);
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

	private void save(IMediaFile mediaFileParent, IVideoMetaData md, Properties props) {
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
					props.setProperty(TITLE, VideoMetaDataUtils.format(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "titleMaskMultiCd", "${Title} Disc ${_disc}"), props));
					// for multiple parts, we try tro re-use the thumbnail to
					// reduce the amount of downloading...
					localThumb = save(props, (IMediaFile) mf, md, localThumb);
				} else {
					log.error("Unknown Media File type for: " + mf.getLocationUri() + "; " + mf.getClass().getName());
				}
			}
		} else {
			// store the title
			props.setProperty(TITLE, props.getProperty(_SER_TITLE));
			// update it using the mask
			props.setProperty(TITLE, VideoMetaDataUtils.format(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "titleMask", "${Title}"), props));
			save(props, mediaFileParent, md, null);
		}
	}

	private String save(Properties props, IMediaFile mf, IVideoMetaData md, String localThumbFile) {
		File partFile = getPropertyFile(mf);
		try {
			log.debug("Saving Sage video metadata properties: " + partFile.getAbsolutePath());

			// update local values for this instance
			props.put(_LOCAL_MEDIAFILE, mf.getLocationUri());

			File thumbFile = getThumbnailFile(mf);
			props.put(_LOCAL_THUMBNAIL, thumbFile.toURI().toString());

			props.store(new FileOutputStream(partFile), "Sage Video Metadata for " + mf.getLocationUri());

			// now download and save the thumbnail, if it does not exist
				thumbFile = getThumbnailFile(mf);
				if (!thumbFile.exists() || md.isThumbnailUpdated()) {
					try {
						if (localThumbFile == null) {
							localThumbFile = md.getThumbnailUrl();
						}
						VideoMetaDataUtils.writeImageFromUrl(localThumbFile, thumbFile);
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

	private String encodeString(String s) {
		return (s == null) ? "" : s;
	}

	private String encodeWriters(ICastMember[] writers) {
		if (writers == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (ICastMember c : writers) {
			sb.append(c.getName()).append(";");
		}
		return sb.toString();
	}

	private String encodeDirectors(ICastMember[] directors) {
		if (directors == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (ICastMember c : directors) {
			sb.append(c.getName()).append(";");
		}
		return sb.toString();
	}

	private String encodeActors(ICastMember[] actors, String mask) {
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

	private String encodeDescription(IVideoMetaData md, String mask, Properties props) {
		if (mask == null)
			mask = "${Description}";
		return VideoMetaDataUtils.format(mask, props);
	}

	private String encodeGenres(String[] genres) {
		if (genres == null)
			return "";

		boolean single = Boolean.parseBoolean(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "singleGenreField", "true"));
		if (single && genres != null && genres.length > 0) {
			return genres[0];
		} else {
			StringBuffer sb = new StringBuffer();

			for (String s : genres) {
				sb.append(s).append("/");
			}

			return sb.toString();
		}
	}

	public IVideoMetaData loadMetaData(IMediaFile mediaFile) {
		File propFile = getPropertyFile(mediaFile);

		if (propFile != null && propFile.exists()) {
			try {
				VideoMetaData md = new VideoMetaData();
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
			// no metadata
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

		} catch (Exception e) {
			log.error("Failed to parse cast member from string: " + cmStr, e);
			for (String f : flds) {
				log.debug(String.format("Field: [%s]", f));
			}
		}

		return cm;
	}

	/**
	 * For regular files, returns basefilename + .jpg ext. For DVD Folders, it
	 * will return VIDEO_TS/folder.jpg if VIDEO_TS dir exists, or just
	 * basedirname + .jpg
	 * 
	 * @param mediaFile
	 * @return
	 */
	public File getThumbnailFile(IMediaFile mediaFile) {
		File f = new File(URI.create(mediaFile.getLocationUri()));
		if (mediaFile instanceof DVDMediaFolder) {
			boolean useTSVIDEO = Boolean.parseBoolean(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "thumbnailInTS_VIDEO", "false"));
			File tsdir = new File(f, "VIDEO_TS");
			if (tsdir.exists() && useTSVIDEO) {
				return new File(tsdir, "folder.jpg");
			} else {
				return new File(f.getParentFile(), f.getName() + ".jpg");
			}
		} else {
			return new File(f.getParentFile(), VideoMetaDataUtils.getBasename(f) + ".jpg");
		}
	}

}
