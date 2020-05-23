package slackbot.controller;

import org.junit.Test;

import java.io.IOException;

public class SlackControllerTest {
    @Test
    public void name() throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(String.format("docker run -itd --rm --network host --name %s gcc", "test"));

    }
}