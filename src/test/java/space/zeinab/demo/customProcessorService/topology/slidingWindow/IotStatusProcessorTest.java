package space.zeinab.demo.customProcessorService.topology.slidingWindow;

import org.apache.kafka.streams.processor.api.MockProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.internals.InMemoryKeyValueStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import space.zeinab.demo.customProcessorService.topology.sessionWindow.UserActivityProcessor;

import static org.mockito.Mockito.*;


class IotStatusProcessorTest {
    private ProcessorContext<String, String> context;
    private KeyValueStore<String, Double> store;
    private IotStatusProcessor processor;

    @BeforeEach
    void setUp() {
        context = Mockito.mock(ProcessorContext.class);
        store = Mockito.mock(KeyValueStore.class);
        processor = new IotStatusProcessor();

        when(context.getStateStore("sensor-store")).thenReturn(store);

        processor.init(context);
    }

    @Test
    void testProcessAnomalyDetected() {
        String key = "sensor1";
        String value = "25.0";
        Record<String, String> record = new Record<>(key, value, System.currentTimeMillis());

        when(store.get(key)).thenReturn(100.0);

        processor.process(record);

        verify(context).forward(new Record<>(key, "Anomaly detected: " + value, record.timestamp()));
    }

    @Test
    void testProcessNoAnomalyDetected() {
        String key = "sensor1";
        String value = "5.0";
        Record<String, String> record = new Record<>(key, value, System.currentTimeMillis());

        when(store.get(key)).thenReturn(100.0);

        processor.process(record);

        verify(context, never()).forward(any());
    }
}