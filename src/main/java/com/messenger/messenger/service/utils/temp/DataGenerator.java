package com.messenger.messenger.service.utils.temp;

import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.ConversationService;
import com.messenger.messenger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataGenerator {

    UserDataDto userTom = new UserDataDto("Tom", "tom_pass");
    UserDataDto userRob = new UserDataDto("Rob", "rob_pass");
    UserDataDto userBob = new UserDataDto("Bob", "bob_pass");
    UserDataDto userMartin = new UserDataDto("Martin", "martin_pass");

    @Autowired
    private ConversationService conversationService;

    private Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    public void generateDataForUser(User newUser, UserService userService){
        registerAdditionalUsers(userService);
        List<User> additionalUsers = userService.users.stream()
                .filter(user -> newUser != user).collect(Collectors.toList());

        if(createConversationBetweenAdditionalUsersAndNewUser(newUser, additionalUsers)){
            logger.info("Conversations added");
        }
        logger.info("Conversations created total: " + conversationService.conversations.size());
        populateConversationWithMessages(newUser);
    }

    private void registerAdditionalUsers(UserService userService){
        userService.register(userTom);
        userService.register(userRob);
        userService.register(userBob);
        userService.register(userMartin);
    }

    private boolean createConversationBetweenAdditionalUsersAndNewUser(User newUser, List<User> additionalUsers){
        for(int i=0; i<additionalUsers.size()-1; i++) {
            conversationService.addConversation(newUser, Arrays.asList(additionalUsers.get(i)));
        }
        conversationService.addConversation(newUser, additionalUsers.subList(0,additionalUsers.size()-1));
        conversationService.addConversation(newUser, additionalUsers.subList(0, additionalUsers.size()-2));
        return true;
    }

    private void populateConversationWithMessages(User newUser){
        for(Conversation conversation : conversationService.conversations){
            populateConversation(conversation);
        }
    }

    private void populateConversation(Conversation conversation){
        for(User userInConversation : conversation.getUsersInConversation()){
            conversationService.send(userInConversation, new SendMessageDto(conversation.getId(),shortMessage));
            conversationService.send(userInConversation, new SendMessageDto(conversation.getId(),midMessage));
            conversationService.send(userInConversation, new SendMessageDto(conversation.getId(),longMessage));
        }
    }

    private String shortMessage = "Lorem ipsum dolor sit amet";
    private String midMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
    private String longMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Pharetra diam sit amet nisl suscipit. In eu mi bibendum neque egestas congue. Tellus integer feugiat scelerisque varius morbi enim nunc. Feugiat in fermentum posuere urna nec tincidunt praesent. Egestas egestas fringilla phasellus faucibus scelerisque. Urna cursus eget nunc scelerisque. Mi proin sed libero enim. Orci dapibus ultrices in iaculis. Sed lectus vestibulum mattis ullamcorper velit sed ullamcorper. Odio morbi quis commodo odio. Suspendisse interdum consectetur libero id faucibus nisl. Morbi blandit cursus risus at ultrices mi tempus imperdiet nulla.";
}
