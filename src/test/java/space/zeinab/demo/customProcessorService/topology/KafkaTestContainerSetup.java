package space.zeinab.demo.customProcessorService.topology;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestContainerSetup {
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @BeforeAll
    public static void startKafka() {
        kafkaContainer.start();
    }

    @AfterAll
    public static void stopKafka() {
        kafkaContainer.stop();
    }

    public static String getBootstrapServers() {
        return kafkaContainer.getBootstrapServers();
    }
}