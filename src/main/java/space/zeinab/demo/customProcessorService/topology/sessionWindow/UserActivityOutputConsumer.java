package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

@Slf4j
@Component
@Profile("session")
public class UserActivityOutputConsumer {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder
                .stream(KafkaConfig.USER_OUTPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> log.info("User activity processor worked well: " + value));
    }
}
