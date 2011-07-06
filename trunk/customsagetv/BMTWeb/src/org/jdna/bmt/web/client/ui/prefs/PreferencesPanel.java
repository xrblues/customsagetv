package org.jdna.bmt.web.client.ui.prefs;


import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.animation.Effects;
import org.jdna.bmt.web.client.i18n.Labels;
import org.jdna.bmt.web.client.i18n.Msgs;
import org.jdna.bmt.web.client.ui.util.UpdatablePanel;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PreferencesPanel extends Composite {
    private static final int FADE_VALUE = 500;
    protected final Msgs msgs = Application.messages();
    protected final Labels labels = Application.labels();
    
    
    private static class EditorPanel extends Composite {
        DockPanel main = new DockPanel();
        Label header = new Label();
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button save = new Button();

        private Widget editor = null;
        private UpdatablePanel updatable = null;
        
        public EditorPanel() {
            main.setWidth("100%");
            
            header.addStyleName("bmt-PrefPanelTitle");
            main.add(header, DockPanel.NORTH);
            main.setWidth("100%");
            
            buttonPanel.setWidth("100%");
            save = new Button("Save", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    savePreferences();
                }
            });
            
            buttonPanel.add(save);
            buttonPanel.setCellHorizontalAlignment(save, HasHorizontalAlignment.ALIGN_RIGHT);
            buttonPanel.setVisible(false);
            main.add(buttonPanel, DockPanel.SOUTH);

            initWidget(main);
        }
        
        public void setEditorWidget(Widget widget) {
            if (editor!=null) {
                main.remove(editor);
            }
            
            buttonPanel.setVisible(false);
            
            if (widget!=null) {
                widget.getElement().getStyle().setOpacity(0.0);
                editor=widget;
                main.add(editor, DockPanel.CENTER);
                if (widget instanceof UpdatablePanel) {
                    this.updatable = (UpdatablePanel) widget;
                    buttonPanel.setVisible(!updatable.isReadonly());
                    header.setText(updatable.getHeader());
                    header.setTitle(updatable.getHelp());
                } else {
                    this.updatable = null;
                    header.setText("");
                }
                
                Effects.fadeIn(widget, FADE_VALUE);
            }
        }

        protected void savePreferences() {
            updatable.save(new AsyncCallback<UpdatablePanel>() {
                public void onFailure(Throwable caught) {
                    Application.fireErrorEvent(Application.messages().failedToSavePreferences(), caught);
                }

                public void onSuccess(UpdatablePanel result) {
                    setStatus(Application.messages().preferencesSaved());
                }
            });
        }
        
        public void setStatus(String msg) {
            Application.fireNotification(msg);
        }
    }

    private Tree                          tree               = new Tree();
    private DockPanel                     main               = new DockPanel();
    private TextBox                       search             = new TextBox();
    private PreferenceItemsPanel          prefItemsPanel     = new PreferenceItemsPanel();
    private EditorPanel editPanel = new EditorPanel();

    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);
    
    public PreferencesPanel() {
        main.setWidth("100%");
        main.setSpacing(10);
        tree.setAnimationEnabled(true);
        tree.addOpenHandler(new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> event) {
                tree.setSelectedItem(event.getTarget(), false);
                final TreeItem item = event.getTarget();
                if (item.getChildCount() == 1) {
                    TreeItem child = item.getChild(0);
                    if (child.getText() == null || child.getText().length() == 0) {
                        preferencesService.getPreferences((PrefItem) item.getUserObject(), new AsyncCallback<PrefItem[]>() {
                            public void onFailure(Throwable caught) {
                                caught.printStackTrace();
                                setStatus(caught.getMessage());
                            }

                            public void onSuccess(PrefItem[] result) {
                                updateTreeItems(item, result);
                            }
                        });
                    }
                }
            }
        });

        tree.addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                TreeItem ti = ((Tree) event.getSource()).getSelectedItem();
                focusTreeItem(ti);
            }
        });

        
        search.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode()==13) {
                    doSearch();
                }
            }
        });
        search.setText("Search...");
        search.addStyleName("SearchBox-Empty");
        search.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                search.setText("");
                search.removeStyleName("SearchBox-Empty");
            }
        });
        
        
        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("200px");
        vp.setSpacing(3);
        
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(4);
        hp.setWidth("100%");
        hp.add(search);
        hp.addStyleName("SearchBox");
        search.setWidth("100%");
        Image img = new Image("images/16x16/system-search.png");
        img.addStyleName("clickable");
        img.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (search.getValue()!=null && search.getValue().trim().length()>0) {
                    doSearch();
                }
            }
        });
        hp.add(img);
        hp.setCellVerticalAlignment(img, HasVerticalAlignment.ALIGN_MIDDLE);
        vp.add(hp);
        vp.add(tree);
        tree.setWidth("100%");
        main.add(vp, DockPanel.WEST);
        main.setCellHeight(vp, "200px");
        main.setCellWidth(vp, "200px");
        main.add(editPanel, DockPanel.CENTER);
        editPanel.setEditorWidget(prefItemsPanel);

        preferencesService.getPreferences(null, new AsyncCallback<PrefItem[]>() {
            public void onFailure(Throwable caught) {
                setStatus(caught.getMessage());
            }

            public void onSuccess(PrefItem[] result) {
                updateTreeItems(null, result);
            }
        });
        
        initWidget(main);
    }

    private void doSearch() {
        if (!StringUtils.isEmpty(search.getValue())) {
            preferencesService.searchPreferences(search.getValue(), new AsyncCallback<PrefItem>() {
                public void onFailure(Throwable caught) {
                    setStatus(caught.getMessage());
                }

                public void onSuccess(PrefItem result) {
                    if (result==null || result.getChildren()==null || result.getChildren().length==0) {
                        Application.fireErrorEvent(Application.messages().nothingFoundFor(search.getValue()));
                    } else {
                        updateSearch(result);
                    }
                }
            });
        }
    }

    protected void updateSearch(PrefItem result) {
        PreferenceItemsPanel panel = new PreferenceItemsPanel();
        panel.setPanelItems(result);
        editPanel.setEditorWidget(panel);
    }

    private void setStatus(String message) {
    }

    private void updateTreeItems(TreeItem item, PrefItem[] items) {
        // the child items in the user object of the tree item
        if (item != null) {
            ((PrefItem)item.getUserObject()).setChildren(items);
            item.removeItems();
        }
        
        for (PrefItem pi : items) {
            if (pi.isGroup()) {
                TreeItem ti = new TreeItem();
                ti.setText(pi.getLabel());
                ti.setUserObject(pi);
                ti.addItem(new TreeItem());
                if (item == null) {
                    tree.addItem(ti);
                } else {
                    item.addItem(ti);
                }
            }
        }
        
        // Add in faked configuration panels
        if (item == null) {
        	tree.addItem(createFakePanel("Refresh Configurations", "Refresh External Configuration Files", "refreshConfigurations"));
        	tree.addItem(createFakePanel("Sage Sources", "Add/Modify Sage Sources", "videoSourcesEditor"));
        	tree.addItem(createFakePanel("Sage Properties", "View Sage Properties (Server)", "viewSageProperties"));
        	tree.addItem(createFakePanel("Channels", "Configure Channels", "channels"));
        	tree.addItem(createFakePanel("SageTV Plugins", "Manage Plugins", "plugins"));
        	//tree.addItem(createFakePanel("Manage Menus", "Manage Menus", "menus"));
        	tree.addItem(createFakePanel("Data Browser", "Manage UserRecord Stores", "userstores"));
        }
        
        focusTreeItem(item);
    }
    
    private TreeItem createFakePanel(String label, String description, String editor) {
    	// add a new TreeItem for Refreshing Configurations
    	TreeItem ti = new TreeItem();
    	ti.setText(label);
    	PrefItem pi = new PrefItem();
    	pi.setDescription(description);
    	pi.setLabel(label);
    	if (!StringUtils.isEmpty(editor)) {
    		pi.getHints().setHint("editor", editor);
    	}
    	ti.setUserObject(pi);
    	return ti;
    }

    private void focusTreeItem(TreeItem ti) {
        if (ti==null) return;
        
        PrefItem prefGroup = (PrefItem) ti.getUserObject();
        if (!StringUtils.isEmpty(prefGroup.getHints().getHint("editor"))) {
            editPanel.setEditorWidget(EditorFactory.createEditor(prefGroup.getHints().getHint("editor")));
        } else if (prefGroup.getHints().getBooleanValue("log4jEditor", false)) {
            editPanel.setEditorWidget(EditorFactory.createEditor("log4jEditor"));
        } else {
            if (prefGroup.getChildren()==null || prefGroup.getChildren().length==0) {
                // fetch items
                ti.setState(true, true);
                editPanel.setEditorWidget(null);
            } else {
                prefItemsPanel.setPanelItems((PrefItem) ti.getUserObject());
                editPanel.setEditorWidget(prefItemsPanel);
            }
        }
    }
}
