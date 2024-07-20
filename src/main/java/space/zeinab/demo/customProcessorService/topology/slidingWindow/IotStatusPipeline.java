package space.zeinab.demo.customProcessorService.topology.slidingWindow;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import space.zeinab.demo.customProcessorService.config.KafkaConfig;

@Component
public class IotStatusPipeline {

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        streamsBuilder.build()
                .addSource("iot-source", KafkaConfig.IOT_INPUT_TOPIC)
                .addProcessor("iot-processor", IotStatusProcessor::new, "iot-source")
                .addStateStore(
                        Stores.keyValueStoreBuilder(
                                Stores.persistentKeyValueStore("iot-store"),
                                Serdes.String(),
                                Serdes.Double()
                        ), "iot-processor"
                )
                .addSink("iot-sink", KafkaConfig.IOT_OUTPUT_TOPIC, "iot-processor");

    }
}
