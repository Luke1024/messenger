package com.messenger.messenger.model.dto;
public class AuthorizationResponseDto {
    private boolean status;
    private String message;

    public AuthorizationResponseDto() {
    }

    public AuthorizationResponseDto(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
