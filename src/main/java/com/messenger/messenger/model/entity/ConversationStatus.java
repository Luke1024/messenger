package com.messenger.messenger.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ConversationStatus {
    private List<Message> waitingMessages = new ArrayList<>();
    private int notificationCount = 0;
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

    public boolean isThereSomethingNew(){
        return somethingChanged;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public void setSomethingChanged(boolean somethingChanged) {
        this.somethingChanged = somethingChanged;
    }
}