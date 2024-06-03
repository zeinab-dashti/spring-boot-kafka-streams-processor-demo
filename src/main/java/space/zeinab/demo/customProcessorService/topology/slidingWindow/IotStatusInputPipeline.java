package space.zeinab.demo.customProcessorService.topology.slidingWindow;

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
public class IotStatusInputPipeline {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder.addStateStore(Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("sensor-store"),
                Serdes.String(),
                Serdes.Double()
        ));

        Topology topology = buildIotTopology();
        KafkaStreams streams = new KafkaStreams(topology, KafkaConfig.setup("iot-status-app", "state/iot-status"));
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }


    public static Topology buildIotTopology() {
        Topology topology = new Topology();
        topology.addSource("Source", KafkaConfig.IOT_INPUT_TOPIC)
                .addProcessor("Processor", IotStatusProcessor::new, "Source")
                .addStateStore(
                        Stores.keyValueStoreBuilder(
                                Stores.persistentKeyValueStore("sensor-store"),
                                Serdes.String(),
                                Serdes.Double()
                        ), "Processor"
                )
                .addSink("Sink", KafkaConfig.IOT_OUTPUT_TOPIC, "Processor");
        return topology;
    }
}
