package com.messenger.messenger.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ConversationStatus {
    private List<Message> waitingMessages = new ArrayList<>();
    private int notificationCount;
    private boolean somethingChanged = true;

    public ConversationStatus() { }

    public List<Message> getWaitingMessages() {
        somethingChanged = true;
        return waitingMessages;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void addNotification(){
        somethingChanged = true;
        notificationCount++;
    }

    public void clearStatus(){
        notificationCount=0;
        somethingChanged=false;
        waitingMessages.clear();
    }

    public boolean isThereSomethingNew(){
        return somethingChanged;
    }
}