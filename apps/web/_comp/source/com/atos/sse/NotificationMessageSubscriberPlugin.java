/* (c) Copyright 2010 SAP AG. All rights reserved. */
package com.atos.sse;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sap.me.frame.Utils;
import com.sap.me.production.podclient.NotificationMessageEvent;
import com.sap.me.production.podclient.NotificationMessageListenerInterface;
import com.sap.me.production.podclient.NotificationMessageManagerHelper;
import com.sap.me.wpmf.Plugin;
import com.sap.me.wpmf.util.ExceptionHandler;
import com.sap.me.wpmf.util.FacesUtility;

/**
 * Subscriber message notifications
 */
public class NotificationMessageSubscriberPlugin extends Plugin implements NotificationMessageListenerInterface {
    private static final long serialVersionUID = 1L;

    private String message = "";

    public NotificationMessageSubscriberPlugin() {
        super();
    }
    public void processPluginLoaded() {
        try {
            NotificationMessageManagerHelper.addConsumer("PUSH_NOTIFICATION_TEST", this);
        } catch (Exception ex) {
       	    ExceptionHandler.handle(ex);
        }
    }

    public void closePlugin() {
        try {
        	NotificationMessageManagerHelper.removeConsumer("PUSH_NOTIFICATION_TEST", this);
        } catch (Exception ex) {
       	    ExceptionHandler.handle(ex);
        }
        closeCurrentPlugin(false, false);
    }

    public String getMessage() {
    	if (message == null) {
    		return "";
    	}
        return message;
    }

    public void setMessage(String message) {
    }

	@Override
	public void processMessage(NotificationMessageEvent event) {

    	if (!"PUSH_NOTIFICATION_TEST".equals(event.getTopic())) {
    		return;
    	}
        try {
            List<String> messages = event.getMessages();
            if (messages == null || messages.isEmpty()) {
            	return;
            }
        	StringBuffer messageBuffer = new StringBuffer(getMessage());
            for (String sMsg : messages) {

	            if (messageBuffer.length() > 0) {
	                messageBuffer.append("\n");
	            }
	            if (!Utils.isBlank(sMsg)) {
	                messageBuffer.append(sMsg);
	            }
            }

            this.message = messageBuffer.toString();
            messageBuffer = null;

            UIComponent container = this.findComponent(FacesContext.getCurrentInstance().getViewRoot(), "nmSubscriberPanel");
            if (container != null) {
	            UIComponent comp = this.findComponent(container, "messageText");
	            if (comp != null) {
	            	FacesUtility.addControlUpdate(comp);
	            }
            }
        } catch (Exception ex) {
        	ExceptionHandler.handle(ex);
        }
	}

    /**
     * Notifies the listener the window has gained focus
     **/
    public void processWindowHasFocus() {
    }
}
