package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.browser.MediaItem.NonEditField;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.LargeStringTextBox;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.media.metadata.MetadataKey;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class MediaEditorPanel extends Composite {
    @SuppressWarnings("unchecked")
    public MediaEditorPanel(MediaItem md) {
        Simple2ColFormLayoutPanel panel = new Simple2ColFormLayoutPanel();
        panel.setWidth("100%");
        
        LargeStringTextBox tb = new LargeStringTextBox(InputBuilder.textbox().bind(md.getNonEditField(NonEditField.FILE_URI)).widget(), "URI");
        tb.setReadOnly(true);
        panel.add("Uri", tb);
        panel.add("Media Id", new Label(md.getNonEditField(NonEditField.MEDIA_ID).get()));
        
        panel.add("Display Title", InputBuilder.textbox().bind(md.getProperty(MetadataKey.DISPLAY_TITLE)).widget());
        panel.add("Fanart Title", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MEDIA_TITLE)).widget());
        panel.add("Fanart Type", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MEDIA_TYPE)).widget());

        panel.add("Description", new LargeStringTextBox(InputBuilder.textbox().bind(md.getProperty(MetadataKey.DESCRIPTION)).widget(), ""));

        panel.add("Year", InputBuilder.textbox().bind(md.getProperty(MetadataKey.YEAR)).widget());
        panel.add("Release Date", InputBuilder.textbox().bind(md.getProperty(MetadataKey.RELEASE_DATE)).widget());
        panel.add("Running Time", InputBuilder.textbox().bind(md.getProperty(MetadataKey.RUNNING_TIME)).widget());
        panel.add("Duration", InputBuilder.textbox().bind(md.getProperty(MetadataKey.DURATION)).widget());

        panel.add("Disc #", InputBuilder.textbox().bind(md.getProperty(MetadataKey.DVD_DISC)).widget());
        panel.add("Season #", InputBuilder.textbox().bind(md.getProperty(MetadataKey.SEASON)).widget());
        panel.add("Episode #", InputBuilder.textbox().bind(md.getProperty(MetadataKey.EPISODE)).widget());
        panel.add("Episode Title", InputBuilder.textbox().bind(md.getProperty(MetadataKey.EPISODE_TITLE)).widget());

        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.GENRE_LIST)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.CAST_MEMBER_LIST)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.BACKGROUND_ART)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.BANNER_ART)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MEDIA_ART_LIST)).widget());
        //panel.add("", InputBuilder.textbox().bind(md.getProperty(MetadataKey.POSTER_ART)).widget());
        
        panel.add("Metadata Id", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MEDIA_PROVIDER_DATA_ID)).widget());
        panel.add("MPAA", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MPAA_RATING)).widget());
        panel.add("MPAA Description", InputBuilder.textbox().bind(md.getProperty(MetadataKey.MPAA_RATING_DESCRIPTION)).widget());
        panel.add("User Rating", InputBuilder.textbox().bind(md.getProperty(MetadataKey.USER_RATING)).widget());

        panel.add("Poster Count", new Label(md.getNonEditField(NonEditField.POSTER_COUNT).get()));
        tb = new LargeStringTextBox(InputBuilder.textbox().bind(md.getNonEditField(NonEditField.POSTER_URI)).widget(), "Current Poster Folder");
        tb.setReadOnly(true);
        panel.add("Posters Folder", tb);
        
        panel.add("Background Count", new Label(md.getNonEditField(NonEditField.BACKGROUND_COUNT).get()));
        tb = new LargeStringTextBox(InputBuilder.textbox().bind(md.getNonEditField(NonEditField.BACKGROUND_URI)).widget(), "Current Background Folder");
        tb.setReadOnly(true);
        panel.add("Backgrounds Folder", tb);
        
        panel.add("Banner Count", new Label(md.getNonEditField(NonEditField.BANNER_COUNT).get()));
        tb = new LargeStringTextBox(InputBuilder.textbox().bind(md.getNonEditField(NonEditField.BANNER_URI)).widget(), "Current Banner Folder");
        tb.setReadOnly(true);
        panel.add("Banners Folder", tb);
        
        initWidget(panel);
        
        System.out.println("Sage Media File: " + md.getSageMediaItemString());
    }
}
