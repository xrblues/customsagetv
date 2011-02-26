package org.jdna.bmt.web.client.util;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NullCallback<T> implements AsyncCallback<T> {
	public NullCallback() {
	}

	@Override
	public void onFailure(Throwable caught) {
		Application.fireErrorEvent("Failed", caught);
	}

	@Override
	public void onSuccess(T result) {
	}
}
