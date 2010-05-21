package org.jdna.bmt.web.client.ui.util;

public interface DialogHandler<T> {
    public void onCancel();
    public void onSave(T data);
}
