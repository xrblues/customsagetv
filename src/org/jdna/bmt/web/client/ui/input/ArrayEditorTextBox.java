package org.jdna.bmt.web.client.ui.input;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArrayEditorTextBox extends EditorTextBox {
    private static class ArrayEditorPanel extends TextEditorPanel {
        private ListBox list;
        private String arraySep;
        public ArrayEditorPanel(String arraySep, String curValue) {
            super(new TextBox());
            this.arraySep=arraySep;
            
            VerticalPanel main = new VerticalPanel();
            main.setSpacing(5);
            main.setWidth("100%");
            
            HorizontalPanel add = new HorizontalPanel();
            add.setSpacing(5);
            final TextBox addBox = new TextBox();
            Button addBtn = new Button("Add", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (addBox.getValue()!=null && addBox.getValue().length()>0) {
                        for (int i=0;i<list.getItemCount();i++) {
                            if (list.getItemText(i).equals(addBox.getValue())) {
                                Log.debug("Duplicate Item: " + addBox.getValue());
                                return;
                            }
                        }
                        
                        if (list.getSelectedIndex()>=0) {
                            list.insertItem(addBox.getValue(), list.getSelectedIndex()+1);
                        } else {
                            list.addItem(addBox.getValue());
                        }
                        addBox.setValue(null);
                    }
                }
            });
            add.add(addBox);
            add.add(addBtn);
            main.add(add);
            
            HorizontalPanel items = new HorizontalPanel();
            items.setSpacing(5);
            
            list = new ListBox(false);
            list.setWidth("100%");
            list.setVisibleItemCount(10);
            if (curValue!=null) {
                String parts[] = curValue.split(arraySep);
                if (parts!=null && parts.length>0) {
                    for (String p : parts)
                    list.addItem(p);
                }
            }
            
            items.add(list);
            items.setCellWidth(list, "75%");
            
            VerticalPanel btnpanel = new VerticalPanel();
            btnpanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            btnpanel.setSpacing(5);
            
            Button up = new Button("Up", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (list.getSelectedIndex()>0) {
                        int sel = list.getSelectedIndex();
                        String s = list.getItemText(sel);
                        list.removeItem(sel);
                        list.insertItem(s, sel-1);
                        list.setSelectedIndex(sel-1);
                    }
                }
            });
            up.setWidth("100%");
            Button down = new Button(Application.labels().down(), new ClickHandler() {
                public void onClick(ClickEvent event) {
                    int sel = list.getSelectedIndex();
                    if (sel>=0 && sel<list.getItemCount()-1) {
                        String s = list.getItemText(sel);
                        list.removeItem(sel);
                        list.insertItem(s, sel+1);
                        list.setSelectedIndex(Math.min(sel+1, list.getItemCount()-1));
                    }
                }
            });
            down.setWidth("100%");
            Button remove = new Button(Application.labels().remove(), new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if (list.getSelectedIndex()>=0) {
                        int s = list.getSelectedIndex();
                        list.removeItem(s);
                        if (list.getItemCount()>0) {
                            list.setSelectedIndex(Math.min(s, list.getItemCount()-1));
                        }
                    }
                }
            });
            remove.setWidth("100%");
            
            btnpanel.add(up);
            btnpanel.add(down);
            btnpanel.add(remove);
            
            items.add(btnpanel);
            
            main.add(items);
            
            initWidget(main);
        }
        @Override
        public String getValue() {
            StringBuffer sb = new StringBuffer();
            if (list!=null && list.getItemCount()>0) {
                int len = list.getItemCount();
                for (int i=0;i<len;i++) {
                    sb.append(list.getValue(i));
                    if (i<(len-1)) {
                        sb.append(arraySep);
                    }
                }
            }
            setValue(sb.toString());
            return super.getValue();
        }
    }
    
    private String arraySep = null;
    public ArrayEditorTextBox(TextBox tb, String caption, String arraySep) {
        super(tb, caption);
        this.arraySep = arraySep;
    }

    @Override
    protected TextEditorPanel createEditorPanel() {
        return new ArrayEditorPanel(arraySep, getValue());
    }
}
