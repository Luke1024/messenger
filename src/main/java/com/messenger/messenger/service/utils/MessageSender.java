package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MessageSender {

    public boolean send(User userRequesting, SendMessageDto sendMessageDto){
        Optional<Conversation> optionalConversation = findById(sendMessageDto.getConversationId(),
                getUserConversations(userRequesting));

        if(optionalConversation.isPresent()){
            Message newMessage = new Message(
                    sendMessageDto.getContent(),
                    LocalTime.now(),
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
        MessageBatchDay currenctBatch = getCurrentBatch(conversation.getMessageBatchDays());
        addMessageToBatch(currenctBatch, newMessage);
        informUsers(newMessage, conversation.getUsersInConversation(), conversation);
    }

    private MessageBatchDay getCurrentBatch(List<MessageBatchDay> messageBatchDays){
        LocalDate today = LocalDate.now();
        MessageBatchDay lastMessageBatch = messageBatchDays.get(messageBatchDays.size()-1);
        if(lastMessageBatch.getLocalDate().isEqual(today)){
            return lastMessageBatch;
        } else {
            MessageBatchDay newBatch = new MessageBatchDay(generateBatchId(messageBatchDays));
            messageBatchDays.add(newBatch);
            return newBatch;
        }
    }

    private long generateBatchId(List<MessageBatchDay> messageBatchDays){
        return messageBatchDays.get(messageBatchDays.size()-1).getId()+1;
    }

    private void addMessageToBatch(MessageBatchDay messageBatchDay, Message message){
        if(messageBatchDay.getMessages().isEmpty()){
            message.setId(0);
            message.setMessageBatchDay(messageBatchDay);
        } else {
            List<Message> messages = messageBatchDay.getMessages();
            long newId = messages.get(messages.size()-1).getId() + 1;
            message.setId(newId);
            message.setMessageBatchDay(messageBatchDay);
        }
        messageBatchDay.getMessages().add(message);
    }

    private void informUsers(Message message, List<User> usersInConversation, Conversation conversation){
        usersInConversation.stream().forEach(user -> addWaitingMessage(user, message, conversation));
        usersInConversation.stream().filter(user -> user != message.getByUser())
                .forEach(user -> addNotification(user, message, conversation));
    }

    private void addWaitingMessage(User user, Message message, Conversation currentConversation){
        ConversationStatus conversationStatus = user.getConversations().get(currentConversation);
        if(conversationStatus != null){
            conversationStatus.getWaitingMessages().add(message);
        }
    }

    private void addNotification(User user, Message message, Conversation currentConversation){
        ConversationStatus conversationStatus = user.getConversations().get(currentConversation);
        if(conversationStatus != null) {
            conversationStatus.addNotification();
        }
    }
}
