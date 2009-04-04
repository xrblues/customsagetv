package org.jdna.media.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;

public class MovieResourceFilter implements IMediaResourceFilter {
    private static final Logger             log               = Logger.getLogger(MovieResourceFilter.class);
    public static final MovieResourceFilter INSTANCE          = new MovieResourceFilter();

    private Pattern                         filePattern       = null;
    private Pattern                         dirExcludePattern = null;

    public MovieResourceFilter(String dirFilterRegex) {
        init(dirFilterRegex, ConfigurationManager.getInstance().getMediaConfiguration().getVideoExtensionsRegex());
    }

    public MovieResourceFilter() {
        init(ConfigurationManager.getInstance().getMediaConfiguration().getExcludeVideoDirsRegex(), ConfigurationManager.getInstance().getMediaConfiguration().getVideoExtensionsRegex());
    }
    
    private void init(String dirFilter, String fileFilter) {
        log.debug("Using Movie Filter Regex: " + fileFilter);
        filePattern = Pattern.compile(fileFilter, Pattern.CASE_INSENSITIVE);

        if (dirFilter == null) {
            log.debug("Not Using any Directory Exclude Filters.");
        } else {
            log.debug("Using Directory Exclude Regex: " + dirFilter);
            dirExcludePattern = Pattern.compile(dirFilter, Pattern.CASE_INSENSITIVE);
        }

    }

    public boolean accept(IMediaResource resource) {
        if (resource == null) return false;

        // check dir pattern
        if (dirExcludePattern != null) {
            String uri = resource.getLocationUri();
            Matcher m = dirExcludePattern.matcher(uri);
            if (m.find()) {
                return false;
            }
        }

        if (resource.getType() == IMediaFolder.TYPE_FOLDER) {
            return true;
        } else {
            // if this is a DVD Media Item, then keep it
            if (resource.getContentType() == IMediaResource.CONTENT_TYPE_DVD) {
                return true;
            } else {
                // otherwise, check the ext to see if this a movie
                String ext = resource.getExtension();
                if (ext == null) return false;

                Matcher filematcher = filePattern.matcher(ext);
                return filematcher.matches();
            }
        }
    }
}
