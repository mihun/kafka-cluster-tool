package com.kafka.consumer;

import com.kafka.validation.ValidationResult;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;


@Component
public class ConsumerValidationProcessor {

    public ValidationResult process(KafkaConsumer backupConsumer, KafkaConsumer sourceConsumer, TopicPartition topicPartition){

        ConsumerUnitChain consumerUnitChain = new ConsumerUnitChain(backupConsumer, sourceConsumer, topicPartition);
        return processConsumerUnitChain(consumerUnitChain);
    }

    private ValidationResult processConsumerUnitChain(ConsumerUnitChain consumerUnitChain){
        ValidationResult result =
                consumerUnitChain
                        .prepare()
                        .start()
                        .buffer()
                        .validate();

        switch (result){
            case SUCCESSFUL_BUFFER_PORTION:
                return consumerUnitChain.isFinished() ? ValidationResult.SUCCESSFUL : processConsumerUnitChain(consumerUnitChain);
            default:
                return result;
        }
    }
}
