package com.messenger.messenger.service.message;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.UpdateDto;
import com.messenger.messenger.model.entity.conversation.Conversation;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.repository.MessageRepository;
import com.messenger.messenger.service.SettingsService;
import com.messenger.messenger.service.conversation.ConversationService;
import com.messenger.messenger.service.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(requestDto.isLoadNew()){
            return loadNewOnly(requestDto);
        } else {
            return loadMessagePack(requestDto);
        }
    }

    private UpdateDto loadNewOnly(RequestDto requestDto){

    }

    private UpdateDto loadMessagePack(RequestDto requestDto, User user){

        List<ConversationStatusDto> conversationStatusDtos = new ArrayList<>();
        List<MessageDto> messageDtos = new ArrayList<>();

        Optional<Conversation> optionalConversation = getConversation(requestDto.getOpenedConversation());
        if(optionalConversation.isPresent()){
            messageDtos = loadRequestedMessages(optionalConversation.get(), requestDto);
        }
        return new UpdateDto(conversationStatusDtos, messageDtos);
    }

    private List<MessageDto> loadRequestedMessages(Conversation conversation, RequestDto requestDto){
        int messageCount = getMessageCount(requestDto.getMessagePackedIndex());
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
