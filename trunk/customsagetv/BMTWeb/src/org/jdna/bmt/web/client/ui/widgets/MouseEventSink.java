package org.jdna.bmt.web.client.ui.widgets;

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class MouseEventSink implements MouseUpHandler, MouseDownHandler {
	public static final MouseEventSink INSTANCE = new MouseEventSink();
	
	public MouseEventSink() {
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}
	
	public static void consumeEvents(Widget widget) {
		widget.sinkEvents(Event.MOUSEEVENTS);
		((HasMouseDownHandlers)widget).addMouseDownHandler(MouseEventSink.INSTANCE);
		((HasMouseUpHandlers)widget).addMouseUpHandler(MouseEventSink.INSTANCE);
	}
}
