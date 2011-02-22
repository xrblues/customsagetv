package org.jdna.bmt.web.client.ui.prefs;

import org.jdna.bmt.web.client.ui.util.Dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class PluginDetailWidget extends Composite {

	private static PluginDetailWidgetUiBinder uiBinder = GWT
			.create(PluginDetailWidgetUiBinder.class);

	interface PluginDetailWidgetUiBinder extends
			UiBinder<Widget, PluginDetailWidget> {
	}

	@UiField Element title;
	@UiField Element description;
	@UiField Element version;
	@UiField Label author;
	@UiField HorizontalPanel iconPanel;
	
	public PluginDetailWidget(final PluginsEditorPanel controller, final PluginDetail d) {
		initWidget(uiBinder.createAndBindUi(this));
		title.setInnerText(d.getName());
		version.setInnerText(d.getVersion());
		author.setText(d.getAuthor());
		
		String desc = d.getDescription();
		if (desc!=null && desc.length()>140) {
			desc = desc.substring(0,140);
			desc += "...";
		}
		description.setInnerText(desc);
		
		author.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.updatePluginsForAuthor(author.getText());
			}
		});
		
		iconPanel.setSpacing(10);
		iconPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Image img = new Image("images/16x16/internet-news-reader.png");
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ScrollPanel sp = new ScrollPanel(new ViewPluginDetails(d));
				sp.setHeight("500px");
				sp.setWidth("400px");
				Dialogs.showAsDialog(d.getName(), sp);
			}
		});
		img.addStyleName("clickable");
		img.setTitle("View plugin details");
		iconPanel.add(img);
		
		if (d.getInstallDate()>0) {
			iconPanel.add(new Image("images/16x16/enblem-favorite.png"));
		}

		if (d.getScreenShots()!=null) {
			for (String s: d.getScreenShots()) {
				final String url = s;
				Image i = new Image("images/16x16/camera-photo.png");
				i.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						// Window.open(url, "pics", null);
						Dialogs.showAsDialog(d.getName(), new ImageViewer(d.getScreenShots()));
					}
				});
				i.setTitle("View Screenshot");
				i.addStyleName("clickable");
				iconPanel.add(i);
			}
		}
		
		if (d.getDemoVideos()!=null) {
			for (String s: d.getDemoVideos()) {
				final String url = s;
				Image i = new Image("images/16x16/applications-multimedia.png");
				i.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Window.open(url, "videos", null);
					}
				});
				i.setTitle("View Video");
				i.addStyleName("clickable");
				iconPanel.add(i);
			}
		}

		if (d.getPluginWebsites()!=null) {
			for (String s: d.getPluginWebsites()) {
				final String url = s;
				Image i = new Image("images/16x16/applications-internet.png");
				i.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Window.open(url, "website", null);
					}
				});
				i.setTitle("Open Website");
				i.addStyleName("clickable");
				iconPanel.add(i);
			}
		}

	}

	private String highlight(String text, String search) {
		if (text==null) return null;
		String replace = "<span style='background-color: #000080;color: white;'>" + search + "</span>";
		text = text.replaceAll(search, replace);
		return text;
	}
}
