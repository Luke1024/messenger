package com.messenger.messenger.model.dto;

import java.util.List;

public class ConversationStatusDto {
    private long conversationId;
    private List<UserDto> users;
    private int waitingMessages;
    private boolean direct;

    public ConversationStatusDto(long conversationId, List<UserDto> users, int waitingMessages, boolean direct) {
        this.conversationId = conversationId;
        this.users = users;
        this.waitingMessages = waitingMessages;
        this.direct = direct;
    }

    public long getConversationId() {
        return conversationId;
    }

    public List<UserDto> getUsers() {
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
                '}';
    }
}
