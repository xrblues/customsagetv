package sagex.remote.builder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sagex.api.AiringAPI;
import sagex.api.AlbumAPI;
import sagex.api.ChannelAPI;
import sagex.api.FavoriteAPI;
import sagex.api.MediaFileAPI;
import sagex.api.PlaylistAPI;
import sagex.api.ShowAPI;
import sagex.api.SystemMessageAPI;

public class SageAPIBuilder {
    public static final SageAPIBuilder INSTANCE = new SageAPIBuilder();
    
    private Map<Class, Method[]>  reflectionMap = new HashMap<Class, Method[]>();
    
    public void build(String name, Object parent, BuilderHandler handler, boolean arrayItem) throws Exception {
        if (parent==null) {
            buildSimpleData(name, parent, handler);
        } else if (parent instanceof String || parent.getClass().isPrimitive() || Number.class.isAssignableFrom(parent.getClass()) || Boolean.class.isAssignableFrom(parent.getClass())) {
            if (arrayItem) {
                buildSimpleData("Item", parent, handler);
            } else {
                buildSimpleData(name, parent, handler);
            }
        } else if (parent instanceof Collection) {
            buildCollection(name, (Collection) parent, handler);
        } else if (parent instanceof File) {
            if (arrayItem) {
                buildFile("File", (File)parent, handler);
            } else {
                buildFile(name, (File)parent, handler);
            }
        } else if (parent.getClass().isArray()) {
            buildArray(name, (Object[])parent, handler);
        } else if (MediaFileAPI.IsMediaFileObject(parent)) {
            buildMediaFile(parent, handler);
        } else if(ShowAPI.IsShowObject(parent)) {
            buildShow(parent, handler);
        } else if(AiringAPI.IsAiringObject(parent)) {
            buildAiring(parent, handler);
        } else if(AlbumAPI.IsAlbumObject(parent)) {
            buildAlbum(parent, handler);
        } else if(FavoriteAPI.IsFavoriteObject(parent)) {
            buildFavorite(parent, handler);
        } else if(ChannelAPI.IsChannelObject(parent)) {
            buildChannel(parent, handler);
        } else if(PlaylistAPI.IsPlaylistObject(parent) || parent.toString().startsWith("Playlist[")) {
            buildPlaylist(parent, handler);
        } else if(parent.toString().contains("SystemMessage")) {
            buildSystemMessage(parent, handler);
        } else {
            String msg = "Unknown Object Type: " + parent.getClass().getName() + " for Sage Object: " + parent;
            handler.handleError(msg, new Exception(msg));
        }
    }

    private void buildSimpleData(String name, Object data, BuilderHandler handler) {
        handler.handleField(makeName(name), data);
    }

    public void buildFile(String name, File parent, BuilderHandler handler) {
        try {
            buildSimpleData(name, parent.getCanonicalPath(), handler);
        } catch (IOException e) {
            buildSimpleData(name, parent.getAbsolutePath(), handler);
        }
    }

    public void buildChannel(Object parent, BuilderHandler handler) throws Exception {
        buildObject("Channel", ChannelAPI.class, parent, handler, new String[] {"GetChannelLogo"});
    }

    public void buildFavorite(Object parent, BuilderHandler handler) throws Exception {
        buildObject("Favorite", FavoriteAPI.class, parent, handler, null);
    }

    private void buildSystemMessage(Object parent, BuilderHandler handler) throws Exception {
        buildObject("SystemMessage", SystemMessageAPI.class, parent, handler, new String[] {"DeleteSystemMessage"});
    }

    public void buildAlbum(Object parent, BuilderHandler handler) throws Exception {
        buildObject("Album", AlbumAPI.class, parent, handler, new String[] {"GetAlbumArt", "GetAlbumTracks"});
    }

    public void buildAiring(Object parent, BuilderHandler handler) throws Exception {
        buildObject("Airing", AiringAPI.class, parent, handler, new String[] {"GetChannel", "GetMediaFileForAiring", "GetAiringOnAfter", "GetAiringOnBefore"});
    }

    public void buildShow(Object parent, BuilderHandler handler) throws Exception {
        buildObject("Show", ShowAPI.class, parent, handler, new String[] {"GetShowSeriesInfo"});
    }

    public void buildMediaFile(Object parent, BuilderHandler handler) throws Exception {
        buildObject("MediaFile", MediaFileAPI.class, parent, handler, new String[] {"GetFullImage", "GetThumbnail", "GetStartTimesForSegments"});
    }

    public void buildPlaylist(Object parent, BuilderHandler handler) throws Exception {
        buildObject("Playlist", PlaylistAPI.class, parent, handler, null);
    }

    public void buildObject(String objectName, Class staticObjectClass, Object parent, BuilderHandler handler, String[] ignoreMethods) throws Exception {
        Method methods[] = getMethods(staticObjectClass, ignoreMethods);
        handler.beginObject(makeName(objectName));
        for (Method m : methods) {
            try {
                Object result = m.invoke(null, parent);
                build(m.getName(), result, handler, false);
            } catch (Exception e) {
                handler.handleError("Failed while Calling "+objectName+" Method: " + m.getName() + " on Object: " + objectName + "; (Sage Object: "+ parent+")", e);
            }
        }
        handler.endObject(makeName(objectName));
    }
    

    private Method[] getMethods(Class klass, String[] ignoreMethods) {
        Method m[] = reflectionMap.get(klass);
        
        if (m==null) {
            List<Method> methods = new LinkedList<Method>();
            m = klass.getDeclaredMethods();
            for (Method meth : m) {
                if (meth.getName().startsWith("Get") || meth.getName().startsWith("Is")) {
                    Class p[] = meth.getParameterTypes();
                    if (p!=null && p.length==1 && p[0].equals(Object.class) &&!ignoreMethod(meth.getName(), ignoreMethods)) {
                        methods.add(meth);
                    }
                }
            }
            m = methods.toArray(new Method[methods.size()]);
            reflectionMap.put(klass, m);
        }
        
        return m;
    }

    private boolean ignoreMethod(String name, String[] ignoreMethods) {
        if (ignoreMethods==null) return false;
        for (String s : ignoreMethods) {
            if (name.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void buildArray(String name, Object[] parent, BuilderHandler handler) throws Exception {
        handler.beginArray(makeName(name), parent.length);
        for (Object o : parent) {
            build(name, o, handler, true);
        }
        handler.endArray(makeName(name));
    }

    public void buildCollection(String name, Collection parent, BuilderHandler handler) throws Exception {
        handler.beginArray(makeName(name), parent.size());
        for (Object o : parent) {
            build(name, o, handler, true);
        }
        handler.endArray(makeName(name));
    }
    
    public String makeName(String name) {
        if (name!=null) name=name.replaceFirst("^Get", "");
        return name;
    }
}
