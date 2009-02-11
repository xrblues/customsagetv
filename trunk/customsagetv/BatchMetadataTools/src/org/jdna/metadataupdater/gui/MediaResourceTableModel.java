package org.jdna.metadataupdater.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.MediaMetadataUtils;

public class MediaResourceTableModel extends AbstractTableModel {
    private List<IMediaResource> resources = new ArrayList<IMediaResource>();
    private static final String colNames[] = {"Title", "Location"};
    
    public MediaResourceTableModel() {
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return resources.size();
    }

    /**
     * To get the "real" IMediaResource object, pass -1 as the column value
     */
    public Object getValueAt(int r, int c) {
        IMediaResource res = resources.get(r);
        if (c==0) {
            return MediaMetadataUtils.cleanSearchCriteria(res.getName(), false);
        } else if (c==1) {
            return res.getLocationUri();
        } else {
            return res;
        }
    }

    @Override
    public String getColumnName(int c) {
        return colNames[c];
    }

    public void add(IMediaResource resource) {
        resources.add(resource);
        fireTableRowsInserted(resources.size(), resources.size());
    }
}
