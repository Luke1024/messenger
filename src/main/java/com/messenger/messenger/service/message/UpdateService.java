package com.messenger.messenger.service.message;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.dto.UserStatusDto;
import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.UpdateDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.repository.MessageRepository;
import com.messenger.messenger.service.SettingsService;
import com.messenger.messenger.service.conversation.ConversationService;
import com.messenger.messenger.service.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UpdateService {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageRepository messageRepository;

    public UpdateDto update(RequestDto requestDto){
        if(requestDto.isLoadFromSelectedTime()){
            return loadByTime(requestDto);
        } else {
            return loadStandardMessagePack(requestDto);
        }
    }

    private UpdateDto loadByTime(RequestDto requestDto){
        LocalDateTime fromTime = requestDto.getLoadFrom();


    }

    private UpdateDto loadStandardMessagePack(RequestDto requestDto, User user){

        List<UserStatusDto> userStatusDtos = new ArrayList<>();
        List<MessageDto> messageDtos = new ArrayList<>();

        Optional<Conversation> optionalConversation = getConversation(requestDto.getOpenedConversation());
        if(optionalConversation.isPresent()){
            messageDtos = loadRequestedMessages(optionalConversation.get(), requestDto);
        }
        return new UpdateDto(userStatusDtos, messageDtos);
    }

    private List<MessageDto> loadRequestedMessages(Conversation conversation, RequestDto requestDto){
        int messageCount = getMessageCount(requestDto.getMessagePackedCountToLoad());
        return getLastMessages(conversation, messageCount);
    }

    private List<MessageDto> getLastMessages(Conversation conversation, int messageCount){
        return messageMapper.mapToDtoList(conversation.getMessages().subList(messageCount, conversation.getMessages().size()-1));
    }

    private int getMessageCount(int messagePackedCountToLoad){
        return messagePackedCountToLoad * settingsService.messageCountInPacked;
    }

    private Optional<Conversation> getConversation(long conversationId){
        return conversationService.getConversation(conversationId);
    }
}
