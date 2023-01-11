package com.messenger.messenger.model.dto;

import java.util.List;

public class UpdateDto {
    private List<ConversationStatusDto> conversationStatusDtos;
    private List<MessageDto> messageDtos;

    public UpdateDto() {
    }

    public UpdateDto(List<ConversationStatusDto> conversationStatusDtos, List<MessageDto> messageDtos) {
        this.conversationStatusDtos = conversationStatusDtos;
        this.messageDtos = messageDtos;
    }

    public List<ConversationStatusDto> getConversationStatusDtos() {
        return conversationStatusDtos;
    }

    public List<MessageDto> getMessageDtos() {
        return messageDtos;
    }
}
