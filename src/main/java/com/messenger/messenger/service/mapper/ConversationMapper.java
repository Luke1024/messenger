package com.messenger.messenger.service.mapper;

import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConversationMapper {

    public List<ConversationStatusDto> mapToConversationStatusDto(User userRequesting,
            Map<Conversation, ConversationStatus> conversationMap){
        return conversationMap.entrySet().stream()
                .map(conversationEntry -> convertToConversationStatusDto(userRequesting,conversationEntry.getKey(),
                        conversationEntry.getValue()))
                .collect(Collectors.toList());
    }

    private ConversationStatusDto convertToConversationStatusDto(
            User userRequesting,
            Conversation conversation,
            ConversationStatus conversationStatus){
        return new ConversationStatusDto(conversation.getId(),
                conversation.getUsersInConversation().stream().filter(user -> user != userRequesting)
                        .map(user -> user.getDto()).collect(Collectors.toList()),
                conversationStatus.getNotificationCount(),
                conversation.isDirect());
    }
}
