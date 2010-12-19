package org.jdna.bmt.web.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.app.GlobalService;

import sagex.phoenix.progress.BasicProgressMonitor;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;

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
		batchOperation(phoenix.umb.CreateView("allvideos"), op);
	}

	public void batchOperation(final IMediaResource res, final BatchOperation op) {
		try {
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
	
}
