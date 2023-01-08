package com.messenger.messenger.model.dto;

public class UserDataDto {
    private String userName;
    private String password;

    public UserDataDto() {
    }

    public UserDataDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
