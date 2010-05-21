package org.jdna.bmt.web.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTFactoryInfo;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTFactoryInfo.SourceType;
import org.jdna.bmt.web.client.ui.browser.BrowsingService;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.util.PathUtils;
import sagex.phoenix.vfs.views.ViewFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BrowsingServicesImpl extends RemoteServiceServlet implements BrowsingService {
    private static final Logger log = Logger.getLogger(BrowsingServicesImpl.class);

    public GWTMediaResource[] browseChildren(GWTMediaFolder folder) {
        IMediaFolder vfsFolder = null;
        vfsFolder = getFolderRef(folder);
        List<GWTMediaResource> files = new ArrayList<GWTMediaResource>();
        if (vfsFolder != null) {
            for (IMediaResource r : vfsFolder) {
                files.add(convertResource(r));
            }
        }

        return files.toArray(new GWTMediaResource[files.size()]);
    }

    protected GWTMediaResource convertResource(IMediaResource r) {
        return convertResource(r, getThreadLocalRequest());
    }

    public static GWTMediaResource convertResource(IMediaResource r, HttpServletRequest req) {
        if (r instanceof IMediaFolder) {
            GWTMediaFolder folder = new GWTMediaFolder(null, phoenix.api.GetMediaTitle(r));
            folder.setResourceRef(String.valueOf(r.hashCode()));
            setFolderRef((IMediaFolder) r, req);
            folder.setPath(PathUtils.getLocation(r));
            folder.setMinorTitle(folder.getPath());
            return folder;
        } else {
            GWTMediaFile file = new GWTMediaFile(null, r.getTitle());
            if (r instanceof IMediaFile) {
                if (r.isType(MediaResourceType.TV.value())) {
                    file.setMinorTitle(phoenix.api.GetEpisodeTitle(r));
                }
                Object sageMedia = phoenix.api.GetSageMediaFile(r);
                if (sageMedia!=null) {
                    int id = MediaFileAPI.GetMediaFileID(sageMedia);
                    //file.setThumbnailUrl("media/poster/" + id + "?transform={name:scale,height:120}}");
                    file.setThumbnailUrl("media/poster/" + id);
                    file.setSageMediaFileId(id);
                    file.setAiringId(String.valueOf(AiringAPI.GetAiringID(sageMedia)));
                    file.setShowId(ShowAPI.GetShowExternalID(sageMedia));
                    File f = new File(phoenix.api.GetFanartBackgroundPath(sageMedia));
                    f=f.getParentFile();
                    file.setFanartDir(f.getAbsolutePath());
                    file.getSageRecording().set(MediaFileAPI.IsTVFile(sageMedia));
                } else {
                    log.debug("Not a sage media object??");
                }
                log.debug("Setting Last Modified: " + file + "; " + r.lastModified());
                file.setLastModified(r.lastModified());
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
        log.debug("Getting Folder for source: " + source);

        if (source.getSourceType() == SourceType.View) {

            ViewFactory factory = null;
            if (source.getSourceType() == SourceType.View) {
                factory = Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactory(source.getId());
            } else {
                log.warn("Inavlid Source: " + source.getId());
            }

            if (factory != null) {
                IMediaFolder f = factory.create(null);
                setFolderRef(f);
                log.debug("**** Returning newly created folder: " + f);
                return (GWTMediaFolder) convertResource(f);
            } else {
                log.warn("Failed to get factory for: " + source.getId());
            }
        /**
        } else if (source.getSourceType() == SourceType.Group) {
            log.debug("** Grouping: " + source.getLabel());
            IMediaFolder folderRef = getFolderRef(folder);
            if (folderRef==null) return null;
            
            GroupingFactory groupFactory = Phoenix.getInstance().getVFSGroupFactory().getFactory(source.getId());
            folderRef = phoenix.api.SetMediaGroup(folderRef, groupFactory.create(null));
            setFolderRef(folderRef);
            return (MediaFolder) convertResource(folderRef);
        } else if (source.getSourceType() == SourceType.Sort) {
            log.debug("** Sorting: " + source.getLabel());
            IMediaFolder folderRef = getFolderRef(folder);
            if (folderRef==null) return null;
            
            SorterFactory factory = Phoenix.getInstance().getVFSSortFactory().getFactory(source.getId());
            folderRef = phoenix.api.SetMediaSort(folderRef, factory.create(null));
            setFolderRef(folderRef);
            return (MediaFolder) convertResource(folderRef);
        } else if (source.getSourceType() == SourceType.Filter) {
            log.debug("** Filtering: " + source.getLabel());
            IMediaFolder folderRef = getFolderRef(folder);
            if (folderRef==null) return null;
            
            FilterFactory factory = Phoenix.getInstance().getVFSFilterFactory().getFactory(source.getId());
            folderRef = phoenix.api.SetMediaFilter(folderRef, factory.create(null));
            setFolderRef(folderRef);
            return (MediaFolder) convertResource(folderRef);
        **/
        } else {
            log.error("Unhandled Factory: " + source.getId());
        }

        return null;
    }

    public GWTFactoryInfo[] getFactories(SourceType sourceType) {
        List<GWTFactoryInfo> sources = new ArrayList<GWTFactoryInfo>();

        /*
        if (sourceType == sourceType.Source) {
            List<Factory<IMediaFolder>> factories = Phoenix.getInstance().getVFSSourceFactory().getFactories();
            for (Factory<IMediaFolder> f : factories) {
                GWTFactoryInfo s = new GWTFactoryInfo(sourceType, f.getId(), f.getLabel(), f.getDescription());
                sources.add(s);
            }
        */
        if (sourceType == SourceType.View) {
            List<ViewFactory> factories = phoenix.api.GetViewFactories();
            for (ViewFactory f : factories) {
                GWTFactoryInfo s = new GWTFactoryInfo(sourceType, f.getId(), f.getLabel(), f.getDescription());
                sources.add(s);
            }
        /*
        } else if (sourceType == SourceType.Sort) {
            List<Factory<Comparator<IMediaResource>>> factories = Phoenix.getInstance().getVFSSortFactory().getFactories();
            for (Factory<Comparator<IMediaResource>> f : factories) {
                GWTFactoryInfo s = new GWTFactoryInfo(sourceType, f.getId(), f.getLabel(), f.getDescription());
                sources.add(s);
            }
        } else if (sourceType == SourceType.Group) {
            List<Factory<IGrouper>> factories = Phoenix.getInstance().getVFSGroupFactory().getFactories();
            for (Factory<IGrouper> f : factories) {
                GWTFactoryInfo s = new GWTFactoryInfo(sourceType, f.getId(), f.getLabel(), f.getDescription());
                sources.add(s);
            }
        } else if (sourceType == SourceType.Filter) {
            List<Factory<IResourceFilter>> factories = Phoenix.getInstance().getVFSFilterFactory().getFactories();
            for (Factory<IResourceFilter> f : factories) {
                GWTFactoryInfo s = new GWTFactoryInfo(sourceType, f.getId(), f.getLabel(), f.getDescription());
                sources.add(s);
            }
        */
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
}
