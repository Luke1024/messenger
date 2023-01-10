package com.messenger.messenger.model.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private long conversationId;
    private LocalDateTime send;
    private String content;

    public MessageDto() { }

    public MessageDto(long conversationId, LocalDateTime send, String content) {
        this.conversationId = conversationId;
        this.send = send;
        this.content = content;
    }

    public long getConversationId() {
        return conversationId;
    }

    public LocalDateTime getSend() {
        return send;
    }

    public String getContent() {
        return content;
    }
}
