package org.jdna.bmt.web.client.util;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NotificationCallback<T> implements AsyncCallback<T> {
	public String error;
	private String ok;
	
	public NotificationCallback(String error) {
		this.error=error;
	}

	public NotificationCallback(String error, String ok) {
		this.ok=ok;
		this.error=error;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		Application.fireErrorEvent(error, caught);
	}

	@Override
	public void onSuccess(T result) {
		Application.fireNotification(ok);
	}
}
