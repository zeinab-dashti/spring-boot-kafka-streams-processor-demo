package space.zeinab.demo.customProcessorService.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;
import space.zeinab.demo.customProcessorService.model.UserActivity;

import java.util.concurrent.CompletableFuture;

@Service
public class UserActivityProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserActivityProducer(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<SendResult<String, String>> addActivity(UserActivity userActivity) {
        return kafkaTemplate
                .send(MessageBuilder.withPayload(userActivity.getActivity())
                        .setHeader(KafkaHeaders.KEY, userActivity.getUserId())
                        .setHeader(KafkaHeaders.TOPIC, KafkaConfig.USER_INPUT_TOPIC)
                        .build());
    }
}