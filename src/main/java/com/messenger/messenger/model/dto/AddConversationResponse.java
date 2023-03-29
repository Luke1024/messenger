package com.messenger.messenger.model.dto;

public class AddConversationResponse {
    private boolean status;
    private String message;

    public AddConversationResponse() {
    }

    public AddConversationResponse(boolean status, String message) {
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
