package space.zeinab.demo.customProcessorService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String USER_INPUT_TOPIC = "user-input-topic";
    public static final String USER_OUTPUT_TOPIC = "user-output-topic";

    public static final String IOT_INPUT_TOPIC = "iot-input-topic";
    public static final String IOT_OUTPUT_TOPIC = "iot-output-topic";

    @Bean
    public NewTopic userInputTopicBuilder() {
        return TopicBuilder.name(USER_INPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userOutputTopicBuilder() {
        return TopicBuilder.name(USER_OUTPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic iotInputTopicBuilder() {
        return TopicBuilder.name(IOT_INPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic iotOutputTopicBuilder() {
        return TopicBuilder.name(IOT_OUTPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
