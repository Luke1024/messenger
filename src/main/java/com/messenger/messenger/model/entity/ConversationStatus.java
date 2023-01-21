package com.messenger.messenger.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ConversationStatus {
    private List<Message> waitingMessages = new ArrayList<>();
    private int notificationCount;

    public ConversationStatus() { }

    public List<Message> getWaitingMessages() {
        return waitingMessages;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void addNotification(){
        notificationCount++;
    }

    public void clearNotifications(){
        notificationCount=0;
    }
}