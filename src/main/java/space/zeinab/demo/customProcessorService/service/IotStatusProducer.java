package space.zeinab.demo.customProcessorService.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;
import space.zeinab.demo.customProcessorService.model.IotStatus;

import java.util.concurrent.CompletableFuture;

@Service
public class IotStatusProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public IotStatusProducer(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<SendResult<String, String>> addStatus(IotStatus iotStatus) {
        return kafkaTemplate
                .send(MessageBuilder.withPayload(iotStatus.getStatus())
                        .setHeader(KafkaHeaders.KEY, iotStatus.getKey())
                        .setHeader(KafkaHeaders.TOPIC, KafkaConfig.IOT_INPUT_TOPIC)
                        .build());
    }
}
