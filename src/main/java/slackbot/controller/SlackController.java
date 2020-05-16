package slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import slackbot.dto.EchoRequest;
import slackbot.dto.SlackPostBody;

@Controller
public class SlackController {

    @Value("${slack.api.key}")
    private String key;

    @Value("${slack.api.channelId}")
    private String channelId;

    //    @PostMapping("/")
//    public ResponseEntity verify(@RequestBody VerifyRequest request) {
//        return ResponseEntity.ok(request);
//    }
    @PostMapping("/")
    public ResponseEntity echo(@RequestBody EchoRequest request) throws JsonProcessingException {
        // bot인지 아닌지
        if (request.getEvent().getBotId() != null) {
            return ResponseEntity.ok().build();
        }
        // key값, 채널값 가져오기
        SlackPostBody slackPostBody = new SlackPostBody(request.getEvent().getText(), channelId);

        // 보내주기
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", key);

        HttpEntity<String> postRequest = new HttpEntity<>(slackPostBody.toJson(), httpHeaders);

        String url = "https://slack.com/api/chat.postMessage";

        restTemplate.postForEntity(url, postRequest, String.class);

        return ResponseEntity.ok().build();
    }

}
