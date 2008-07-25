package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.impl.DVDMediaFolder;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.VideoMetaDataUtils;

/**
 * <pre>
 * The valid property names for music and video files (not DVDs currently) are:
 * 
 * Title
 * Album
 * Artist
 * AlbumArtist
 * Composer
 * Track
 * TotalTracks
 * Year
 * Comment
 * Genre
 * GenreID
 * Language
 * Rated
 * RunningTime
 * Duration
 * Description
 * 
 *  In addition to the above property names, video files can also use the following properties for roles:
 * 
 * Actor
 * Lead\ Actor
 * Supporting\ Actor
 * Actress
 * Lead\ Actress
 * Supporting\ Actress
 * Guest
 * Guest\ Star
 * Director
 * Producer
 * Writer
 * Choreographer
 * Sports\ Figure
 * Coach
 * Host
 * Executive\ Producer
 * </pre>
 * 
 * @author seans
 * 
 * TODO: All other types need to be Serialized and Deserialized, currently we
 * are only handling string types for deserialization.
 * 
 */
public class SageVideoMetaData implements IVideoMetaData {
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

	public static final Logger log = Logger.getLogger(SageVideoMetaData.class);

	public static final String DESCRIPTION_MASK_KEY = "descriptionMask";
	public static final String ACTOR_MASK_KEY = "actorMask";
	public static final String DOWNLOAD_THUMBNAIL_KEY = "downloadThumbnail";

	private Properties props = new Properties();
	private File propFile = null;
	private String localThumbFile = null;
	private IMediaFile mediaFileParent;
	private boolean hasMetaData = false;
	private String title = null;

	public SageVideoMetaData(IMediaFile mediaFile) {
		this.mediaFileParent = mediaFile;

		// store a reference to the property file for the first item;
		this.propFile = getPropertyFile(mediaFile);

		if (this.propFile.exists()) {
			load();
		}
	}

	public SageVideoMetaData(IMediaFile mediaFile, IVideoMetaData copy) {
		this(mediaFile);

		ConfigurationManager cm = ConfigurationManager.getInstance();

		// now copy this metadata...
		// Sage recognized properties
		props.put(TITLE, encodeString(copy.getTitle()));
		props.put(YEAR, encodeString(copy.getYear()));
		props.put(GENRE, encodeString(encodeGenres(copy.getGenres())));
		props.put(RATED, encodeString(copy.getMPAARating()));
		props.put(RUNNING_TIME, encodeString(copy.getRuntime()));
		props.put(ACTOR, encodeString(encodeActors(copy.getActors(), cm
				.getProperty(this.getClass().getName(), ACTOR_MASK_KEY))));
		props.put(WRITER, encodeString(encodeWriters(copy.getWriters())));
		props.put(DIRECTOR, encodeString(encodeDirectors(copy.getDirectors())));
		props.put(DESCRIPTION, encodeString(copy.getPlot()));

		// Other Props
		props.put(_COMPANY, encodeString(copy.getCompany()));
		props.put(_PROVIDER_DATA_URL, encodeString(copy.getProviderDataUrl()));
		props.put(_PROVIDER_ID, encodeString(copy.getProviderId()));
		props.put(_USER_RATING, encodeString(copy.getUserRating()));
		props.put(_THUMBNAIL_URL, encodeString(copy.getThumbnailUrl()));
		props.put(_RELEASE_DATE, encodeString(copy.getReleaseDate()));
		props.put(_ASPECT_RATIO, encodeString(copy.getAspectRatio()));


		// lastly encode the description, to ensure that all other props are
		// set.
		props.put(DESCRIPTION, encodeDescription(copy, cm.getProperty(this
				.getClass().getName(), DESCRIPTION_MASK_KEY), props));
		
		// store the title so that we can work with it later
		this.title = getTitle();
	}

	public File getPropertyFile(IMediaFile mediaFile) {
		File f = new File(mediaFile.getLocationUri());
		if (mediaFile instanceof DVDMediaFolder) {
			return new File(f.getParentFile(), f.getName() + ".properties");
		} else {
			return new File(f.getParentFile(), mediaFile.getName() + ".properties");
		}
	}

	public List<ICastMember> getActors() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAspectRatio() {
		return props.getProperty(_ASPECT_RATIO);
	}

	public String getCompany() {
		return props.getProperty(_COMPANY);
	}

	public List<ICastMember> getDirectors() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getGenres() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMPAARating() {
		return props.getProperty(RATED);
	}

	public String getPlot() {
		return props.getProperty(DESCRIPTION);
	}

	public String getProviderDataUrl() {
		return props.getProperty(_PROVIDER_DATA_URL);
	}

	public String getReleaseDate() {
		return props.getProperty(_RELEASE_DATE);
	}

	public String getRuntime() {
		return props.getProperty(RUNNING_TIME);
	}

	public String getThumbnailUrl() {
		return props.getProperty(_THUMBNAIL_URL);
	}

	public String getTitle() {
		return props.getProperty(TITLE);
	}

	public String getUserRating() {
		return props.getProperty(_USER_RATING);
	}

	public List<ICastMember> getWriters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getYear() {
		return props.getProperty(YEAR);
	}

	public boolean isUpdated() {
		return propFile.lastModified() > mediaFileParent.lastModified();
	}

	private String encodeString(String s) {
		return (s == null) ? "" : s;
	}

	private String encodeWriters(List<ICastMember> writers) {
		StringBuffer sb = new StringBuffer();
		for (ICastMember c : writers) {
			sb.append(c.getName()).append(";");
		}
		return sb.toString();
	}

	private String encodeDirectors(List<ICastMember> directors) {
		StringBuffer sb = new StringBuffer();
		for (ICastMember c : directors) {
			sb.append(c.getName()).append(";");
		}
		return sb.toString();
	}

