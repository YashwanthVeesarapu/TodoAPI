package us.redsols.todo.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    public void sendEmail(String to, String html, String subject) {
        // Call other micro service to send email
        String apiURL = "https://api.redash.us/email/send";
//        String apiURL = "http://localhost:4000/email/send";

        String from = "REDSOLS <hello@redsols.us>";

        String requestJson = "{"
                + "\"to\":\"" + to + "\","
                + "\"from\":\"" + from + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"html\":\"" + html + "\""
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.postForEntity(apiURL, entity, String.class);

    }

}
