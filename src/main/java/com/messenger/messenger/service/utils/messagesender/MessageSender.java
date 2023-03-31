package com.messenger.messenger.service.utils.messagesender;

import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class MessageSender {

    private CurrentBatchRetriever currentBatchRetriever = new CurrentBatchRetriever();

    public boolean send(User userRequesting, SendMessageDto sendMessageDto, Conversation conversation){
        Message newMessage = new Message(
                sendMessageDto.getContent(),
                LocalTime.now(),
                userRequesting,
                conversation);
        addMessage(conversation, newMessage);
        return true;
    }

    private void addMessage(Conversation conversation, Message newMessage){
        MessageBatchDay currenctBatch = getCurrentBatch(conversation.getMessageBatchDays());
        addMessageToBatch(currenctBatch, newMessage);
        informUsers(newMessage, conversation.getUsersInConversation(), conversation);
    }

    private MessageBatchDay getCurrentBatch(List<MessageBatchDay> messageBatchDays){
        return currentBatchRetriever.getBatch(messageBatchDays, LocalDate.now());
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
