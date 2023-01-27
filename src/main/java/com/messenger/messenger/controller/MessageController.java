package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.MessageService;
import com.messenger.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "change")
    public ResponseEntity<Boolean> isStatusNew(HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(messageService.isStatusChanged(userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/status")
    public ResponseEntity<List<ConversationStatusDto>> getConversationStatus(HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(messageService.getConversationStatus(userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/new/{conversationId}")
    public ResponseEntity<List<MessageDto>> getNewMessages(long conversationId, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(messageService.getNewMessages(userOptional.get(), conversationId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/load/{conversationId}/{batchId}")
    public ResponseEntity<BatchDto> getConversationUpdate(long conversationId, long batchId, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()) {
            Optional<BatchDto> optionalBatchDto = messageService.load(userOptional.get(), conversationId, batchId);
            if (optionalBatchDto.isPresent()) {
                return ResponseEntity.ok(optionalBatchDto.get());
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Boolean> sendMessage(@RequestBody SendMessageDto sendMessageDto, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(messageService.send(userOptional.get(), sendMessageDto));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/addConversation")
    public ResponseEntity<Boolean> addConversation(HttpServletRequest request, @RequestBody List<UserDto> userDtos){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(messageService.addConversation(userOptional.get(), userDtos));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private Optional<User> authorize(HttpServletRequest request){
        return userService.findUserByHttpRequest(request);
    }
}
