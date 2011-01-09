package sagex.phoenix.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import sagex.UIContext;
import sagex.api.AiringAPI;
import sagex.api.ChannelAPI;
import sagex.api.Global;
import sagex.api.ShowAPI;
import sagex.api.WidgetAPI;
import sagex.api.enums.SageCommandEnum;
import sagex.phoenix.cache.ICache;
import sagex.phoenix.cache.MapCache;
import sagex.phoenix.metadata.ICastMember;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.views.ViewFolder;

public class VFSService extends JSONHandler {
	private static final String CONTEXT = "ctx";
	private static final String CMD = "cmd";
	private static final String CMD_PLAY = "play";
	private static final String CMD_REFRESH = "refresh";
	private static final String VIEW = "view";
	private static final String VIEW_ITEM = "item";
	private static final String START = "start";
	private static final String SIZE = "end";
	
	private static interface Base {
		public static final String type = "type";
		public static final String id = "id";
		public static final String title = "title";
	}
	
	private static interface Item extends Base {
		public static String is_watched = "is_watched";
		public static String year = "year";
		public static String aired = "aired";
	}

	private static interface TVItem extends Item {
		public static String episode_title = "episode_title";
		public static String season = "season";
		public static String episode = "episode";
		public static String channel = "channel";
		public static String network = "network";
		public static String is_first = "is_first";
		public static String is_fav = "is_fav";
		public static String TYPE_TV = "tv";
		public static String is_movie = "is_movie";
		public static String airingid = "airingid";
	}
	
	private static interface VideoItem {
		public static String ishd = "ishd";
		public static String genres = "genres";
	}

	private static interface Folder extends Base {
		public static String start = "start";
		public static String size = "size";
		public static String items = "items";
		public static String hints = "hints";
		public static String TYPE_FOLDER = "folder";
	}
	
	private static class FullItem implements TVItem {
		public static final String actors = "actors";
		public static final String description = "description";
		public static final String directors = "directors";
		public static final String writers = "writers";
		public static final String duration = "duration";
		public static final String extended_ratings = "extended_ratings";
		public static final String rated = "rated";
	}

	private Map newFolder() {
		Map map = new HashMap();
		map.put(Folder.type, Folder.TYPE_FOLDER);
		map.put(Folder.items, new ArrayList());
		return map;
	}
	
	private Map newTVItem() {
		Map map = new HashMap();
		map.put(TVItem.type, TVItem.TYPE_TV);
		return map;
	}
	
	
	private Map newItem() {
		Map map = new HashMap();
		return map;
	}
	
	public static final String ID = "vfs";
	
	private Map<String,String> osdMenus = new HashMap<String, String>();
	private static final String DEFAULT_OSD_MENU = "SageTV7";
	
	private ICache<IMediaFolder> viewCache = new MapCache<IMediaFolder>(5*1000);
	private Help help;
	public VFSService() {
		help = new Help();
		help.title=ID;
		help.description = "Fetches View Media Items using the Phoenix VFS";
		help.parameters.put(CMD, "VFS Command. Values are: play, refresh");
		help.parameters.put(CONTEXT, "Sage UI Context to use when playing a mediafile");
		help.parameters.put(VIEW, "VFS View (required)");
		help.parameters.put(VIEW_ITEM, "Path to view item");
		help.parameters.put(START, "The starting item to fetch when fetching a view folder. (ie, there may be 200 items, but you want to start at 100)");
		help.parameters.put(SIZE, "The number of items to return with this request.  (ie, there may be 200 items, but you only want to return the first 10)");
		help.examples.add("/vfs?view=recordings&item=House");
		help.examples.add("/vfs?view=recordings&item=House&cmd=play&ctx=123456");
		
		osdMenus.put("Phoenix", "Phoenix MediaPlayer OSD");
		osdMenus.put(DEFAULT_OSD_MENU, "MediaPlayer OSD");
	}

