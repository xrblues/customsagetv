package org.jdna.bmt.web.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.filechooser.FileChooserServices;
import org.jdna.bmt.web.client.ui.filechooser.JSFile;
import org.jdna.bmt.web.client.ui.filechooser.JSFileResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class FileChooserServicesImpl extends RemoteServiceServlet implements FileChooserServices {
    private static final Logger log = Logger.getLogger(FileChooserServicesImpl.class);
    public FileChooserServicesImpl() {
        ServicesInit.init();
    }

    public JSFileResult listFiles(String base) {
        log.debug("listFiles("+base+")");
        File f = null;
        if (base==null || base.trim().length()==0) {
            return listRoots();
        } else {
            f = new File(base);
            if (!f.exists()) {
                return listRoots();
            }
        }
        
        try {
            if (!f.isDirectory()) {
                f = f.getCanonicalFile().getParentFile();
            }
            
            if (f==null || !f.exists()) {
                return listRoots();
            }
            log.debug("listFiles("+f.getAbsolutePath()+")");
            return listFiles(toJSFile(f));
        } catch (Throwable t) {
            log.error("listFiles failed!", t);
            throw new RuntimeException(t);
        }
    }

    public JSFileResult listRoots() {
        List<JSFile> files = new ArrayList<JSFile>();
        for (File f : File.listRoots()) {
            files.add(toJSFile(f));
        }
        return new JSFileResult(null, ROOT(), files.toArray(new JSFile[files.size()]));
    }

    public JSFileResult listFiles(JSFile base) {
        if (base==null) {
            return listRoots();
        }
        
        File f = new File(base.getPath());
        if (!f.exists()) return listRoots();
        if (!f.isDirectory()) return listRoots();
        
        List<JSFile> files = new ArrayList<JSFile>();
        for (File f1 : f.listFiles()) {
            files.add(toJSFile(f1));
        }
        //if (files.size()==0) return null;
        return new JSFileResult(toJSFile(new File(base.getPath()).getParentFile()), base, files.toArray(new JSFile[files.size()]));
    }
    
    private JSFile toJSFile(File in) {
        if (in==null) {
            return ROOT();
        } else {
            return new JSFile(in.getAbsolutePath(), in.getName(), in.isDirectory());
        }
    }
    
    private JSFile ROOT() {
        JSFile f =  new JSFile("Root FileSystems", "/", true);
        f.setRoot(true);
        return f;
    }
}
