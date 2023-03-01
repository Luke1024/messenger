package com.messenger.messenger.model.dto;

public class UserDto {
    private Long userId;
    private String userName;
    private long defaultConversationId;

    public UserDto(Long userId, String userName, long defaultConversationId) {
        this.userId = userId;
        this.userName = userName;
        this.defaultConversationId = defaultConversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public long getDefaultConversationId() {
        return defaultConversationId;
    }
}
