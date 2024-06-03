package space.zeinab.demo.customProcessorService.topology.slidingWindow;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;


public class IotStatusProcessor implements Processor<String, String, String, String> {
    private ProcessorContext<String, String> context;
    private KeyValueStore<String, Double> sensorStore;
    private static final int WINDOW_SIZE = 10;

    @Override
    public void init(ProcessorContext<String, String> context) {
        this.context = context;
        this.sensorStore = context.getStateStore("sensor-store");
    }

    @Override
    public void process(Record<String, String> record) {
        Double sum = sensorStore.get(record.key());
        if (sum == null) {
            sum = 0.0;
        }

        sum += Double.parseDouble(record.value());
        sensorStore.put(record.key(), sum);

        double average = sum / WINDOW_SIZE;

        if (Double.parseDouble(record.value()) > average * 1.5) {  // Anomaly detection condition
            context.forward(new Record<>(record.key(), "Anomaly detected: " + record.value(), record.timestamp()));
        }
    }

    @Override
    public void close() {
        // No-op
    }
}
