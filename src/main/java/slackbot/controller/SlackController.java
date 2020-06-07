package slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

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
import slackbot.dto.PlaygroundRequest;
import slackbot.dto.SlackPostBody;
import slackbot.dto.VerifyRequest;

import java.io.*;
import java.nio.Buffer;
import java.util.UUID;

@Controller
public class SlackController {

    @Value("${slack.api.key}")
    private String key;

    @Value("${slack.api.channelId}")
    private String channelId;

    // @PostMapping("/")
    public ResponseEntity verify(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(request);
    }

    @PostMapping("/")
    public ResponseEntity echo(@RequestBody EchoRequest request) throws IOException, InterruptedException {
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

            String uuid = UUID.randomUUID().toString();
            String sourceCodePath = String.format("src/main/resources/codes/%s.c", uuid);
            OutputStream output = new FileOutputStream(sourceCodePath);
            output.write(content.getBytes());

            // 플레이그라운드 컨테이너 run
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(String.format("docker run -itd --rm --network host --name %s gcc", uuid));
            BufferedReader containerInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String s = null;
            while ((s = containerInput.readLine()) != null) {
                System.out.println(s);
            }

            // 컨테이너에 생성된 c 전달하기
            pr = rt.exec(String.format("docker cp %s %s:/", sourceCodePath, uuid));
            containerInput = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            while ((s = containerInput.readLine()) != null) {
                System.out.println(s);
            }
            // 컨테이너에 존재하는 compile.sh 실행
            pr = rt.exec(String.format("docker exec %s /compile.sh %s.c", uuid, uuid));
            containerInput = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            while ((s = containerInput.readLine()) != null) {
                System.out.println(s);
            }

            // key값, 채널값 가져오기
            SlackPostBody slackPostBody = new SlackPostBody(content, channelId);

            //            // 보내주기
            //            RestTemplate restTemplate = new RestTemplate();
            //
            //            HttpHeaders httpHeaders = new HttpHeaders();
            //            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            //            httpHeaders.set("Authorization", key);
            //
            //            HttpEntity<String> postRequest = new HttpEntity<>(slackPostBody.toJson(), httpHeaders);
            //
            //            String url = "https://slack.com/api/chat.postMessage";
            //
            //            restTemplate.postForEntity(url, postRequest, String.class);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/docker/playground")
    public void sendToSlackAPIServer(@RequestBody PlaygroundRequest outputData) throws JsonProcessingException {
        SlackPostBody slackPostBody = new SlackPostBody(outputData.getResult(), channelId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", String.format("Bearer %s", key));

        HttpEntity<String> postRequest = new HttpEntity<>(slackPostBody.toJson(), httpHeaders);

        String url = "https://slack.com/api/chat.postMessage";

        restTemplate.postForEntity(url, postRequest, String.class);
    }
}
