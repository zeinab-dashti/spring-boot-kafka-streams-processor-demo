package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

@Component
public class UserActivityPipeline {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder.build()
                .addSource("session-source", KafkaConfig.USER_INPUT_TOPIC)
                .addProcessor("session-processor", UserActivityProcessor::new, "session-source")
                .addStateStore(
                        Stores.keyValueStoreBuilder(
                                Stores.persistentKeyValueStore("session-store"),
                                Serdes.String(),
                                Serdes.Long()
                        ), "session-processor"
                )
                .addSink("session-sink", KafkaConfig.USER_OUTPUT_TOPIC, "session-processor");
    }
}
