package com.messenger.messenger.model.dto;

import java.util.List;

public class BatchDto {
    private long id;
    private List<MessageDto> messageDtoList;

    public BatchDto() {
    }

    public BatchDto(long id, List<MessageDto> messageDtoList) {
        this.id = id;
        this.messageDtoList = messageDtoList;
    }

    public long getId() {
        return id;
    }

    public List<MessageDto> getMessageDtoList() {
        return messageDtoList;
    }
}
