package com.messenger.messenger.model.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private long conversationId;
    private LocalDateTime send;
    private String message;

    public MessageDto() { }

    public MessageDto(long conversationId, LocalDateTime send, String message) {
        this.conversationId = conversationId;
        this.send = send;
        this.message = message;
    }

    public long getConversationId() {
        return conversationId;
    }

    public LocalDateTime getSend() {
        return send;
    }

    public String getMessage() {
        return message;
    }
}
