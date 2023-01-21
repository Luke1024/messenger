package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.conversation.ConversationService;
import com.messenger.messenger.service.mapper.MessageMapper;
import com.messenger.messenger.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserService userService;

    public ResponseEntity<UpdateDto> update(RequestDto requestDto, HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(executeUpdating(requestDto, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public UpdateDto executeUpdating(RequestDto requestDto, User user){
        if(requestDto.isLoadNew()){
            return loadNewOnly(requestDto, user);
        } else {
            return loadMessageBatch(requestDto, user);
        }
    }

    private UpdateDto loadNewOnly(RequestDto requestDto, User user){
        List<ConversationStatusDto> conversationStatusDtos = getStatusOfAllConversations(user);
        List<MessageDto> messageDtos = new ArrayList<>();

        Optional<Conversation> optionalConversation = conversationService.getConversation(requestDto.getOpenedConversation());
        if(optionalConversation.isPresent()) {
            messageDtos = messageMapper.mapToDtoList(optionalConversation.get()
                    .getOnlyNewMessages(user));
        }
        return new UpdateDto(conversationStatusDtos, messageDtos, null);
    }

    private List<ConversationStatusDto> getStatusOfAllConversations(User user){
        return user.getConversations()
                .entrySet().stream()
                .map(conversationEntry -> convertToConversationStatusDto(conversationEntry.getKey(), conversationEntry.getValue()))
                .collect(Collectors.toList());
    }

    private ConversationStatusDto convertToConversationStatusDto(Conversation conversation, ConversationStatus conversationStatus){
        return new ConversationStatusDto(conversation.getId(),
                conversation.getUsersInConversation().stream().map(user -> user.getDto()).collect(Collectors.toList()),
                conversationStatus.getNotificationCount());
    }

    private UpdateDto loadMessageBatch(RequestDto requestDto, User user){

        List<ConversationStatusDto> conversationStatusDtos = new ArrayList<>();
        MessageBatch messageBatch;

        Optional<Conversation> optionalConversation = conversationService.getConversation(requestDto.getOpenedConversation());
        if(optionalConversation.isPresent()){
            Optional<MessageBatch> optionalMessageBatch =
                    optionalConversation.get().getMessageBatch(user, requestDto.getMessageBatchIndex());
            if(optionalMessageBatch.isPresent()){
                return new UpdateDto(conversationStatusDtos, new ArrayList<>(), optionalMessageBatch.get());
            }
        }
        return new UpdateDto(conversationStatusDtos, new ArrayList<>(), null);
    }

    public ResponseEntity<Boolean> send(MessageDto messageDto, HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(sendMessage(messageDto, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean sendMessage(MessageDto messageDto, User user){
        Optional<Conversation> optionalConversation = conversationService.findById(messageDto.getConversationId());
        if(optionalConversation.isPresent()){
            optionalConversation.get().addWaitingMessage(new Message(
                    messageDto.getContent(),
                    messageDto.getSend(),
                    user,
                    optionalConversation.get())
            );
            return true;
        } else return false;
    }


    public ResponseEntity<Boolean> addConversation(HttpServletRequest request, List<UserDto> userDtos){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(conversationService.addConversation(userDtos, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
