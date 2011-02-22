package org.jdna.bmt.web.client.ui.browser;

import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.util.CommandItem;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.SideMenuItem;
import org.jdna.bmt.web.client.util.MessageHandler;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScanSideMenuItem extends SideMenuItem implements MessageHandler {
	private class ItemActions extends Composite {
		private DockPanel panel = new DockPanel();
		private Label status = new Label();

		public ItemActions(Widget primaryLabel) {
			panel.setWidth("100%");
			panel.setHeight("100%");
			panel.add(primaryLabel, DockPanel.NORTH);
			status.addStyleName("ScanSideMenuItem-labelStatus");
			status.setWordWrap(true);
			status.setText("Waiting for update...");
			panel.add(status, DockPanel.CENTER);

			HorizontalButtonBar buttons = new HorizontalButtonBar();
			// buttons.basicStyle();
			buttons.setWidth("100%");

			CommandItem cancel = new CommandItem(
					"images/16x16/process-stop.png", "Cancel", new Command() {
						public void execute() {
							controller.cancelScan(progressId);
						}
					});
			buttons.add(cancel);

			CommandItem remove = new CommandItem(
					"images/16x16/edit-delete.png", "Remove", new Command() {
						public void execute() {
							controller.removeScan(progressId);
							removeItem();
						}
					});
			buttons.add(remove);

			panel.add(buttons, DockPanel.SOUTH);
			panel.setCellHorizontalAlignment(buttons,
					HasHorizontalAlignment.ALIGN_RIGHT);
			panel.setCellWidth(buttons, "100%");
			initWidget(panel);
		}

		public void setStatusText(String text) {
			status.setText(text);
		}
	}

	private String progressId = null;
	private ProgressStatus progressStatus = null;
	private BrowsePanel controller;

	public ScanSideMenuItem(BrowsePanel controller, String label,
			String iconUrl, String progressId, ClickHandler onclick) {
		super(label, iconUrl, onclick);
		this.controller = controller;
		this.progressId = progressId;
	}

	public void removeItem() {
		removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdna.bmt.web.client.ui.util.SideMenuItem#createLabelWidget()
	 */
	@Override
	protected Widget createLabelWidget() {
		return new ItemActions(super.createLabelWidget());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		controller.getMessageBus().addHandler(BrowsePanel.MSG_PROGRESS_UPDATED,
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();
		controller.getMessageBus().removeHandler(
				BrowsePanel.MSG_PROGRESS_UPDATED, this);
	}

	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (BrowsePanel.MSG_PROGRESS_UPDATED.equals(msg)) {
			ProgressStatus status = (ProgressStatus) args
					.get(BrowsePanel.MSG_PROGRESS_UPDATED);

			if (progressId.equals(status.getProgressId())) {
				if (status.isCancelled()) {
					setStatus(Application.messages().cancelledWithStatus(
							status.getSuccessCount(), status.getFailedCount()));
					setBusy(false);
				} else if (status.isDone()) {
					setStatus(Application.messages().completeWithStatus(
							status.getSuccessCount(), status.getFailedCount()));
					setBusy(false);
				} else {
					setBusy(true);
					setStatus(Application.messages().scanStatus(
							status.getStatus(), (status.getWorked())));
				}
			}
		}
	}

	public void setStatus(String status) {
		if (getLabelWidget() != null) {
			((ItemActions) getLabelWidget()).setStatusText(status);
		}
	}

	public ProgressStatus getProgress() {
		return progressStatus;
	}
}
