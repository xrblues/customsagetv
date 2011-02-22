package org.jdna.bmt.web.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;

public class AbstractMouseAdapter extends Composite implements MouseOutHandler, MouseOverHandler, MouseDownHandler, MouseUpHandler {
    private String hoverStyleName="Hover";
    
	private HandlerRegistration mouseOver;
	private HandlerRegistration mouseOut;
	private HandlerRegistration mouseDown;
	private HandlerRegistration mouseUp;

	boolean buttonDown = false;
	
    public AbstractMouseAdapter() {
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
            removeStyleDependentName(hoverStyleName);
        }
    }

    public void onMouseOver(MouseOverEvent event) {
        if (hoverStyleName!=null) {
            addStyleDependentName(hoverStyleName);
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        addStyleName("clickable");
        mouseOver = addDomHandler(this, MouseOverEvent.getType());
        mouseOut = addDomHandler(this, MouseOutEvent.getType());
        mouseDown = addDomHandler(this, MouseDownEvent.getType());
        mouseUp = addDomHandler(this, MouseUpEvent.getType());
    }

	@Override
	protected void onDetach() {
		super.onDetach();
		mouseOver.removeHandler();
		mouseOut.removeHandler();
		mouseDown.removeHandler();
		mouseUp.removeHandler();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (buttonDown) {
			buttonDown=false;
			onClick();
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		buttonDown = true;
		Timer t = new Timer() {
			@Override
			public void run() {
				if (buttonDown) {
					buttonDown=false;
					onLongPress();
				}
			}
		};
		t.schedule(500);
		event.stopPropagation();
	}

	protected void onLongPress() {
	}
	
	protected void onClick() {
	}
}
