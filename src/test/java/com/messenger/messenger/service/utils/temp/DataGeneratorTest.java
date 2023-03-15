package com.messenger.messenger.service.utils.temp;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataGeneratorTest {

    @Autowired
    private DataGenerator dataGenerator;

    @Autowired
    private UserService userService;

    @Test
    public void generateDataForUser(){
        userService.register(new UserDataDto("master", "flamaster"));

        User masterFlamaster = userService.users.get(0);

        dataGenerator.generateDataForUser(masterFlamaster, userService);

        Assert.assertTrue(masterFlamaster.getConversations().size()==4);
    }
}