package com.messenger.messenger.model.dto;

public class SendMessageDto {
    private long conversationId;
    private String content;

    public SendMessageDto() {
    }

    public SendMessageDto(long conversationId, String content) {
        this.conversationId = conversationId;
        this.content = content;
    }

    public long getConversationId() {
        return conversationId;
    }

    public String getContent() {
        return content;
    }
}
