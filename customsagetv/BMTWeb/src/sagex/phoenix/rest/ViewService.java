package sagex.phoenix.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import sagex.phoenix.Phoenix;
import sagex.phoenix.vfs.views.ViewFactory;

public class ViewService extends JSONHandler {
	public static final String TAG = "tag";
	
	private static class ViewInfo {
		String id;
		String label;
	}

	public static final String ID = "views";
	private Help help;

	public ViewService() {
		help = new Help();
		help.title=ID;
		help.description="Request available views by tag";
		help.parameters.put(TAG, "View Tag (will return views that match the tag).  If 'all' is passed, then all views will be returned. If 'list' is passed, then all known tags are returned");
		help.examples.add("/views?tag=tv");
		help.examples.add("/views?tag=video");
		help.examples.add("/views?tag=all");
		help.examples.add("/views?tag=list");
	}

	@Override
	public Object handleService(String[] args, HttpServletRequest request) throws ServiceException {
		String tag = request.getParameter(TAG);
		if (StringUtils.isEmpty(tag)) throw new ServiceException("vfs_missingview", "Missing View");

		List<ViewInfo> views = new ArrayList<ViewInfo>();
		List<ViewFactory> l = null;
		if ("all".equals(tag)) {
			l = Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactories();
		} else if ("list".equals(tag)) {
			Set<String> tags = phoenix.umb.GetTags(false);
			for (String s: tags) {
				ViewInfo vi = new ViewInfo();
				vi.id = s;
				vi.label = phoenix.umb.GetTagLabel(s);
				views.add(vi);
			}
			return views;
		} else {
			l = new ArrayList<ViewFactory>(Phoenix.getInstance().getVFSManager().getVFSViewFactory().getFactories(tag, false));
		}
		
		for (ViewFactory v : l) {
			ViewInfo vi = new ViewInfo();
			vi.id = v.getName();
			vi.label = v.getLabel();
			views.add(vi);
		}
		return views;
	}

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
