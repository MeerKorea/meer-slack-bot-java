package slackbot.dto;

public class EchoRequest {
    private EventRequest event;

    // TODO: 왜 이걸 써야할까
    public EchoRequest() {
    }

    public EchoRequest(EventRequest event) {
        this.event = event;
    }

    public EventRequest getEvent() {
        return event;
    }
}
