package com.messenger.messenger.model.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private long conversationId;
    private long batchId;
    private LocalDateTime send;
    private String content;

    public MessageDto() { }

    public MessageDto(long conversationId, long batchId, LocalDateTime send, String content) {
        this.conversationId = conversationId;
        this.batchId = batchId;
        this.send = send;
        this.content = content;
    }

    public long getConversationId() {
        return conversationId;
    }

    public long getBatchId() {
        return batchId;
    }

    public LocalDateTime getSend() {
        return send;
    }

    public String getContent() {
        return content;
    }
}
