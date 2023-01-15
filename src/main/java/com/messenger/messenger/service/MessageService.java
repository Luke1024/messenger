package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.MessageBatch;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.model.entity.conversation.Conversation;
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
        List<Conversation> conversations = user.getConversations();
        List<ConversationStatusDto> conversationStatusDtos = new ArrayList<>();
        for(Conversation conversation : conversations){
            Optional<ConversationStatusDto> conversationStatusDto = conversation.getConversationStatus(user);
            if(conversationStatusDto.isPresent()){
                conversationStatusDtos.add(conversationStatusDto.get());
            }
        }
        return conversationStatusDtos;
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
            optionalConversation.get().addMessage(new Message(
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
