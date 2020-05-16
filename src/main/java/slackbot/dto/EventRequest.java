package slackbot.dto;

public class EventRequest {
    private String botId;
    private String text;

    public EventRequest(String bot_id, String text) {
        this.botId = bot_id;
        this.text = text;
    }

    public String getBotId() {
        return botId;
    }

    public String getText() {
        return text;
    }
}
