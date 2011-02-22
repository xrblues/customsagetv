package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

public abstract class AbstractClickableItem extends Composite implements HasClickHandlers, MouseOutHandler, MouseOverHandler, ClickHandler {
    private String hoverStyleName="Hover";
    
	private HandlerRegistration mouseOver;
	private HandlerRegistration mouseOut;
	private HandlerRegistration clicked;

    public AbstractClickableItem() {
    }
    
    public void setHoveStyleName(String name) {
        this.hoverStyleName=name;
    }

    /**
     * @return the hoverStyleName
     */
    public String getHoverStyleName() {
        return hoverStyleName;
    }

    /**
     * @param hoverStyleName the hoverStyleName to set
     */
    public void setHoverStyleName(String hoverStyleName) {
        this.hoverStyleName = hoverStyleName;
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public void onMouseOut(MouseOutEvent event) {
        if (hoverStyleName!=null) {
            removeStyleName(hoverStyleName);
        }
    }

    public void onMouseOver(MouseOverEvent event) {
        if (hoverStyleName!=null) {
            addStyleName(hoverStyleName);
        }
    }

    public abstract void onClick(ClickEvent event);

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        addStyleName("clickable");
        mouseOver = addDomHandler(this, MouseOverEvent.getType());
        mouseOut = addDomHandler(this, MouseOutEvent.getType());
        clicked = addDomHandler(this, ClickEvent.getType());
    }

	@Override
	protected void onDetach() {
		super.onDetach();
		mouseOver.removeHandler();
		mouseOut.removeHandler();
		clicked.removeHandler();
	}
    
    
}
