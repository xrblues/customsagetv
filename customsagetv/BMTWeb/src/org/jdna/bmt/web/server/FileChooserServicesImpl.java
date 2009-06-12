package org.jdna.bmt.web.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.ui.filechooser.FileChooserServices;
import org.jdna.bmt.web.client.ui.filechooser.JSFile;
import org.jdna.bmt.web.client.ui.filechooser.JSFileResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class FileChooserServicesImpl extends RemoteServiceServlet implements FileChooserServices {
    public FileChooserServicesImpl() {
        ServicesInit.init();
    }

    public JSFileResult listFiles(String base) {
        System.out.println("listFiles("+base+")");
        File f = null;
        if (base==null || base.trim().length()==0) {
            f = new File(".");
        } else {
            f = new File(base);
            if (!f.exists()) {
                System.out.println("Does not exist: " + base);
                f = new File(".");
            }
        }
        try {
            if (!f.isDirectory()) f = f.getCanonicalFile().getParentFile();
            System.out.println("listFiles("+f.getAbsolutePath()+")");
            return listFiles(toJSFile(f));
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    public JSFileResult listFiles(JSFile base) {
        File f = new File(base.getPath());
        if (!f.exists()) return null;
        if (!f.isDirectory()) return null;
        List<JSFile> files = new ArrayList<JSFile>();
        for (File f1 : f.listFiles()) {
            files.add(toJSFile(f1));
        }
        if (files.size()==0) return null;
        return new JSFileResult(toJSFile(new File(base.getPath()).getParentFile()), base, files.toArray(new JSFile[files.size()]));
    }
    
    private JSFile toJSFile(File in) {
        return new JSFile(in.getAbsolutePath(), in.getName(), in.isDirectory());
    }
}
