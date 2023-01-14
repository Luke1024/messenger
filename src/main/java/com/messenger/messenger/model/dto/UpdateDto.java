package com.messenger.messenger.model.dto;

import com.messenger.messenger.model.entity.MessageBatch;

import java.util.List;

public class UpdateDto {
    private List<ConversationStatusDto> conversationStatusDtos;
    private List<MessageDto> messageDtos;
    private MessageBatch messageBatch;

    public UpdateDto() {
    }

    public UpdateDto(List<ConversationStatusDto> conversationStatusDtos, List<MessageDto> messageDtos, MessageBatch messageBatch) {
        this.conversationStatusDtos = conversationStatusDtos;
        this.messageDtos = messageDtos;
        this.messageBatch = messageBatch;
    }

    public List<ConversationStatusDto> getConversationStatusDtos() {
        return conversationStatusDtos;
    }

    public List<MessageDto> getMessageDtos() {
        return messageDtos;
    }

    public MessageBatch getMessageBatch() {
        return messageBatch;
    }
}
