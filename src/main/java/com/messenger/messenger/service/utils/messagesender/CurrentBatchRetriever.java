package com.messenger.messenger.service.utils.messagesender;

import com.messenger.messenger.model.entity.MessageBatchDay;

import java.time.LocalDate;
import java.util.List;

public class CurrentBatchRetriever {

    public MessageBatchDay getBatch(List<MessageBatchDay> messageBatchDays, LocalDate today){
        if(messageBatchDays.isEmpty()){
            MessageBatchDay newMessageBatchDay = new MessageBatchDay(0, LocalDate.now());
            messageBatchDays.add(newMessageBatchDay);
            return newMessageBatchDay;
        }
        MessageBatchDay lastMessageBatch = messageBatchDays.get(messageBatchDays.size()-1);
        if(lastMessageBatch.getSend().isEqual(today)){
            return lastMessageBatch;
        } else {
            MessageBatchDay newBatch = new MessageBatchDay(generateBatchId(messageBatchDays), LocalDate.now());
            messageBatchDays.add(newBatch);
            return newBatch;
        }
    }

    private long generateBatchId(List<MessageBatchDay> messageBatchDays){
        return messageBatchDays.get(messageBatchDays.size()-1).getId()+1;
    }
}
