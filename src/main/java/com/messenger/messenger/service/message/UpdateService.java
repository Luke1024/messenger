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

    public UpdateDto update(RequestDto requestDto, User user){
        if(requestDto.isLoadNew()){
            return loadNewOnly(requestDto, user);
        } else {
            return loadMessagePack(requestDto, user);
        }
    }

    private UpdateDto loadNewOnly(RequestDto requestDto, User user){
        List<ConversationStatusDto> conversationStatusDtos = new ArrayList<>();
        List<MessageDto> messageDtos = new ArrayList<>();

        Optional<Conversation> optionalConversation = getConversation(requestDto.getOpenedConversation());
        if(optionalConversation.isPresent()) {
            messageDtos = messageMapper.mapToDtoList(optionalConversation.get()
                    .getOnlyNewMessages(user));
        }
        return new UpdateDto(conversationStatusDtos, messageDtos);
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
        int messageCount = getMessageCount(requestDto.getMessageBatchIndex());
        return getLastMessages(conversation, messageCount);
    }

    private List<MessageDto> getLastMessages(Conversation conversation, int indexFrom, int indexTo){
        return messageMapper.mapToDtoList(conversation.getMessages(user, indexFrom, indexTo));
    }

    private int getMessageCount(int messagePackedCountToLoad){
        return messagePackedCountToLoad * settingsService.messageCountInPacked;
    }

    private Optional<Conversation> getConversation(long conversationId){
        return conversationService.getConversation(conversationId);
    }
}
