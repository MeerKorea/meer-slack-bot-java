package slackbot.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SlackPostBody {
    private String text;
    private String channel;

    public SlackPostBody(String text, String channel) {
        this.text = text;
        this.channel = channel;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }


    public String getText() {
        return text;
    }

    public String getChannel() {
        return channel;
    }
}
