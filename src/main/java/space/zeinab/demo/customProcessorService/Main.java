package space.zeinab.demo.customProcessorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}