package org.jdna.bmt.web.client.ui.filechooser;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
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

        DockPanel dp = new DockPanel();
        dp.setSpacing(5);
        dp.setWidth("100%");
        
        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.setSpacing(5);
        vp.add(fileSelection);
        fileSelection.setWidth("100%");
        fileSelection.setReadOnly(false);
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
                            Application.fireErrorEvent(Application.messages().failedToGetFiles(), caught);
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
        
        VerticalPanel buttons = new VerticalPanel();
        vp.setSpacing(5);
        
        Button list = new Button(Application.labels().listRoots(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                chooserServices.listFiles((String)null, new AsyncCallback<JSFileResult>() {
                    public void onFailure(Throwable caught) {
                        Application.fireErrorEvent(Application.messages().failedToGetFiles(), caught);
                    }

                    public void onSuccess(JSFileResult result) {
                        updateTree(result);
                    }
                });
            }
        });
        list.setWidth("100%");

        Button sage = new Button(Application.labels().sageHome(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                chooserServices.listFiles(".", new AsyncCallback<JSFileResult>() {
                    public void onFailure(Throwable caught) {
                        Application.fireErrorEvent(Application.messages().failedToGetFiles(), caught);
                    }

                    public void onSuccess(JSFileResult result) {
                        updateTree(result);
                    }
                });
            }
        });
        sage.setWidth("100%");
        
        buttons.add(list);
        buttons.add(sage);
        
        dp.add(buttons, DockPanel.WEST);
        
        dp.add(vp, DockPanel.CENTER);
        initWidget(dp);
        setWidth("100%");
        initTree(fileSelection.getValue());
    }
    

    private void initTree(String base) {
        chooserServices.listFiles(base, new AsyncCallback<JSFileResult>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent(Application.messages().failedToGetFiles(), caught);
            }

            public void onSuccess(JSFileResult result) {
                updateTree(result);
            }
        });
    }

    private void updateTree(JSFileResult result) {
        dirs.clear();

        fileSelection.setValue(result.getDir().getPath());

        if (!result.getDir().isRoot()) {
            addItem(new JSFile(result.getParent().getPath(), "..", true));
        }

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
