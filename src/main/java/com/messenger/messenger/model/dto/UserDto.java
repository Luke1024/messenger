package com.messenger.messenger.model.dto;

public class UserDto {
    private Long userId;
    private String userName;

    public UserDto(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
