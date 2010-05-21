package org.jdna.bmt.web.client.ui.filechooser;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("filechooser")
public interface FileChooserServices extends RemoteService {
    public JSFileResult listFiles(String base);
    public JSFileResult listFiles(JSFile base);
    public JSFileResult listRoots();
}
