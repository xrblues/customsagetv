package org.jdna.bmt.web.client.ui.filechooser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FileChooserServicesAsync {
    public void listFiles(String base, AsyncCallback<JSFileResult> result);
    public void listFiles(JSFile base, AsyncCallback<JSFileResult> result);
    public void listRoots(AsyncCallback<JSFileResult> result);
}
