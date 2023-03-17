package com.messenger.messenger.model.dto;

public class ConversationStatusDto {
    private long conversationId;
    private String users;
    private int waitingMessages;
    private boolean direct;

    public ConversationStatusDto(long conversationId, String users, int waitingMessages, boolean direct) {
        this.conversationId = conversationId;
        this.users = users;
        this.waitingMessages = waitingMessages;
        this.direct = direct;
    }

    public long getConversationId() {
        return conversationId;
    }

    public String getUsers() {
        return users;
    }

    public int getWaitingMessages() {
        return waitingMessages;
    }

    public boolean isDirect() {
        return direct;
    }

    @Override
    public String toString() {
        return "ConversationStatusDto{" +
                "conversationId=" + conversationId +
                ", users=" + users +
                ", waitingMessages=" + waitingMessages +
                ", direct=" + direct +
                '}';
    }
}
