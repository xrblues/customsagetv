package org.jdna.bmt.web.server;

import sagex.phoenix.progress.BasicProgressMonitor;

public class NotificationCompleteHandler extends BasicProgressMonitor {
	private String completeMessage;
	private String cancelledMessage;

	public NotificationCompleteHandler(String completeMessage) {
		this(completeMessage, null);
	}
	
	public NotificationCompleteHandler(String completeMessage, String cancelledMessage) {
		super();
		this.completeMessage=completeMessage;
		this.cancelledMessage=cancelledMessage;
	}

	@Override
	public void done() {
		super.done();
		NotificationManager.getInstance().addInfo(completeMessage);
	}

	@Override
	public void setCancelled(boolean cancel) {
		super.setCancelled(cancel);
		if (cancelledMessage!=null) {
			NotificationManager.getInstance().addError(cancelledMessage);
		}
	}
}
