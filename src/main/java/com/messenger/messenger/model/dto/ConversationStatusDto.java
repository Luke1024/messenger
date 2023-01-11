package com.messenger.messenger.model.dto;

import com.messenger.messenger.model.entity.User;

import java.util.List;

public class ConversationStatusDto {
    private long conversationId;
    private List<UserDto> users;
    private int waitingMessages;

    public ConversationStatusDto(long conversationId, List<UserDto> users, int waitingMessages) {
        this.conversationId = conversationId;
        this.users = users;
        this.waitingMessages = waitingMessages;
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

    public void addWaitingMessage(){

    }
}
