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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;
import space.zeinab.demo.customProcessorService.topology.KafkaTestContainerSetup;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserActivityInputPipelineIT {
    private KafkaStreams streams;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @BeforeEach
    public void setUp() {
        KafkaTestContainerSetup.startKafka();

        // Kafka producer configuration
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainerSetup.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);

        // Kafka consumer configuration
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainerSetup.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(KafkaConfig.USER_OUTPUT_TOPIC));

        // Kafka Streams configuration
        Properties streamsProps = KafkaConfig.setup("user-activity-app", "state/user-activity");
        streamsProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainerSetup.getBootstrapServers());

        Topology topology = UserActivityInputPipeline.buildSessionTopology();
        streams = new KafkaStreams(topology, streamsProps);
        streams.start();
    }

    @AfterEach
    public void tearDown() {
        if (streams != null) {
            streams.close();
        }
        if (producer != null) {
            producer.close();
        }
        if (consumer != null) {
            consumer.close();
        }
        KafkaTestContainerSetup.stopKafka();
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
        ConsumerRecord<String, String> record = consumer.poll(Duration.ofSeconds(10)).iterator().next();
        assertEquals("Session expired and restarted", record.value());
    }
}