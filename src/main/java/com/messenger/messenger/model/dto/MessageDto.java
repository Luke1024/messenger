package com.messenger.messenger.model.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private long conversationId;
    private long batchId;
    private String send;
    private String content;
    private boolean byUserReceiving;

    public MessageDto() { }

    public MessageDto(long conversationId, long batchId, String send, String content, boolean byUserReceiving) {
        this.conversationId = conversationId;
        this.batchId = batchId;
        this.send = send;
        this.content = content;
        this.byUserReceiving = byUserReceiving;
    }

    public long getConversationId() {
        return conversationId;
    }

    public long getBatchId() {
        return batchId;
    }

    public String getSend() {
        return send;
    }

    public String getContent() {
        return content;
    }

    public boolean isByUserReceiving() {
        return byUserReceiving;
    }
}
