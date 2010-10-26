package org.jdna.bmt.web.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.event.Notification.MessageType;

public class NotificationManager {
	private static final NotificationManager mgr = new NotificationManager();
	public static NotificationManager getInstance() {
		return mgr;
	}
	
	private Queue<Notification> notices = new LinkedList<Notification>();
	
	public void addNotification(Notification evt) {
		notices.add(evt);
	}
	
	public void addError(String msg) {
		addNotification(new Notification(MessageType.ERROR, msg));
	}

	public void addInfo(String msg) {
		addNotification(new Notification(MessageType.INFO, msg));
	}
	
	public ArrayList<Notification> getNotices() {
		ArrayList<Notification> arr = new ArrayList<Notification>();
		Notification ne = null;
		while ((ne = notices.poll())!=null) {
			arr.add(ne);
		}
		return arr;
	}
}
