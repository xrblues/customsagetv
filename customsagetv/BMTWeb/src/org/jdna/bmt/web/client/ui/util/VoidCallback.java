package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class VoidCallback<T> implements AsyncCallback<T> {
	public VoidCallback() {
	}

	@Override
	public void onFailure(Throwable caught) {
	}

	@Override
	public void onSuccess(T result) {
	}
}