	private String encodeActors(List<ICastMember> actors, String mask) {
		if (mask == null)
			mask = "{0} -- {1};";

		StringBuffer sb = new StringBuffer();
		for (ICastMember c : actors) {
			sb.append(MessageFormat.format(mask, c.getName(), c.getPart()));
		}
		return sb.toString();
	}

	private String encodeDescription(IVideoMetaData md, String mask,
			Properties props) {
		if (mask == null)
			mask = "${Description}";
		return VideoMetaDataUtils.format(mask, props);
	}

	private String encodeGenres(List<String> genres) {
		boolean single = Boolean.parseBoolean(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "singleGenreField", "true"));
		if (single && genres != null && genres.size()>0) {
			return genres.get(0);
		} else {
			StringBuffer sb = new StringBuffer();
	
			for (String s : genres) {
				sb.append(s).append("/");
			}
	
			return sb.toString();
		}
	}

	/**
	 * We only load the metadata for the first part of a MediaFile.
	 */
	public void load() {
		if (propFile != null && propFile.exists()) {
			try {
				log.debug("Loading Sage Video Metadata properties: "
						+ propFile.getAbsolutePath());
				props.load(new FileInputStream(propFile));
				hasMetaData = true;
			} catch (IOException e) {
				log.error("Failed to load sage properties: "
						+ propFile.getAbsolutePath(), e);
			}
		}
	}

	public void save() {
		if (propFile != null) {
			// in the event that this is a grouped/stacked MediaFile, we need to
			// write the metadata for each part.
			if (mediaFileParent.isStacked()) {
				int i = 1;
				for (IMediaResource mf : mediaFileParent.getParts()) {
					if (mf instanceof IMediaFile) {
						// Update the title with the title mask before saving multi part movies
						// store the title
						props.setProperty(TITLE, title);
						// set the disc # in the props
						props.setProperty(_DISC, String.valueOf(i++));
						// update it using the mask
						props.setProperty(TITLE, VideoMetaDataUtils.format(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "titleMaskMultiCd"), props));
						save((IMediaFile) mf);
					} else {
						log.error("Unknown Media File type for: "
								+ mf.getLocationUri() + "; "
								+ mf.getClass().getName());
					}
				}
			} else {
				// store the title
				props.setProperty(TITLE, title);
				// update it using the mask
				props.setProperty(TITLE, VideoMetaDataUtils.format(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "titleMask"), props));
				save(mediaFileParent);
			}
			hasMetaData = true;
		}
	}

	private void save(IMediaFile mf) {
		File partFile = getPropertyFile(mf);
		try {
			log.debug("Saving Sage video metadata properties: "
					+ partFile.getAbsolutePath());
			
			props.store(new FileOutputStream(partFile),
					"Sage Video Metadata for " + mf.getLocationUri());

			// now download and save the thumbnail, if it does not exist
			ConfigurationManager cm = ConfigurationManager.getInstance();
			boolean dlThumb = Boolean.parseBoolean(cm.getProperty(this
					.getClass().getName(), DOWNLOAD_THUMBNAIL_KEY, "true"));
			boolean downloadThumb = getThumbnailUrl() != null
					&& getThumbnailUrl().trim().length() > 0 && dlThumb;
			boolean overwrite = Boolean.parseBoolean(cm.getProperty(this
					.getClass().getName(), "overwriteThumbnail", "false"));
			if (downloadThumb) {
				File thumbFile = getThumbnailFile(mf);
				if (!thumbFile.exists() || overwrite) {
					try {
						if (localThumbFile == null) {
							localThumbFile = getThumbnailUrl();
						}
						VideoMetaDataUtils.writeImageFromUrl(localThumbFile,
								thumbFile);
						log.debug("Stored Thumbanil: "
								+ thumbFile.getAbsolutePath() + " from "
								+ localThumbFile);

						// next time, use the local file for the thumbnail url,
						// saves
						// excessive downloading for multipart cds
						localThumbFile = thumbFile.toURI().toURL()
								.toExternalForm();
					} catch (Exception e) {
						log
								.error(
										"Failed to save/download thumbnail: "
												+ localThumbFile
												+ " to: "
												+ thumbFile.getAbsolutePath()
												+ "; But the rest of the property data has been saved.",
										e);
					}
				}
			} else {
				log.warn("Skipping Thumbnail download for " + mf.getLocationUri());
			}

			// update the file data/time on the mediafile
			mf.touch();
			log.debug("Touched Media File: " + mf.getLocationUri() + " so that sage to reload the metadata.");
		} catch (IOException e) {
			log.error("Failed to save properties: "
					+ propFile.getAbsolutePath(), e);
		}
	}

	public boolean hasMetaData() {
		return hasMetaData;
	}

	/**
	 * For regular files, returns basefilename + .jpg ext.  For DVD Folders, it will return VIDEO_TS/folder.jpg if VIDEO_TS dir exists, or just basedirname + .jpg
	 * 
	 * @param mediaFile
	 * @return
	 */
	public File getThumbnailFile(IMediaFile mediaFile) {
		File f = new File(mediaFile.getLocationUri());
		if (mediaFile instanceof DVDMediaFolder) {
			boolean useTSVIDEO = Boolean.parseBoolean(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "thumbnailInTS_VIDEO", "false"));
			File tsdir = new File(f, "VIDEO_TS");
			if (tsdir.exists() && useTSVIDEO) {
				return new File(tsdir, "folder.jpg");
			} else {
				return new File(f.getParentFile(), f.getName() + ".jpg");
			}
		} else {
			return new File(f.getParentFile(), VideoMetaDataUtils.getBasename(f)
				+ ".jpg");
		}
	}

	public String getProviderId() {
		return props.getProperty(_PROVIDER_ID);
	}

}
