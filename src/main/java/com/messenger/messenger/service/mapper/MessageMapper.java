package com.messenger.messenger.service.mapper;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageMapper {

    public List<MessageDto> mapToDtoList(List<Message> messages){
        return messages.stream().map(message -> mapToDto(message)).collect(Collectors.toList());
    }

    private MessageDto mapToDto(Message message){
        return new MessageDto(message.getConversation().getId(), message.getMessageBatch().getId(), message.getSend(), message.getContent());
    }
}
