package com.messenger.messenger.mapper;

import com.messenger.messenger.model.dto.BatchDto;
import com.messenger.messenger.model.entity.MessageBatchDay;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void checkDateTimeFormatting(){

        MessageBatchDay messageBatchDay = new MessageBatchDay(0, LocalDate.of(2023, 4,13));
        User user = new User("","","");
        Optional<BatchDto> batchDto = messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(Optional.of(messageBatchDay), user);

        Assert.assertEquals("2023-04-13",batchDto.get().getSend());
    }
}