package se.backend.groupred2.resource;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.inject.Singleton;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Singleton
@Path("tasks/events")
public final class TaskEventResource {

    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents() {
        final EventOutput eventOutput = new EventOutput();
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
                    eventBuilder.name("message-to-client");
                    eventBuilder.data(String.class, "Hello world " + i + "!");
                    final OutboundEvent event = eventBuilder.build();
                    eventOutput.write(event);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error when writing the event.", e);
            } finally {
                try {
                    eventOutput.close();
                } catch (IOException ioClose) {
                    throw new RuntimeException("Error when closing the event output.", ioClose);
                }
            }
        }).start();
        return eventOutput;
    }
}
