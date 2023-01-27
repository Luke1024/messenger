package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping(value = "change")
    public ResponseEntity<Boolean> isStatusNew(HttpServletRequest request){
        return messageService.isStatusChange(request);
    }

    @PostMapping(value = "/status")
    public ResponseEntity<List<ConversationStatusDto>> getConversationStatus(HttpServletRequest request){
        return messageService.getConversationStatus(request);
    }

    @PostMapping(value = "/new/{conversationId}")
    public ResponseEntity<List<MessageDto>> getMessages(long conversationId, HttpServletRequest request){
        return messageService.getNewMessages(conversationId, request);
    }

    @PostMapping(value = "/load/{conversationId}/{batchId}")
    public ResponseEntity<BatchDto> getConversationUpdate(long conversationId, long batchId, HttpServletRequest request){
        return messageService.load(conversationId, batchId, request);
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Boolean> sendMessage(@RequestBody SendMessageDto sendMessageDto, HttpServletRequest request){
        return messageService.send(sendMessageDto, request);
    }

    @PostMapping(value = "/addConversation")
    public ResponseEntity<Boolean> addConversation(HttpServletRequest request, @RequestBody List<UserDto> userDtos){
        return messageService.addConversation(request, userDtos);
    }
}
