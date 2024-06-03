package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

@Component
@AllArgsConstructor
public class UserActivityOutputPipeline {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder
                .stream(KafkaConfig.USER_OUTPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> System.out.println("User activity processor worked well: " + value));
    }
}
