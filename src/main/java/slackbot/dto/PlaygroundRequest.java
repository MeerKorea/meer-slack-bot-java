package slackbot.dto;

public class PlaygroundRequest {
    private String result;

    public PlaygroundRequest() {
    }

    public PlaygroundRequest(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
