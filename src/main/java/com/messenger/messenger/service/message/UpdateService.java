package com.messenger.messenger.service.message;

import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.UpdateDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.SettingsService;
import com.messenger.messenger.service.conversation.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UpdateService {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private ConversationService conversationService;

    public UpdateDto update(RequestDto requestDto){
        if(requestDto.isLoadNew()){
            loadNewMessages(requestDto);
        } else {
            loadStandardMessagePacked(requestDto);
        }
    }

    private UpdateDto loadNewMessages(RequestDto requestDto){

    }

    private UpdateDto loadStandardMessagePacked(RequestDto requestDto, User user){
        int messageCount = getMessageCount(requestDto.getMessagePackedCountToLoad());
        Optional<Conversation> optionalConversation = getConversation(requestDto.getOpenedConversation());
    }

    private int getMessageCount(int messagePackedCountToLoad){
        return messagePackedCountToLoad * settingsService.messageCountInPacked;
    }

    private Optional<Conversation> getConversation(long conversationId){
        return conversationService.getConversation(conversationId);
    }
}
