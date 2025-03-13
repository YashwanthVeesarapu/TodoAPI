package us.redsols.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import us.redsols.todo.model.Email;
import us.redsols.todo.service.EmailService;

@RestController
@RequestMapping("test")
public class TestController {
    EmailService emailService;
    RestTemplate restTemplate;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
        restTemplate = new RestTemplate();
    }

    public void sendTestEmail() {

        String html = "<!DOCTYPE html>";
        html += "<html>";
        html += "<head>";
        html += "<style>";
        html += "body {font-family: Arial, sans-serif;}";
        html += "p {color: #008000;}";
        html += ".past-task {color: #FF0000;}";
        html += ".task-list {margin-bottom: 20px;}";
        html += "</style>";
        html += "</head>";
        html += "<body>";
        html += "<div>";
        html += "<h1>To Do by Redsols</h1>";
        html += "<div class='task-list'>";
        html += "<h2>Today's Tasks</h2>";
        html += "<p>Task 1</p>";
        html += "<p>Task 2</p>";
        html += "</div>";
        html += "<div class='task-list'>";
        html += "<h2>Pending Tasks</h2>";
        html += "<p class='past-task'>Task 3</p>";
        html += "<p class='past-task'>Task 4</p>";
        html += "</div>";
        html += "</div>";
        html += "</body>";
        html += "</html>";
        // Send Email

        Email email = new Email("v.yashwanthreddy2@gmail.com", "Today's Tasks", html);

        emailService.sendEmail(email);

    }

    @GetMapping("email")
    public ResponseEntity<?> dailyEmailService() {
        sendTestEmail();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("sent email");

    }

}