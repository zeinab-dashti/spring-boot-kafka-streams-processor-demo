package space.zeinab.demo.customProcessorService.topology.sessionWindow;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

public class UserActivityProcessor implements Processor<String, String, String, String> {
    private ProcessorContext<String, String> context;
    private KeyValueStore<String, Long> sessionStore;

    @Override
    public void init(ProcessorContext<String, String> context) {
        this.context = context;
        this.sessionStore = context.getStateStore("session-store");
    }

    @Override
    public void process(Record<String, String> record) {
        long currentTime = context.currentSystemTimeMs();
        Long lastAccessTime = sessionStore.get(record.key());

        sessionStore.put(record.key(), currentTime);
        if (lastAccessTime != null) {
            if (currentTime - lastAccessTime > 60000) { // 60000 Custom expiration logic (e.g., 1 minute)
                context.forward(new Record<>(record.key(), "Session expired and restarted", record.timestamp()));
            }
        }
    }

    @Override
    public void close() {
        // No-op
    }
}
