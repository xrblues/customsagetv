package org.jdna.media;

public interface IAsyncHandler<T> {
	void onFailure(Throwable caught);
	void onSuccess(T result);
}
