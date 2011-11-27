package org.jdna.bmt.web.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.Version;
import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.app.GlobalService;

import sagex.api.Configuration;
import sagex.phoenix.progress.BasicProgressMonitor;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.sage.SageSourcesMediaFolder;
import sagex.phoenix.vfs.sources.SageSourcesFactory;
import sagex.phoenix.vfs.views.ViewFolder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GlobalServicesImpl extends RemoteServiceServlet implements GlobalService {
	private transient Logger log = Logger.getLogger(GlobalServicesImpl.class);

	private transient Timer timer = new Timer("BatchOperations");

	private static final long serialVersionUID = 1L;

	public GlobalServicesImpl() {
	}

	@Override
	public ArrayList<Notification> getNotices() {
		return NotificationManager.getInstance().getNotices();
	}

	@Override
	public void batchOperation(GWTMediaFolder folder, BatchOperation op) {
		batchOperation(BrowsingServicesImpl.getFolderRef(folder, getThreadLocalRequest()), op);
	}

	@Override
	public void batchOperation(BatchOperation op) {
		IMediaFolder vf = phoenix.umb.CreateView("phoenix.view.bmt.allvideos");
		if (vf==null||vf.getChildren().size()==0) {
			vf = new SageSourcesMediaFolder("TVL", "All Files");
		}
		batchOperation(vf, op);
	}

	public void batchOperation(final IMediaResource res, final BatchOperation op) {
		try {
			log.info("Running Batch Operation " + op.getLabel());
			final IMediaResourceVisitor vis = createVisitor(op);
			final IProgressMonitor monitor = new BasicProgressMonitor();

			if (op.isBackground()) {
				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						res.accept(vis, monitor, IMediaResource.DEEP_UNLIMITED);
						if (op.getCompleteMessage()!=null) {
							NotificationManager.getInstance().addInfo(op.getCompleteMessage());
						}
					}
				};
				timer.schedule(tt, 0);
			} else {
				res.accept(vis, monitor, IMediaResource.DEEP_UNLIMITED);
				if (op.getCompleteMessage()!=null) {
					NotificationManager.getInstance().addInfo(op.getCompleteMessage());
				}
			}
		} catch (Exception e) {
			// reset the timer if we get an error, since it somehow appear to think it's cancelled.
			timer.cancel();
			timer = new Timer("BatchOperations");
			log.warn("Batch Operation Failed!", e);
			NotificationManager.getInstance().addError("Failed to create video list for operation; Reason: " + e.getMessage());
		}

	}

	public static IMediaResourceVisitor createVisitor(BatchOperation op) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		Class<IMediaResourceVisitor> visClass = (Class<IMediaResourceVisitor>) Class.forName(op.getVisitorClass());
		if (op.getVisitorArg() == null) {
			return visClass.newInstance();
		} else {
			Constructor<IMediaResourceVisitor> c = visClass.getConstructor(op.getVisitorArg().getClass());
			return c.newInstance(op.getVisitorArg());
		}
	}

	@Override
	public boolean showAboutDialog() {
		String versionOld = Configuration.GetServerProperty("bmt/web/lastVersion", "0");
		String versionCur = Version.VERSION;
		
		if (!phoenix.util.IsAtLeastVersion(versionOld, versionCur)) {
			log.debug("Updating version to " + Version.VERSION);
			Configuration.SetServerProperty("bmt/web/lastVersion", Version.VERSION);
			Configuration.SaveProperties();
			return true;
		}
		
		return false;
	}
}
