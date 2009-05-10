package org.jdna.media;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class StackedMediaFolder extends DecoratedMediaFolder {
    private static final Logger  log        = Logger.getLogger(StackedMediaFolder.class);
    private IMediaStackModel     stackModel = null;
    private List<IMediaResource> members    = null;

    public StackedMediaFolder(IMediaFolder orig, IMediaStackModel stackModel) {
        super(orig);
        this.stackModel=stackModel;
    }

    @Override
    public IMediaResource getParent() {
        IMediaResource parent = super.getParent();
        if (parent==null) return null;
        if (parent instanceof StackedMediaFolder) return parent;
        return new StackedMediaFolder((IMediaFolder) parent, stackModel);
    }

    @Override
    public List<IMediaResource> members() {
        if (members!=null) return members;
        
        List<IMediaResource> curMembers = getUndecoratedFolder().members();
        if (curMembers == null || curMembers.size() == 0) {
            log.debug("Cannot get stacked members for non empty Folder: " + getLocationUri());
            return members;
        }

        List<IMediaResource> tmpList  = new LinkedList<IMediaResource>(curMembers);
        List<IMediaResource> stackedList = new LinkedList<IMediaResource>();

        // important to sore the list, or else the stacking will not work.
        Collections.sort(tmpList);
        
        String lastTitle = null;
        String curTitle = null;
        lastTitle = curTitle;

        IMediaResource mr = null;
        
        // apply stacking against the list using the stacking model
        
        for (int i=0;i<tmpList.size();i++) {
            IMediaResource res = tmpList.get(i);
            curTitle = stackModel.getStackedTitle(res);
            
            if (res.getType() == IMediaResource.Type.Folder) {
                // add stacking to our children
                stackedList.add(new StackedMediaFolder((IMediaFolder) res, stackModel));
                continue;
            }
            
            if (curTitle != null && curTitle.equals(lastTitle)) {
                mr = stackedList.get(stackedList.size() - 1);
                if (mr instanceof StackedMediaFile) {
                    ((StackedMediaFile)mr).addMediaFile((IMediaFile) res);
                } else {
                    mr = new StackedMediaFile(curTitle, (IMediaFile) mr);
                    ((StackedMediaFile)mr).addMediaFile((IMediaFile)res);
                    stackedList.set(stackedList.size() - 1, mr);
                }
            } else {
                lastTitle = curTitle;
                stackedList.add(res);
            }
        }
        
        members = stackedList;

        return members;
    }
}
