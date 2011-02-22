package org.jdna.bmt.web.client.ui.prefs;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ViewPluginDetails extends Composite {

	private static ViewPluginDetailsUiBinder uiBinder = GWT
			.create(ViewPluginDetailsUiBinder.class);

	interface ViewPluginDetailsUiBinder extends
			UiBinder<Widget, ViewPluginDetails> {
	}

	@UiField Element name;
	@UiField Element version;
	@UiField Element author;
	@UiField Element description;
	@UiField Element releaseNotes;
	@UiField Element lastModified;
	@UiField Element created;
	@UiField Element dependencies;
	@UiField Element pluginId;
	
	public ViewPluginDetails(PluginDetail det) {
		initWidget(uiBinder.createAndBindUi(this));
		name.setInnerText(det.getName());
		version.setInnerText(det.getVersion());
		author.setInnerText(det.getAuthor());
		description.setInnerText(det.getDescription());
		releaseNotes.setInnerHTML(formatReleaseNotes(det.getReleaseNotes()));
		lastModified.setInnerText(DateTimeFormat.getFullDateFormat().format(new Date(det.getLastModified())));
		created.setInnerText(DateTimeFormat.getFullDateFormat().format(new Date(det.getCreatedDate())));
		if (det.getPluginDependencies()!=null) {
			for (String s: det.getPluginDependencies()) {
				dependencies.setInnerHTML(dependencies.getInnerHTML() + "<li>" + s + "</li>");
			}
		}
		pluginId.setInnerText(det.getId());
	}

	private String formatReleaseNotes(String notes) {
		if (notes==null) {
			return null;
		}
		
		notes = notes.replaceAll("\\*","<li>");
		return notes;
	}

	public ViewPluginDetails(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
