package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.animation.FadeOut;
import org.jdna.bmt.web.client.event.NotificationEvent;
import org.jdna.bmt.web.client.event.NotificationEventHandler;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;

import sagex.phoenix.metadata.MediaArtifactType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FanartManagerPanel extends Composite implements NotificationEventHandler {

    private static FanartManagerPanelUiBinder uiBinder = GWT.create(FanartManagerPanelUiBinder.class);

    interface FanartManagerPanelUiBinder extends UiBinder<Widget, FanartManagerPanel> {
    }

    @UiField Image image;
    @UiField ScrollPanel scroller;
    @UiField HorizontalPanel imagePanel;
    @UiField TextBox downloadUrl;
    @UiField Label message;
    
    private GWTMediaFile file;
    private MediaArtifactType curType=null;
    private HandlerRegistration handler;
    private FanartImage curImage;
    
    public FanartManagerPanel(GWTMediaFile mf) {
        initWidget(uiBinder.createAndBindUi(this));
        this.file=mf;
        this.image.setUrl(mf.getThumbnailUrl());
    }
    
    
    @UiHandler("postersButton")
    public void loadPosters(ClickEvent evt) {
        loadFanart(MediaArtifactType.POSTER);
    }

    @UiHandler("bannersButton")
    public void loadBanners(ClickEvent evt) {
        loadFanart(MediaArtifactType.BANNER);
    }
    
    @UiHandler("backgroundsButton")
    public void loadBackgrounds(ClickEvent evt) {
        loadFanart(MediaArtifactType.BACKGROUND);
    }

    @UiHandler("download")
    public void download(ClickEvent evt) {
        if (downloadUrl.getText().length()==0) {
            onNotification(new NotificationEvent("Download Url cannot be blank."));
            return;
        }
        GWTMediaArt ma = new GWTMediaArt();
        ma.setDownloadUrl(downloadUrl.getText());
        BrowsingServicesManager.getInstance().downloadFanart(file, curType, ma, new AsyncCallback<GWTMediaArt>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to download image", caught);
            }

            public void onSuccess(GWTMediaArt result) {
                FanartImage img = new FanartImage(result,FanartManagerPanel.this);
                imagePanel.add(img);
                img.onClick(null);
            }
        });
    }

    private void loadFanart(final MediaArtifactType type) {
        curType=type;
        BrowsingServicesManager.getInstance().loadFanart(file, type, new AsyncCallback<ArrayList<GWTMediaArt>>() {
            public void onSuccess(ArrayList<GWTMediaArt> result) {
                if (result==null || result.size()==0) {
                    Application.fireErrorEvent("Failed to load fanart for: " + type);
                    return;
                }
                updateDisplay(result);
            }
            
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to load fanart for: " + type, caught);
            }
        });
    }

    protected void updateDisplay(ArrayList<GWTMediaArt> result) {
        imagePanel.clear();
        for (GWTMediaArt ma : result) {
            imagePanel.add(new FanartImage(ma,this));
        }
        if (result.size()>0) {
            FanartImage img = (FanartImage) imagePanel.getWidget(0);
            img.onClick(null);
        }
    }

    public void setImageUrl(String url) {
        image.setUrl(url);
        if (curType == MediaArtifactType.POSTER) {
            image.setWidth("200px");
        } else {
            image.setWidth("300px");
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        handler = Application.events().addHandler(NotificationEvent.TYPE, this);
        loadPosters(null);
    }
    
    @UiHandler("delete")
    public void deleteImage(ClickEvent evt) {
        if(curImage!=null) {
            if (Window.confirm("Press OK to delete this image.  This cannot be undone.")) {
                BrowsingServicesManager.getInstance().getServices().deleteFanart(curImage.getMediaArt(), new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        Application.fireErrorEvent("Unable to delete image", caught);
                    }
    
                    public void onSuccess(Boolean result) {
                        if (Boolean.TRUE.equals(result)) {
                            removeCurrentImage();
                        } else {
                            Application.fireErrorEvent("Unable to delete image");
                        }
                    }
                });
            }
        } else {
            onNotification(new NotificationEvent("Can't remove this image"));
        }
    }

    protected void removeCurrentImage() {
        imagePanel.remove(curImage);
        try {
            FanartImage img = (FanartImage) imagePanel.getWidget(0);
            img.onClick(null);
        } catch (Exception e) {
            // don't care
        }
        onNotification(new NotificationEvent("Image deleted"));
    }

    @UiHandler("makeDefault")
    public void makeDefaultImage(ClickEvent evt) {
        if (curImage!=null) {
            BrowsingServicesManager.getInstance().getServices().makeDefaultFanart(file, curType, curImage.getMediaArt(), new AsyncCallback<Void>() {
                public void onFailure(Throwable caught) {
                    Application.fireErrorEvent("Unable to make this the default iamge", caught);
                }

                public void onSuccess(Void result) {
                    Application.fireNotification("This is now the default");
                }
            });
        } else {
            onNotification(new NotificationEvent("Already default"));
        }
    }

    public void onNotification(NotificationEvent event) {
        message.getElement().getStyle().setOpacity(1.0);
        message.setText(event.getMessage());
        FadeOut out = new FadeOut(message);
        out.run(1000, System.currentTimeMillis()+3000);
    }


    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onDetach()
     */
    @Override
    protected void onDetach() {
        super.onDetach();
        handler.removeHandler();
    }


    /**
     * @return the curImage
     */
    public FanartImage getCurImage() {
        return curImage;
    }


    /**
     * @param curImage the curImage to set
     */
    public void setCurImage(FanartImage curImage) {
        this.curImage = curImage;
        setImageUrl(curImage.getMediaArt().getDisplayUrl());
    }
}
