package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserFinderTest {

    @Autowired
    private UserFinder userFinder;

    @Test
    public void findUsersByNameToDto() {
        List<User> users = new ArrayList<>();

        User user1 = new User("Martin","","");
        User user2 = new User("Bob","","");
        User user3 = new User("Rob", "", "");

        Assert.assertEquals("Martin", userFinder.findUsersByNameToDto("ma", Arrays.asList(user1, user2, user3)).get(0).getUserName());
    }
}