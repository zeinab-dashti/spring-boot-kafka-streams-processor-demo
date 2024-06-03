package space.zeinab.demo.customProcessorService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Properties;

@Configuration
@EnableKafka
@EnableKafkaStreams
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

    public static Properties setup(String applicationId, String relativePath) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        String stateDir = System.getProperty("user.dir") + "/" + relativePath;
        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);

        return props;
    }
}
