package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.util.AsyncServiceReply;
import org.jdna.bmt.web.client.ui.util.Dialogs;

import sagex.phoenix.metadata.MediaArtifactType;
import sagex.phoenix.metadata.MediaType;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FanartManagerPanel extends Composite {
	private GWTMediaFile file;
	private BrowsePanel controller;
    
	VerticalPanel panel = new VerticalPanel();
	DecoratedTabPanel tabs = new DecoratedTabPanel();
	
	VerticalPanel fanartPanel = new VerticalPanel();
	FanartPanel posters = new FanartPanel();
	FanartPanel backgrounds = new FanartPanel();
	FanartPanel banners = new FanartPanel();
	
	VerticalPanel files = new VerticalPanel();
	
    public FanartManagerPanel(BrowsePanel controller, GWTMediaFile mf) {
    	this.controller=controller;
        this.file=mf;

        panel.setWidth("100%");
        Label l = new Label(mf.getFanartDir());
        l.getElement().getStyle().setPaddingBottom(5, Unit.PX);
        panel.add(l);
        
        tabs.setWidth("100%");
        tabs.getTabBar().setWidth("100%");
        tabs.getDeckPanel().setWidth("100%");
        tabs.getDeckPanel().getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        
        fanartPanel.add(posters);
        posters.init(this, controller, mf, MediaArtifactType.POSTER);
        if (MediaType.TV.equals(mf.getType()) || "TV".equals(mf.getMetadata().getMediaType().get())) {
        	fanartPanel.add(banners);
        	banners.init(this, controller, mf, MediaArtifactType.BANNER);
        }
        fanartPanel.add(backgrounds);
        backgrounds.init(this, controller, mf, MediaArtifactType.BACKGROUND);
        
        tabs.add(fanartPanel, "Fanart");
        
        tabs.add(createFiles(), "Files");
        
        VerticalPanel vp = new VerticalPanel();
        vp.add(new DownloadFanartPanel(this, MediaArtifactType.POSTER));
        vp.add(new DownloadFanartPanel(this, MediaArtifactType.BACKGROUND));
        if (MediaType.TV.equals(mf.getType()) || "TV".equals(mf.getMetadata().getMediaType().get())) {
        	vp.add(new DownloadFanartPanel(this, MediaArtifactType.BANNER));
        }
        tabs.add(vp, "Download");
        
        tabs.selectTab(0, true);
        
        panel.add(tabs);
        
        initWidget(panel);
    }

	private Widget createFiles() {
		controller.loadFanartFiles(file, new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				updateFiles(result);
			}

		});
		return files;
	}

	private void updateFiles(ArrayList<String> result) {
		files.clear();
		if (result!=null && result.size()>0) {
			for (String f: result) {
				Label l = new Label(f);
				files.add(l);
			}
		} else {
			files.add(new Label("No fanart files"));
		}
	}

	public void downloadFanart(String url, final MediaArtifactType type) {
        GWTMediaArt ma = new GWTMediaArt();
        ma.setType(type);
        ma.setDownloadUrl(url);
        Dialogs.showWaiting("Downloading Fanart image...");
        controller.downloadFanart(file, type, ma, new AsyncServiceReply<GWTMediaArt>() {
			@Override
			public void onOK(GWTMediaArt result) {
            	Application.fireNotification("Image downloaded to " + result.getLocalFile());
            	if (type.equals(MediaArtifactType.POSTER)) {
            		posters.loadFanart(type);
            	} else if (type.equals(MediaArtifactType.BANNER)) {
            		banners.loadFanart(type);
            	} else if (type.equals(MediaArtifactType.BACKGROUND)) {
            		backgrounds.loadFanart(type);
            	}
            	createFiles();
			}
        });
	}

	public void updateFiles() {
		createFiles();
	}
}