	@Override
	public Object handleService(String[] args, HttpServletRequest request) throws ServiceException {
		String cmd = request.getParameter(CMD);
		if (CMD_REFRESH.equals(cmd)) {
			viewCache.clear();
			return new Message(0, "caches are cleared");
		}

		String view = request.getParameter(VIEW);
		if (StringUtils.isEmpty(view)) throw new ServiceException("vfs_missingview", "Missing View");
		
		IMediaFolder viewFolder = getView(view);
		String item = request.getParameter(VIEW_ITEM);
		int start = NumberUtils.toInt(request.getParameter(START), 0);
		int size = NumberUtils.toInt(request.getParameter(SIZE), 50);
		
		if (StringUtils.isEmpty(item)) {
			return convertResource(viewFolder, start, size);
		}
		
		IMediaResource res = viewFolder.findChild(item);
		if (res==null) {
			throw new ServiceException("vfs_invalid_item", "Invalid VFS Item " + item + " for view " + view);
		}

		if (res instanceof IMediaFile) {
			if (CMD_PLAY.equals(cmd)) {
				// play the item
				String ctx = request.getParameter(CONTEXT);
				if (StringUtils.isEmpty(ctx)) {
					throw new ServiceException("vfs_missing_context", "Missing UI Context");
				}
				try {
					UIContext uictx = new UIContext(ctx);

					String name = WidgetAPI.GetSTVName(uictx);
					log.debug("STVNAME: " + name);
					if (name==null) {
						name = DEFAULT_OSD_MENU;
					}
					
					String menu = osdMenus.get(name);
					if (menu==null) {
						menu = osdMenus.get(DEFAULT_OSD_MENU);
					}

					log.info("Playing File: " + res.getTitle() + " on " + ctx + " using Menu " + menu);
					phoenix.umb.Play(res, ctx);
					Thread.sleep(500);
					WidgetAPI.LaunchMenuWidget(uictx, menu);
					Thread.sleep(500);
					Global.SageCommand(uictx, SageCommandEnum.TV.command());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new Message(0, "Playing MediaFile");
			} else {
				// just return the item
				return fullItem((IMediaFile) res);
			}
		}
		
		return convertResource(res, start, size);
	}

	private Map fullItem(IMediaFile file) throws ServiceException {
		Map item = convertItem(file);
		
		IMetadata md = file.getMetadata();
		item.put(FullItem.actors, fillCast(md.getActors()));
		item.put(FullItem.directors, fillCast(md.getDirectors()));
		item.put(FullItem.writers, fillCast(md.getDirectors()));
		item.put(FullItem.aired, file.getStartTime());
		item.put(FullItem.description, md.getDescription());
		item.put(FullItem.duration, md.getDuration());
		item.put(FullItem.extended_ratings, md.getExtendedRatings());
		item.put(FullItem.rated, md.getRated());
		item.put(VideoItem.ishd, file.isType(MediaResourceType.HD.value()));
		item.put(VideoItem.genres, md.getGenres());
		
		if (file.isType(MediaResourceType.RECORDING.value()) || file.isType(MediaResourceType.EPG_AIRING.value())) {
			Object sage = phoenix.media.GetSageMediaFile(file);
			item.put(TVItem.is_first, ShowAPI.IsShowFirstRun(sage));
			item.put(TVItem.is_fav, AiringAPI.IsFavorite(sage));
			if (md.getExternalID()!=null&&md.getExternalID().startsWith("MV")) {
				item.put(TVItem.is_movie,true);
			}
			item.put(TVItem.airingid, AiringAPI.GetAiringID(sage));
		}
		
		return item;
	}
	
	public List fillCast(List<ICastMember> cast) {
		List to = new ArrayList();
		for (ICastMember c : cast) {
			Map m = new HashMap();
			m.put("name", c.getName());
			m.put("role", c.getRole());
			to.add(m);
		}
		return to;
	}

	private IMediaFolder getView(String view) throws ServiceException {
		IMediaFolder folder = viewCache.get(view);
		if (folder==null) {
			folder = phoenix.umb.CreateView(view);
			if (folder==null) {
				throw new ServiceException("vfs_unknownview", "Unknown View " + view);
			}
			viewCache.put(view, folder);
		}
		return folder;
	}
	
	private Object convertResource(IMediaResource res, int start, int size) throws ServiceException {
		if (res instanceof IMediaFile) {
			return convertItem((IMediaFile) res);
		} else {
			return convertFolder((IMediaFolder)res, start, size);
		}
	}

	private Map convertItem(IMediaFile file) throws ServiceException {
		IMetadata md = file.getMetadata();
		Map item = null;

		if (file.isType(MediaResourceType.TV.value())) {
			Map tvitem = newTVItem();
			tvitem.put(TVItem.type, TVItem.TYPE_TV);
			tvitem.put(TVItem.episode_title, md.getEpisodeName());
			tvitem.put(TVItem.season,md.getSeasonNumber());
			tvitem.put(TVItem.episode,md.getEpisodeNumber());
			if (file.isType(MediaResourceType.RECORDING.value())) {
				Object channel = AiringAPI.GetChannel(phoenix.media.GetSageMediaFile(file));
				if (channel!=null) {
					tvitem.put(TVItem.channel, ChannelAPI.GetChannelNumber(channel));
					tvitem.put(TVItem.network, ChannelAPI.GetChannelNetwork(channel));
				}
			}
			item = tvitem;
		} else if (file.isType(MediaResourceType.EPG_AIRING.value())) {
			item = newItem();
			item.put(TVItem.type, "epg");
			item.put(TVItem.episode_title, md.getEpisodeName());
			Object channel = AiringAPI.GetChannel(phoenix.media.GetSageMediaFile(file));
			if (channel!=null) {
				item.put(TVItem.channel, ChannelAPI.GetChannelNumber(channel));
				item.put(TVItem.network, ChannelAPI.GetChannelNetwork(channel));
			}
		} else {
			item = newItem();
		}
		
		item.put(Item.aired, file.getStartTime());
		item.put(Item.id,getResourceId(file));
		item.put(Item.title,file.getTitle());
		item.put(Item.is_watched,file.isWatched());
		item.put(Item.year,md.getYear());
		return item;
	}


	private Map convertFolder(IMediaFolder mf, int start, int size) throws ServiceException {
		Map folder = newFolder();
		folder.put(Folder.id,getResourceId(mf));
		folder.put(Folder.title,mf.getTitle());

		if (mf instanceof ViewFolder) {
			folder.put(Folder.hints, ((ViewFolder)mf).getPresentation().getHints());
		}
		
		List<IMediaResource> list = mf.getChildren();
		if (start>list.size()) {
			throw new ServiceException("vfs_index_out_of_range","Item index out of range. Index: " + start + "; Size: " + list.size());
		}
		
		folder.put(Folder.start,start);
		folder.put(Folder.size , list.size());
		int end = Math.min(list.size(), start+size);
		
		List items = (List) folder.get(Folder.items);
		for (int i = start; i < end; i++) {
			IMediaResource r = list.get(i);
			if (r instanceof IMediaFile) {
				items.add(convertItem((IMediaFile)r));
			} else {
				// add a folder stub
				Map f = newFolder();
				f.put(Folder.id , getResourceId(r));
				f.put(Folder.title , r.getTitle());
				items.add(f);
			}
		}
		return folder;
	}

	public String getResourceId(IMediaResource r) {
		String id=r.getPath();
		if (id.startsWith("/")) id = id.substring(1);
		id = StringUtils.substringAfter(id, "/");
		return id;
	}

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
