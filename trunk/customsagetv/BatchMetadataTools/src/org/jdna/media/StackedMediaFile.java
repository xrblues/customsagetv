package org.jdna.media;

import java.util.LinkedList;
import java.util.List;

public class StackedMediaFile extends DecoratedMediaFile {
    private List<IMediaFile> stackedFiles = new LinkedList<IMediaFile>();
    private String title = null;
    
    public StackedMediaFile(String title, IMediaFile file) {
        super(file);
        stackedFiles.add(file);
        this.title=title;
    }
    
    public void addMediaFile(IMediaFile mf) {
        stackedFiles.add(mf);
    }
    
    public List<IMediaFile> getStackedFiles() {
        return stackedFiles;
    }

    @Override
    public void accept(IMediaResourceVisitor visitor) {
        for (IMediaFile f : stackedFiles) {
            f.accept(visitor);
        }
    }

    @Override
    public void delete() {
        for (IMediaFile f : stackedFiles) {
            f.delete();
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void touch() {
        for (IMediaFile f : stackedFiles) {
            f.delete();
        }
    }
}
