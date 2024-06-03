package space.zeinab.demo.customProcessorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaStreamsProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsProcessorApplication.class, args);
    }
}