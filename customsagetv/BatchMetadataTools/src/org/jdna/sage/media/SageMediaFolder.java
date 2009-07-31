package org.jdna.sage.media;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.media.VirtualMediaFolder;
import org.jdna.media.util.PathUtils;

import sagex.api.Database;
import sagex.api.MediaFileAPI;

/**
 * Virtual Folder for Saage Media Items
 * 
 * @author seans
 *
 */
public class SageMediaFolder extends VirtualMediaFolder {
    private static final Logger log = Logger.getLogger(SageMediaFolder.class);
    private Object[] initList=null;
    private String uriCommand = null;
    private String sageQueryTypes = null;
    private String titleFilter = null;
   
    /**
     * The Sage uri is in the form, sage://query/SAGE_TYPES, where SAGE_TYPES is T for TV, D for DVD, V for Vidoe, B for Bluray.
     * ie, sage://query/DV, for DVD and VIDEO media types
     * 
     * @param uri
     * @throws URISyntaxException
     */
    public SageMediaFolder(String uri) throws URISyntaxException {
        this(new URI(uri));
    }

    public SageMediaFolder(URI uri) {
        super(PathUtils.createPath(uri));
        
        if (!"sage".equals(uri.getScheme())) {
            throw new RuntimeException("Can only accept SageURIs");
        }
        
        uriCommand = uri.getHost();
        if ("query".equals(uriCommand)) {
            sageQueryTypes = parseLeadingSlash(uri.getPath());
            String q = uri.getQuery();
            if (q!=null) {
                Pattern argPattern = Pattern.compile("([^=]+)=([^&$]+)");
                Matcher argMatcher = argPattern.matcher(q);
                while (argMatcher.find()) {
                    if (argMatcher.group(1).equals("filterTitle")) {
                        titleFilter = argMatcher.group(2);
                    }
                }
            }
        }
    }
    
    private static URI sageURI(String path) {
        try {
            return new URI("sage://" + path);
        } catch (URISyntaxException e) {
            log.error("Failed to create sage uri: " + path,e);
        }
        return null;
    }
    
    public SageMediaFolder(Object media[]) {
        super(PathUtils.createPath(sageURI("list")));
        this.initList=media;
    }

    @Override
    protected void loadMembers() {
        log.debug("Beging Loading Sage Media List");
        if (initList!=null) {
            loadSageMediaFiles(initList);
        } else {
            // do a query lookup (sage://query/TVMDB)
            if ("query".equals(uriCommand)) {
                log.debug("SageMediaFolder: Types: " + sageQueryTypes + "; Filter: " + titleFilter);
                if (titleFilter==null) {
                    if (sageQueryTypes.contains("T")) {
                        // special handler for TV files because otherwise, they get missed.
                        Object files1 = MediaFileAPI.GetMediaFiles(sageQueryTypes);
                        Object files2 = MediaFileAPI.GetMediaFiles("T");
                        Vector files3 = Database.DataUnion(files1, files2);
                        if (files3!=null) {
                            loadSageMediaFiles(files3.toArray());
                        }
                    } else {
                        loadSageMediaFiles(MediaFileAPI.GetMediaFiles(sageQueryTypes));
                    }
                } else {
                    if (sageQueryTypes.contains("T")) {
                        Object files1 = Database.SearchByText(titleFilter, sageQueryTypes);
                        Object files2 = Database.SearchByText(titleFilter, "T");
                        Vector files3 = Database.DataUnion(files1, files2);
                        if (files3!=null) {
                            loadSageMediaFiles(files3.toArray());
                        }
                    } else {
                        loadSageMediaFiles(Database.SearchByText(titleFilter, sageQueryTypes));
                    }
                }
            } else {
                log.warn("No Action for SageURI: " + getLocation().toString());
            }
        }
        log.debug("Finished Loading Sage Media List");
    }
    
    private String parseLeadingSlash(String path) {
        if (path==null) {
            return null;
        }
        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    protected void loadSageMediaFiles(Object[] list) {
        members = new SageMediaList(list);
    }

    public String getUriCommand() {
        return uriCommand;
    }

    public String getSageQueryTypes() {
        return sageQueryTypes;
    }
}
