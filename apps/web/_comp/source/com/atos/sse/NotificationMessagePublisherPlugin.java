/* (c) Copyright 2010 SAP AG. All rights reserved. */
package com.atos.sse;

import com.sap.me.frame.ServiceLocator;
import com.sap.me.messaging.AddNotificationMessageRequest;
import com.sap.me.messaging.NotificationMessageServiceInterface;
import com.sap.me.wpmf.Plugin;
import com.sap.me.wpmf.util.ExceptionHandler;


/**
 * Publisher for Notification Messages
 */
public class NotificationMessagePublisherPlugin extends Plugin {
    private static final long serialVersionUID = 1L;

    private String message = "";

    private NotificationMessageServiceInterface nmService = null;

    public NotificationMessagePublisherPlugin () {
        super();
    }

    public void closePlugin() {
        closeCurrentPlugin(false, false);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void processSend() {
        try {
        	AddNotificationMessageRequest request = new AddNotificationMessageRequest("PUSH_NOTIFICATION_TEST");
        	request.setComments(getMessage());
        	getNotificationMessageService().addNotificationMessage(request);
        } catch (Exception ex) {
        	ExceptionHandler.handle(ex);
        } finally {
        	setMessage("");
        }
    }


    public NotificationMessageServiceInterface getNotificationMessageService() {
    	if (nmService == null) {
    		nmService = (NotificationMessageServiceInterface)ServiceLocator.getService("com.sap.me.messaging", "NotificationMessageService");
    	}
        return nmService;
    }
}
