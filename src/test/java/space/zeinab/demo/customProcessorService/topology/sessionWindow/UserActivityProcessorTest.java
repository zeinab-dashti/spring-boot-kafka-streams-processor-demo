package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

class UserActivityProcessorTest {
    private ProcessorContext<String, String> context;
    private KeyValueStore<String, Long> store;
    private UserActivityProcessor processor;

    @BeforeEach
    public void setUp() {
        context = Mockito.mock(ProcessorContext.class);
        store = Mockito.mock(KeyValueStore.class);
        processor = new UserActivityProcessor();

        when(context.getStateStore("session-store")).thenReturn(store);

        processor.init(context);
    }

    @Test
    public void testProcessSessionExpired() {
        String key = "user1";
        String value = "activity";
        long currentTime = 1000000L;
        long lastAccessTime = currentTime - 30000L;

        when(context.currentSystemTimeMs()).thenReturn(currentTime);
        when(store.get(key)).thenReturn(lastAccessTime);

        Record<String, String> record = new Record<>(key, value, currentTime);
        processor.process(record);

        verify(store).put(key, currentTime);
        verify(context).forward(new Record<>(key, "Session expired and restarted", currentTime));
    }

    @Test
    public void testProcessSessionNotExpired() {
        String key = "user2";
        String value = "activity";
        long currentTime = 1000000L;
        long lastAccessTime = currentTime - 10000L;

        when(context.currentSystemTimeMs()).thenReturn(currentTime);
        when(store.get(key)).thenReturn(lastAccessTime);

        Record<String, String> record = new Record<>(key, value, currentTime);
        processor.process(record);

        verify(store).put(key, currentTime);
        verify(context, never()).forward(any());
    }

    @Test
    public void testProcessNewSession() {
        String key = "user3";
        String value = "activity";
        long currentTime = 1000000L;

        when(context.currentSystemTimeMs()).thenReturn(currentTime);
        when(store.get(key)).thenReturn(null);

        Record<String, String> record = new Record<>(key, value, currentTime);
        processor.process(record);

        verify(store).put(key, currentTime);
        verify(context, never()).forward(any());
    }
}