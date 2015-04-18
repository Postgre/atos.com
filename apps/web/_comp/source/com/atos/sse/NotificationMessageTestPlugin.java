package com.atos.sse;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.sap.me.frame.Utils;
import com.sap.me.frame.service.CommonMethods;
import com.sap.me.messaging.AddNotificationMessageRequest;
import com.sap.me.messaging.NotificationMessageServiceInterface;
import com.sap.me.messaging.client.RetrieveMessageBoardListEvent;
import com.sap.me.messaging.client.RetrieveMessageBoardListListenerInterface;
import com.sap.me.production.podclient.NotificationMessageEvent;
import com.sap.me.production.podclient.NotificationMessageListenerInterface;
import com.sap.me.production.podclient.NotificationMessageManagerHelper;
import com.sap.me.security.RunAsServiceLocator;
import com.sap.me.wpmf.Plugin;
import com.sap.me.wpmf.util.ExceptionHandler;
import com.sap.me.wpmf.util.FacesUtility;
import com.visiprise.frame.configuration.ServiceReference;

public class NotificationMessageTestPlugin extends Plugin implements NotificationMessageListenerInterface, RetrieveMessageBoardListListenerInterface  {

	private static final long serialVersionUID = 1L;

    private String message = null;
    private long messageCount = 0;
    private NotificationMessageServiceInterface nmService = null;


    public NotificationMessageTestPlugin() {
        super();
        getPluginEventManager().addPluginListeners(this.getClass());
    }

    public void processPluginLoaded() {
        try {
            NotificationMessageManagerHelper.addConsumer("NOTIFICATION_MESSAGING_TESTING", this);

            runPublishMessageOnClient(5000);

        } catch (Exception ex) {
            ExceptionHandler.handle(ex);
        }
    }

    public void closePlugin() {
        try {
            NotificationMessageManagerHelper.removeConsumer("NOTIFICATION_MESSAGING_TESTING", this);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex);
        }
        closeCurrentPlugin(false, false);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
    }

    public String getMessageCount() {
        return String.valueOf(messageCount);
    }

    public void setMessageCount(String messageCount) {
    }

    @Override
    public void processMessage(NotificationMessageEvent event) {

        if (!"NOTIFICATION_MESSAGING_TESTING".equals(event.getTopic())) {
            return;
        }
        try {
            List<String> messages = event.getMessages();
            if (messages == null || messages.isEmpty()) {
                return;
            }
            StringBuffer messageBuffer = new StringBuffer();
            if (!Utils.isBlank(this.message)) {
                messageBuffer.append(this.message);
            }
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

            UIComponent container = this.findComponent(FacesContext.getCurrentInstance().getViewRoot(), "notificationMessageTestPanel");
            if (container != null) {
                UIComponent comp = this.findComponent(container, "messageText");
                if (comp != null) {
                    FacesUtility.addControlUpdate(comp);
                }
            }

        } catch (Exception ex) {
            ExceptionHandler.handle(ex);

        } finally {
            runPublishMessageOnClient(250);
        }
    }

    public void publishMessage() {
        try {
            messageCount++;
            AddNotificationMessageRequest request = new AddNotificationMessageRequest("NOTIFICATION_MESSAGING_TESTING");
            request.setComments("Notification Message Test #" + messageCount);
            getNotificationMessageService().addNotificationMessage(request);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex);
        }
    }

    private void runPublishMessageOnClient(int iDelay) {
        try {
            UIComponent container = this.findComponent(FacesContext.getCurrentInstance().getViewRoot(), "notificationMessageTestPanel");
            if (container == null) {
                return;
            }
            UIComponent comp = this.findComponent(container, "notificationMessagePublishButton");
            if (comp != null) {
                String clientId = comp.getClientId(FacesContext.getCurrentInstance());
                if (!Utils.isBlank(clientId)) {
                    StringBuffer buf = new StringBuffer();
                    buf.append("setTimeout(function(){processButtonPress('");
                    buf.append(clientId);
                    buf.append("',0);},");
                    buf.append(iDelay);
                    buf.append(");");
                    FacesUtility.addScriptCommand(buf.toString());
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.handle(ex);
        }
    }

    public NotificationMessageServiceInterface getNotificationMessageService() {
       /* if (nmService == null) {
            nmService = (NotificationMessageServiceInterface)ServiceLocator.getService("com.sap.me.messaging", "NotificationMessageService");
    		ServiceReference shoporderServiceRef = new ServiceReference("com.sap.me.demand", "ShopOrderService");
    		shoporderService = RunAsServiceLocator.getService(shoporderServiceRef,ShopOrderServiceInterface.class, user, site, null);
       }*/
    	ServiceReference NotificationMessageServiceRef = new ServiceReference("com.sap.me.messaging", "NotificationMessageService");
    	nmService = RunAsServiceLocator.getService(NotificationMessageServiceRef,NotificationMessageServiceInterface.class, CommonMethods.getUserId(), CommonMethods.getSite(), null);
        return nmService;
    }

    @Override
    public void processRetrieveMessageBoardListEvent(RetrieveMessageBoardListEvent event) {
        publishMessage();
    }
}
