package com.messenger.messenger.service.utils.temp;

import com.messenger.messenger.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataGeneratorTest {

    @Autowired
    private DataGenerator dataGenerator;

    @Autowired
    private UserService userService;

    @Test
    public void generateDataForUser(){

    }
}