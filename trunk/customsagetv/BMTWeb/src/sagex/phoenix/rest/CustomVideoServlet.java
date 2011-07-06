package sagex.phoenix.rest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.VirtualMediaFile;
import sagex.phoenix.vfs.custom.CustomFolder;
import sagex.phoenix.vfs.custom.CustomFolders;

public class CustomVideoServlet extends HttpServlet {
	private transient Logger log = Logger
			.getLogger(CustomVideoServlet.class);

	private static final long serialVersionUID = 1L;

	public CustomVideoServlet() {
	}

	private String getArg(HttpServletRequest req, HttpServletResponse resp, String arg) throws IOException {
		String value = req.getParameter(arg);
		if (value == null) {
			resp.sendError(404, "Missing '"+arg+"' Arg");
			return null;
		}
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cmd = req.getParameter("cmd");
		if ("list".equals(cmd)) {
			listFolders(req, resp);
			return;
		}
		
		String folder = getArg(req, resp, "folder");
		if (folder == null) {
			return;
		}
		
		String folderTitle = req.getParameter("folderTitle");
		if (StringUtils.isEmpty(folderTitle)) {
			folderTitle=folder;
		}
		
		String url = getArg(req, resp, "url");
		if (url == null) {
			return;
		}

		String title = getArg(req, resp, "title");
		if (title == null) {
			return;
		}
		
		String poster = getArg(req, resp, "poster");
		if (poster == null) {
			return;
		}

		CustomFolders folders= new CustomFolders();
		CustomFolder f = (CustomFolder) folders.getChildById(folder);
		if (f==null) {
			// need to add the folder
			f = folders.newCustomFolder(folder, folderTitle);
		}
		
		VirtualMediaFile vmf = new VirtualMediaFile(f, url, url, title);
		f.addMediaResource(vmf);
		resp.getWriter().println("{result: true}");
	}

	private void listFolders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter pw = resp.getWriter();
		CustomFolders folders = new CustomFolders();
		pw.println("[");
		for (IMediaResource r: folders) {
			pw.printf("{id: '%s', title: '%s'}\n", r.getId(), r.getTitle());
		}
		pw.println("]");
	}
}
