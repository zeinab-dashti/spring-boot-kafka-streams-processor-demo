package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

@Component
@AllArgsConstructor
public class UserActivityInputPipeline {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder.addStateStore(Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("session-store"),
                Serdes.String(),
                Serdes.String()
        ));

        Topology topology = buildSessionTopology();

        KafkaStreams streams = new KafkaStreams(topology, KafkaConfig.setup("user-activity-app", "state/user-activity"));
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static Topology buildSessionTopology() {
        Topology topology = new Topology();
        topology.addSource("Source", KafkaConfig.USER_INPUT_TOPIC)
                .addProcessor("Processor", UserActivityProcessor::new, "Source")
                .addStateStore(
                        Stores.keyValueStoreBuilder(
                                Stores.persistentKeyValueStore("session-store"),
                                Serdes.String(),
                                Serdes.Long()
                        ), "Processor"
                )
                .addSink("Sink", KafkaConfig.USER_OUTPUT_TOPIC, "Processor");
        return topology;
    }
}
