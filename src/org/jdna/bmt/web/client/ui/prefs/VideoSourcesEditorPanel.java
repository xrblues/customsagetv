package org.jdna.bmt.web.client.ui.prefs;

import java.util.List;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.input.FileChooserTextBox;
import org.jdna.bmt.web.client.ui.prefs.VideoSource.SourceType;
import org.jdna.bmt.web.client.ui.util.UpdatablePanel;
import org.jdna.bmt.web.client.util.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VideoSourcesEditorPanel extends Composite implements UpdatablePanel {
    private final PreferencesServiceAsync preferencesService = GWT.create(PreferencesService.class);

    private VerticalPanel panel = new VerticalPanel();
    private List<VideoSource>             sources              = null;
    private FlexTable table = null;
    private TextBox newRow = new TextBox();

    public VideoSourcesEditorPanel() {
        panel.setWidth("100%");
        initWidget(panel);
    }

    public void onLoad() {
        preferencesService.getVideoSources(new AsyncCallback<List<VideoSource>>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to load video sources", caught);
            }

            public void onSuccess(List<VideoSource> result) {
                updatePanel(result);
            }
        });
    }

    private void updatePanel(List<VideoSource> sources) {
        this.sources = sources;
        panel.clear();

        table=new FlexTable();
        table.addStyleName("VideoSource");
        table.setWidth("100%");
        table.setCellSpacing(4);
        for (int i=0;i<sources.size();i++) {
            VideoSource vs = sources.get(i);
            addSource(i, vs);
        }

        buildAddNewRow();
        panel.add(table);
    }

    private void buildAddNewRow() {
        final int row = table.getRowCount();
        newRow.setWidth("100%");
        newRow.setText("");
        final FileChooserTextBox chooser = new FileChooserTextBox(newRow,"Add Video Source", true);
        chooser.setWidth("100%");
        table.setWidget(row, 0, chooser);
        table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        table.setWidget(row, 1, new Button("Add Source", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (StringUtils.isEmpty(chooser.getValue())) {
                    return;
                }
                VideoSource vs = new VideoSource(chooser.getValue(), SourceType.VIDEO);
                vs.setNew(true);
                sources.add(vs);
                addSource(row, vs);
                buildAddNewRow();
            }
        }));
    }

    private void addSource(final int row, final VideoSource vs) {
        table.setWidget(row, 0, new Label(vs.getPath()));
        table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        table.setWidget(row, 1, new Button("Remove", new ClickHandler() {
            public void onClick(ClickEvent event) {
                vs.setDeleted(true);
                table.getCellFormatter().addStyleName(row, 0, "VideosSource-Deleted");
            }
        }));
        table.getRowFormatter().addStyleName(row, "VideoSource-Row");
    }
    
    public String getHeader() {
        return "Video Sources";
    }

    public String getHelp() {
        return "Configure SageTV Video Sources";
    }

    public boolean isReadonly() {
        return false;
    }

    public void save(final AsyncCallback<UpdatablePanel> callback) {
        preferencesService.saveVideoSources(sources, new AsyncCallback<List<VideoSource>>() {
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            public void onSuccess(List<VideoSource> result) {
                VideoSourcesEditorPanel.this.sources = result;
                updatePanel(result);
                callback.onSuccess(VideoSourcesEditorPanel.this);
            }
        });
    }
}
