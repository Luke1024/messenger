package com.messenger.messenger.mapper;

import com.messenger.messenger.model.dto.BatchDto;
import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.MessageBatchDay;
import com.messenger.messenger.model.entity.User;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageMapper {

    public List<MessageDto> mapToDtoList(List<Message> messages, User userRequesting){
        return messages.stream().map(message -> mapToDto(message, userRequesting)).collect(Collectors.toList());
    }

    private MessageDto mapToDto(Message message, User userRequesting){
        return new MessageDto(
                message.getConversation().getId(),
                message.getMessageBatchDay().getId(),
                message.getSend().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
                message.getContent(),
                userRequesting==message.getByUser());
    }

    public Optional<BatchDto> mapToBatchDtoOptionalFromMessageBatchOptional(Optional<MessageBatchDay> messageBatch, User userRequesting){
        if(messageBatch.isPresent()){
            return Optional.of(new BatchDto(messageBatch.get().getId(),
                    messageBatch.get().getSend().toString(),
                    mapToDtoList(messageBatch.get().getMessages(),
                            userRequesting)));
        } else {
            return Optional.empty();
        }
    }
}
