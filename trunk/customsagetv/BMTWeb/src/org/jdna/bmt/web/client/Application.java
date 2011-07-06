package org.jdna.bmt.web.client;

import org.jdna.bmt.web.client.event.EventBus;
import org.jdna.bmt.web.client.event.NotificationEvent;
import org.jdna.bmt.web.client.event.NotificationEvent.MessageType;
import org.jdna.bmt.web.client.i18n.Labels;
import org.jdna.bmt.web.client.i18n.Msgs;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.app.GlobalService;
import org.jdna.bmt.web.client.ui.app.GlobalServiceAsync;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.VoidCallback;
import org.jdna.bmt.web.client.util.MessageBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

public class Application {

	public static final String MSG_REQUEST_CURRENT_VIEW_INFO = "bmt.req.currentview";
	public static final String MSG_RESPONSE_CURRENT_VIEW_INFO = "bmt.resp.currentview";
	public static final String PARAM_MSG_RESPONSE_CURRENT_VIEW_VIEWNAME = "view";
	public static final String PARAM_MSG_RESPONSE_CURRENT_VIEW_PATH = "path";

    private static final Labels i18nLabels = GWT.create(Labels.class);
    private static final Msgs i18nMessages = GWT.create(Msgs.class);
    private static final HandlerManager eventBus = EventBus.getHandlerManager();
    private static final GlobalServiceAsync global = GWT.create(GlobalService.class);
    private static final MessageBus messageBus = new MessageBus();
    
    public static boolean BMT5 = false;
    
    public static Labels labels() {
        return i18nLabels;
    }
    
    public static Msgs messages() {
        return i18nMessages;
    }

    public static HandlerManager events() {
        return eventBus;
    }

    public static void fireErrorEvent(String msg) {
        fireErrorEvent(msg, null);
    }

    public static void fireErrorEvent(String msg, Throwable t) {
        events().fireEvent(new NotificationEvent(MessageType.ERROR, msg, t));    
    }

    public static void fireNotification(String msg) {
        events().fireEvent(new NotificationEvent(MessageType.INFO, msg));    
    }
    
    /**
     * Runs a Batch Folder Operation on the Server.  Folder can be null, and if so, then
     * it will run it against all video files.
     * 
     * @param folder
     * @param op
     */
    public static void runBatchOperation(final GWTMediaFolder folder, final BatchOperation op) {
    	if (op.getConfirm()!=null) {
	    	Dialogs.confirm(op.getConfirm(), new DialogHandler<Void>() {
				@Override
				public void onSave(Void data) {
					runBatchOperationPrivate(folder, op);
				}
				
				@Override
				public void onCancel() {
				}
			});
    	} else {
			runBatchOperationPrivate(folder, op);
    	}
    }
    
    private static void runBatchOperationPrivate(GWTMediaFolder folder, BatchOperation op) {
		if (op.getStartMessage()!=null) {
			Application.fireNotification(op.getStartMessage());
		}
		
		if (folder!=null) {
			global.batchOperation(folder, op, new VoidCallback<Void>());
		} else {
			global.batchOperation(op, new VoidCallback<Void>());
		}
    }

	public static MessageBus getMessagebus() {
		return messageBus;
	}
}
