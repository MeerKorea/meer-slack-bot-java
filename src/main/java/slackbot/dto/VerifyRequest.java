package slackbot.dto;

public class VerifyRequest {
    // data transfer object
    private String token;
    private String challenge;
    private String type;

    public VerifyRequest(String token, String challenge, String type) {
        this.token = token;
        this.challenge = challenge;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public String getChallenge() {
        return challenge;
    }

    public String getType() {
        return type;
    }
}
