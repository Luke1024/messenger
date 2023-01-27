package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    public ResponseEntity<Boolean> isStatusChange(HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.get().isConversationStatusChanged());
        } else {
            return ResponseEntity.ok(false);
        }
    }

    public ResponseEntity<List<ConversationStatusDto>> getConversationStatus(HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(getStatusOfAllConversations(userOptional.get()));
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    public ResponseEntity<List<MessageDto>> getNewMessages(long conversationId, HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()){
            Optional<Conversation> optionalConversation = conversationService.findById(conversationId);
            if(optionalConversation.isPresent()){
                return ResponseEntity.ok(messageMapper.mapToDtoList(optionalConversation.get().getOnlyNewMessages(userOptional.get())));
            }
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<BatchDto> load(long conversationId, long batchId, HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()) {
            Optional<Conversation> optionalConversation = conversationService.findById(conversationId);
            if (optionalConversation.isPresent()) {
                Optional<BatchDto> optionalBatchDto =
                        messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                                optionalConversation.get().getMessageBatch(userOptional.get(), (int) batchId));
            }
        }
        return ResponseEntity.badRequest().build();
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

    public ResponseEntity<Boolean> send(SendMessageDto sendMessageDto, HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(sendMessage(sendMessageDto, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean sendMessage(SendMessageDto sendMessageDto, User user){
        Optional<Conversation> optionalConversation = conversationService.findById(sendMessageDto.getConversationId());
        if(optionalConversation.isPresent()){
            optionalConversation.get().addWaitingMessage(new Message(
                    sendMessageDto.getContent(),
                    LocalDateTime.now(),
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
