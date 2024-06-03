package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)

@EmbeddedKafka(partitions = 1, topics = {"user-input-topic", "user-output-topic"})
public class UserActivityInputPipelineITNew {


    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;
    private static KafkaStreams streams;


    @BeforeAll
    public static void setUp(@Autowired EmbeddedKafkaBroker embeddedKafka) {
        // Kafka producer configuration
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);

        // Kafka consumer configuration
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(KafkaConfig.USER_OUTPUT_TOPIC));

        // Kafka Streams configuration
        Properties streamsProps = KafkaConfig.setup("user-activity-app", "state/user-activity");
        streamsProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());

        Topology topology = UserActivityInputPipeline.buildSessionTopology();
        streams = new KafkaStreams(topology, streamsProps);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }


    @AfterAll
    public static void tearDown() {
        if (streams != null) {
            streams.close();
        }

        if (producer != null) {
            producer.close();
        }

        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    public void testSessionExpiration() {
        String key = "user1";
        String value = "activity";

        // Send initial record
        producer.send(new ProducerRecord<>(KafkaConfig.USER_INPUT_TOPIC, key, value));

        // Simulate passage of time
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Send second record
        producer.send(new ProducerRecord<>(KafkaConfig.USER_INPUT_TOPIC, key, value));

        // Consume and verify the output
        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "user-output-topic");
        assertEquals("Session expired and restarted", record.value());
    }
}