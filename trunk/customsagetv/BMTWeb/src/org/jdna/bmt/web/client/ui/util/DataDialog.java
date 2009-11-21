package org.jdna.bmt.web.client.ui.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Children must call initPanels() in their constuctor!
 * 
 * @author seans
 *
 * @param <T>
 */
public class DataDialog<T> extends Composite {
    protected T data = null;
    protected DialogHandler<T> handler;
    
    protected HorizontalPanel headerWidget = null;
    protected VerticalPanel bodyWidget = null;
    protected HorizontalPanel buttonPanel = null;
    protected Button okButton = null;
    protected Button cancelButton = null;
    protected DockPanel mainPanel = null;
    protected List<Button> extraButtons = new ArrayList<Button>();
    protected String title = null;
    
    public DataDialog(String title, T data, DialogHandler<T> handler) {
        this.data=data;
        this.handler=handler;
        this.title=title;
    }
    
    public String getTitle() {
        return title;
    }
    
    protected Widget getHeaderWidget() {
        return null;
    }
    
    protected Widget getBodyWidget() {
        return null;
    }

    protected void updateButtonPanel(Object buttonPan) {
    }
    
    /**
     * Called once all the dialog panels have been created.  At this point, you can
     * change labels, etc, if you need to.
     */
    protected void userInitDialog() {
    }

    /**
     * Called when OK button it clicked.
     * 
     * @return true if the values can be updated to the data object
     */
    protected boolean updateValues() {
         return true;
    }

    protected void initPanels() {
        mainPanel = new DockPanel();
        mainPanel.setWidth("100%");
        
        Widget head = getHeaderWidget();
        if (head!=null) {
            headerWidget = new HorizontalPanel();
            headerWidget.setStyleName("DataDialog-HeaderBox");
            headerWidget.setWidth("95%");
            headerWidget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            headerWidget.add(head);
            headerWidget.setCellHorizontalAlignment(head, HasHorizontalAlignment.ALIGN_CENTER);
            mainPanel.add(headerWidget, DockPanel.NORTH);
            mainPanel.setCellHorizontalAlignment(headerWidget, HasHorizontalAlignment.ALIGN_CENTER);
            mainPanel.setCellVerticalAlignment(headerWidget, HasVerticalAlignment.ALIGN_TOP);
        }
     
        Widget body = getBodyWidget();
        if (body!=null) {
            bodyWidget = new VerticalPanel();
            bodyWidget.setWidth("100%");
            bodyWidget.setStyleName("DataDialog-BodyBox");
            bodyWidget.add(body);
            mainPanel.add(bodyWidget, DockPanel.CENTER);
            mainPanel.setCellHorizontalAlignment(bodyWidget, HasHorizontalAlignment.ALIGN_LEFT);
            mainPanel.setCellVerticalAlignment(bodyWidget, HasVerticalAlignment.ALIGN_TOP);
        }
        
        buttonPanel =new HorizontalPanel();
        buttonPanel.setStyleName("DataDialog-ButtonBox");
        
        okButton = new Button("OK", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (updateValues()) {
                    getHandler().onSave(data);
                }
            }
        });
        buttonPanel.add(okButton);

        cancelButton =new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                getHandler().onCancel();
            }
        });
        buttonPanel.add(cancelButton);
        
        updateButtonPanel(buttonPanel);
        
        mainPanel.add(buttonPanel, DockPanel.SOUTH);
        mainPanel.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        userInitDialog();
        
        initWidget(mainPanel);
    }

    protected DialogHandler<T> getHandler() {
        return handler;
    }
    
    protected void setHandler(DialogHandler<T> newHandler) {
        this.handler=newHandler;
    }
    
    public static <T> void showDialog(final DataDialog<T> dialogPanel) {
        final DialogBox dialog = new DialogBox(false, true);
        final DialogHandler<T> handler = dialogPanel.getHandler();
        dialogPanel.setHandler(new DialogHandler<T>() {
            public void onCancel() {
                dialog.hide();
                handler.onCancel();
                dialogPanel.setHandler(handler);
            }

            public void onSave(T data) {
                dialog.hide();
                handler.onSave(data);
                dialogPanel.setHandler(handler);
            }
        });
        dialog.setText(dialogPanel.getTitle());
        dialog.setWidget(dialogPanel);
        dialog.center();
        dialog.show();  
        dialogPanel.focus();
    }
    
    public void focus() {
    }
    
    public T getData() {
        return data;
    }
}
