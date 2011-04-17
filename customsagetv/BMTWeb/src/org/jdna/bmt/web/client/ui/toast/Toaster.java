package org.jdna.bmt.web.client.ui.toast;

import org.jdna.bmt.web.client.animation.FadeOut;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Toaster {
	private VerticalPanel toastPanel = new VerticalPanel();
	private PopupPanel panel = new PopupPanel(false);
	
	public class MessageFade extends FadeOut {
		public MessageFade(Widget w) {
			super(w);
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			remove((ToastMessage) widget);
		}
	}
	
	public Toaster() {
		panel.setStyleName("Toast");
		panel.setAnimationEnabled(false);
		panel.setWidget(toastPanel);
	}
	
	public void addMessage(String message) {
		addMessage(new ToastMessage(this, message));
	}

	public void addErrorMessage(String message) {
		ToastMessage tm = new ToastMessage(this, message);
		tm.setError();
		addMessage(tm);
	}
	public void addWarnMessage(String message) {
		ToastMessage tm = new ToastMessage(this, message);
		tm.setWarning();
		addMessage(tm);
	}
	
	public void addMessage(final ToastMessage tm) {
		toastPanel.insert(tm,0);
		
		Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
			@Override
			public boolean execute() {
				MessageFade mf = new MessageFade(tm);
				mf.run(300);
				return false;
			}
		}, 5000);
		
		show();
	}


	private void show() {
		if (!panel.isShowing()) {
			panel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		          public void setPosition(int offsetWidth, int offsetHeight) {
		            int left = (Window.getClientWidth() - offsetWidth) / 2;
		            int top = ((Window.getClientHeight() - offsetHeight) / 2) + Window.getScrollTop();
		            panel.setPopupPosition(left, top);
		          }
		    });
		}
	}

	public void remove(ToastMessage toastMessage) {
		toastPanel.remove(toastMessage);
		if (toastPanel.getWidgetCount()==0) {
			panel.hide();
		}
	}
}
