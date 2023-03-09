package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MessageSender {

    @Autowired
    private SettingsService settingsService;

    public boolean send(User userRequesting, SendMessageDto sendMessageDto){
        Optional<Conversation> optionalConversation = findById(sendMessageDto.getConversationId(), getUserConversations(userRequesting));
        if(optionalConversation.isPresent()){
            Message newMessage = new Message(
                    sendMessageDto.getContent(),
                    LocalDateTime.now(),
                    userRequesting,
                    optionalConversation.get());

            addMessage(optionalConversation.get(), newMessage);
            return true;
        } else return false;
    }

    private List<Conversation> getUserConversations(User user){
        return user.getConversations().entrySet().stream()
                .map(entry -> entry.getKey()).collect(Collectors.toList());
    }

    private Optional<Conversation> findById(long id, List<Conversation> conversations){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }

    private void addMessage(Conversation conversation, Message newMessage){
        MessageBatch currenctBatch = getCurrentBatch(conversation.getMessageBatches());
        addMessageToBatch(currenctBatch, newMessage);
        informUsers(newMessage, conversation.getUsersInConversation());
    }

    private MessageBatch getCurrentBatch(List<MessageBatch> messageBatches){
        if(messageBatches.isEmpty()){
            MessageBatch newMessageBatch = new MessageBatch(0);
            messageBatches.add(newMessageBatch);
            return newMessageBatch;
        }
        if( ! messageBatches.isEmpty()){
            MessageBatch lastMessageBatch = messageBatches.get(messageBatches.size()-1);
            if(lastMessageBatch.getMessages().size() > settingsService.messageCountInBatch-1){
                MessageBatch newMessageBatch = new MessageBatch(generateBatchId(messageBatches));
                messageBatches.add(newMessageBatch);
                return newMessageBatch;
            }
        }
        return messageBatches.get(messageBatches.size()-1);
    }

    private long generateBatchId(List<MessageBatch> messageBatches){
        return messageBatches.get(messageBatches.size()-1).getId()+1;
    }

    private void addMessageToBatch(MessageBatch messageBatch, Message message){
        if(messageBatch.getMessages().isEmpty()){
            message.setId(0);
            message.setMessageBatch(messageBatch);
        } else {
            List<Message> messages = messageBatch.getMessages();
            long newId = messages.get(messages.size()-1).getId() + 1;
            message.setId(newId);
            message.setMessageBatch(messageBatch);
        }
        messageBatch.getMessages().add(message);
    }

    private void informUsers(Message message, List<User> usersInConversation){
        usersInConversation.stream().forEach(user -> addWaitingMessage(user, message));
        usersInConversation.stream().filter(user -> user != message.getByUser())
                .forEach(user -> addNotification(user, message));
    }

    private void addWaitingMessage(User user, Message message){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            conversationStatus.getWaitingMessages().add(message);
        }
    }

    private void addNotification(User user, Message message){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null) {
            conversationStatus.addNotification();
        }
    }
}
