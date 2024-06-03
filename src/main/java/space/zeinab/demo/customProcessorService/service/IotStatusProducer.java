package space.zeinab.demo.customProcessorService.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

import java.util.concurrent.CompletableFuture;

@Service
public class IotStatusProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public IotStatusProducer(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<SendResult<String, String>> addStatus(String value) {
        return kafkaTemplate
                .send(MessageBuilder.withPayload(value)
                        .setHeader(KafkaHeaders.KEY, "key1")
                        .setHeader(KafkaHeaders.TOPIC, KafkaConfig.IOT_INPUT_TOPIC)
                        .build());
    }
}
