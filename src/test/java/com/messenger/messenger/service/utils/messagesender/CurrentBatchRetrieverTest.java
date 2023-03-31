package com.messenger.messenger.service.utils.messagesender;

import com.messenger.messenger.model.entity.MessageBatchDay;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CurrentBatchRetrieverTest {

    private CurrentBatchRetriever currentBatchRetriever = new CurrentBatchRetriever();

    @Test
    public void testBatchSplittingByDate(){

        List<MessageBatchDay> emptyBatchList = new ArrayList<>();

        MessageBatchDay currentBatch = currentBatchRetriever.getBatch(emptyBatchList, LocalDate.now());

        assertTrue(currentBatch.getSend().isEqual(LocalDate.now()));
        assertEquals(1, emptyBatchList.size());

        currentBatchRetriever.getBatch(emptyBatchList, LocalDate.now());

        assertEquals(1, emptyBatchList.size());

        currentBatchRetriever.getBatch(emptyBatchList, LocalDate.now().plusDays(1));

        assertEquals(2, emptyBatchList.size());
    }
}