package space.zeinab.demo.customProcessorService.topology.slidingWindow;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;

public class IotStatusProcessor implements Processor<String, String, String, String> {
    private KeyValueStore<String, Double> sensorStore;
    private static final int WINDOW_SIZE = 10;

    @Override
    public void init(final ProcessorContext<String, String> context) {
        Punctuator punctuator = timestamp -> {
            try (final KeyValueIterator<String, Double> iter = sensorStore.all()) {
                while (iter.hasNext()) {
                    final KeyValue<String, Double> entry = iter.next();
                    Double value = entry.value;
                    double average = value / WINDOW_SIZE;

                    if (value > average * 1.5) {  // Anomaly detection condition
                        context.forward(new Record<>(
                                entry.key,
                                String.format("Anomaly detected - key=%s , value=%s", entry.key, value),
                                timestamp)
                        );
                    }
                }
            }
        };
        context.schedule(Duration.ofSeconds(20), PunctuationType.WALL_CLOCK_TIME, punctuator);
        this.sensorStore = context.getStateStore("iot-store");
    }

    @Override
    public void process(Record<String, String> record) {
        Double sum = sensorStore.get(record.key());
        if (sum == null) {
            sum = 0.0;
        }

        sum += Double.parseDouble(record.value());
        sensorStore.put(record.key(), sum);
    }

    @Override
    public void close() {
        // No-op
    }
}
