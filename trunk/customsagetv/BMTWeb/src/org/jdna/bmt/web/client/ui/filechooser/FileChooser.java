package org.jdna.bmt.web.client.ui.filechooser;

import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileChooser extends Composite implements HasValue<String> {
    private boolean                        dirsOnly        = false;
    private TextBox                        fileSelection;
    private ListBox                        dirs;

    private final FileChooserServicesAsync chooserServices = GWT.create(FileChooserServices.class);

    public FileChooser(String base) {
        this(base, false);
    }
    
    public FileChooser(String base, boolean dirOnly) {
        this.dirsOnly = dirOnly;
        this.fileSelection = new TextBox();
        this.fileSelection.setValue(base);

        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.setSpacing(5);
        vp.add(fileSelection);
        fileSelection.setWidth("100%");
        fileSelection.setReadOnly(true);
        vp.setCellWidth(fileSelection, "100%");
        
        dirs = new ListBox(false);
        dirs.setVisibleItemCount(10);
        dirs.setWidth("250px");
        dirs.setHeight("100%");
        dirs.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String sel = dirs.getValue(dirs.getSelectedIndex());
                String selName = dirs.getItemText(dirs.getSelectedIndex());
                if (selName.startsWith("[dir]")) {
                    chooserServices.listFiles(sel, new AsyncCallback<JSFileResult>() {
                        public void onFailure(Throwable caught) {
                            Log.error("Failed to get Files!", caught);
                        }
    
                        public void onSuccess(JSFileResult result) {
                            updateTree(result);
                        }
                    });
                } else {
                    fileSelection.setValue(sel);
                }
            }
        });
        
        vp.add(dirs);
        
        initWidget(vp);
        setWidth("100%");
        initTree(fileSelection.getValue());
    }
    

    private void initTree(String base) {
        chooserServices.listFiles(base, new AsyncCallback<JSFileResult>() {
            public void onFailure(Throwable caught) {
                Log.error("Failed to get Files!", caught);
            }

            public void onSuccess(JSFileResult result) {
                updateTree(result);
            }
        });
    }

    private void updateTree(JSFileResult result) {
        dirs.clear();

        fileSelection.setValue(result.getDir().getPath());
        
        addItem(new JSFile(result.getDir().getPath(), ".", true));
        addItem(new JSFile(result.getParent().getPath(), "..", true));

        for (JSFile f : result.children) {
            if (dirsOnly && !f.isDirectory()) continue;
            addItem(f);
        }
    }

    private void addItem(JSFile file) {
        if (file.isDirectory()) {
            dirs.addItem("[dir] " + file.getName(), file.getPath());
        } else {
            dirs.addItem(file.getName(), file.getPath());
        }
    }

    public String getValue() {
        return fileSelection.getValue();
    }

    public void setValue(String value) {
        fileSelection.setValue(value);
    }

    public void setValue(String value, boolean fireEvents) {
        fileSelection.setValue(value, fireEvents);
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return fileSelection.addValueChangeHandler(handler);
    }

    @Override
    protected void onLoad() {
        dirs.setWidth(fileSelection.getOffsetWidth() + "px");
    }
}
