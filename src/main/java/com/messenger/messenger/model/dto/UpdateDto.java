package com.messenger.messenger.model.dto;

import java.util.List;

public class UpdateDto {
    private List<UserStatusDto> userStatusDtos;
    private List<MessageDto> messageDtos;

    public UpdateDto() {
    }

    public UpdateDto(List<UserStatusDto> userStatusDtos, List<MessageDto> messageDtos) {
        this.userStatusDtos = userStatusDtos;
        this.messageDtos = messageDtos;
    }

    public List<UserStatusDto> getUserStatusDtos() {
        return userStatusDtos;
    }

    public List<MessageDto> getMessageDtos() {
        return messageDtos;
    }
}
