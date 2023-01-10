package com.messenger.messenger.model.dto;

public class UserStatusDto {
    private long userId;
    private String name;
    private int waitingMessages;

    public UserStatusDto() {
    }

    public UserStatusDto(long userId, String name, int waitingMessages) {
        this.userId = userId;
        this.name = name;
        this.waitingMessages = waitingMessages;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getWaitingMessages() {
        return waitingMessages;
    }
}
