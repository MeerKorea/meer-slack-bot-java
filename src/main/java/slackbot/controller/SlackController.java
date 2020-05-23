package slackbot.controller;

import org.apache.commons.text.StringEscapeUtils;
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
import slackbot.dto.VerifyRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


@Controller
public class SlackController {

    @Value("${slack.api.key}")
    private String key;

    @Value("${slack.api.channelId}")
    private String channelId;

    //    @PostMapping("/")
    public ResponseEntity verify(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(request);
    }

    @PostMapping("/")
    public ResponseEntity echo(@RequestBody EchoRequest request) throws IOException {
        // bot인지 아닌지
        if (request.getEvent().getBotId() != null) {
            return ResponseEntity.ok().build();
        }

        String messageText = request.getEvent().getText();

        String[] parsedText = messageText.split("```");

        String[] command = parsedText[0].split(" ");

        if ("run".equals(command[0])) {
            String language = command[1];

            String content = StringEscapeUtils.unescapeHtml4(parsedText[1]);
            OutputStream output = new FileOutputStream("src/main/resources/codes/saved.txt");
            output.write(content.getBytes());

            String uuid = UUID.randomUUID().toString();

            // key값, 채널값 가져오기
            SlackPostBody slackPostBody = new SlackPostBody(content, channelId);

            // 보내주기
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", key);

            HttpEntity<String> postRequest = new HttpEntity<>(slackPostBody.toJson(), httpHeaders);

            String url = "https://slack.com/api/chat.postMessage";

            restTemplate.postForEntity(url, postRequest, String.class);
        }

        return ResponseEntity.ok().build();
    }
}
