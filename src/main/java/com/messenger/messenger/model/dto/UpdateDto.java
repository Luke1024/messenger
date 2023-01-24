package com.messenger.messenger.model.dto;

import java.util.List;

public class UpdateDto {
    private List<ConversationStatusDto> conversationStatusDtos;
    private List<MessageDto> messageDtos;
    private BatchDto batchDto;

    public UpdateDto() {
    }

    public UpdateDto(List<ConversationStatusDto> conversationStatusDtos, List<MessageDto> messageDtos, BatchDto batchDto) {
        this.conversationStatusDtos = conversationStatusDtos;
        this.messageDtos = messageDtos;
        this.batchDto = batchDto;
    }

    public List<ConversationStatusDto> getConversationStatusDtos() {
        return conversationStatusDtos;
    }

    public List<MessageDto> getMessageDtos() {
        return messageDtos;
    }

    public BatchDto getBatchDto() {
        return batchDto;
    }
}
