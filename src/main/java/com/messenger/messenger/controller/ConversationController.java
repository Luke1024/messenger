package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.ConversationService;
import com.messenger.messenger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200/",allowCredentials = "true")
@RestController
@RequestMapping(value = "/message")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(ConversationController.class);

    @GetMapping(value = "change")
    public ResponseEntity<Boolean> isStatusNew(HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(conversationService.isStatusChanged(userOptional.get()));
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping(value = "/status")
    public ResponseEntity<List<ConversationStatusDto>> getConversationStatus(HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(conversationService.getStatus(userOptional.get()));
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping(value = "/new/{conversationId}")
    public ResponseEntity<List<MessageDto>> getNewMessages(@PathVariable long conversationId, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(conversationService.getNewMessages(userOptional.get(), conversationId));
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping(value = "loadLast/{conversationId}")
    public ResponseEntity<BatchDto> getLastMessageBatch(@PathVariable long conversationId, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            Optional<BatchDto> optionalBatchDto = conversationService.loadLastBatch(userOptional.get(), conversationId);
            if(optionalBatchDto.isPresent()){
                return ResponseEntity.ok(optionalBatchDto.get());
            }
        }
        return ResponseEntity.ok(new BatchDto());
    }

    @PostMapping(value = "/load/{conversationId}/{batchId}")
    public ResponseEntity<BatchDto> getMessageBatch(@PathVariable long conversationId, @PathVariable int batchId, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()) {
            Optional<BatchDto> optionalBatchDto = conversationService.loadBatch(userOptional.get(), conversationId, batchId);
            if (optionalBatchDto.isPresent()) {
                return ResponseEntity.ok(optionalBatchDto.get());
            }
        }
        return ResponseEntity.ok(new BatchDto());
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Boolean> sendMessage(@RequestBody SendMessageDto sendMessageDto, HttpServletRequest request){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(conversationService.send(userOptional.get(), sendMessageDto));
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping(value = "/addConversation")
    public ResponseEntity<Boolean> addConversation(HttpServletRequest request, @RequestBody List<UserDto> userDtos){
        Optional<User> userOptional = authorize(request);
        if(userOptional.isPresent()){
            List<User> userFound = userService.findUsersByDto(userDtos);
            return ResponseEntity.ok(conversationService.addConversation(userOptional.get(), userFound));
        } else {
            return ResponseEntity.ok(false);
        }
    }

    private Optional<User> authorize(HttpServletRequest request){
        return userService.findUserByHttpRequest(request);
    }
}
