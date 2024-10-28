package us.redsols.todo.controller;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import us.redsols.todo.model.Email;
import us.redsols.todo.model.Notification;
import us.redsols.todo.model.Template;
import us.redsols.todo.model.Todo;
import us.redsols.todo.model.User;
import us.redsols.todo.service.AuthService;
import us.redsols.todo.service.EmailService;
import us.redsols.todo.service.NotificationService;
import us.redsols.todo.service.TodoService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

@RestController
@RequestMapping("notification")
public class NotificationController {

    AuthService authService;
    TodoService todoService;
    EmailService emailService;
    NotificationService notificationService;
    RestTemplate restTemplate;

    public NotificationController(AuthService authService, TodoService todoService, EmailService emailService,
            NotificationService notificationService) {
        this.authService = authService;
        this.todoService = todoService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        restTemplate = new RestTemplate();
    }

    @Value("${ADMIN_TOKEN}")
    private String adminToken;

    @Value("${AMPLIFY_API_KEY}")
    private String apiKey;

    // Daily reminder
    @GetMapping
    public ResponseEntity<?> fetchAllTodos(
            @RequestHeader("Authorization") String token) {

        if (!token.isEmpty()) {
            if (token.equals(adminToken)) {
                // remove all notifications
                notificationService.removeAllNotifications();

                // notification data for todays tasks before 15 mins
                List<Notification> notifications = new ArrayList<>();

                Iterable<User> users = authService.getAllUsers();

                for (User user : users) {
                    // Send Email Notification to user if task is due today
                    List<Todo> todos = todoService.getAllTodos(user.getId());
                    List<Todo> todayTodos = new ArrayList<>();
                    List<Todo> pastTodos = new ArrayList<>();

                    for (Todo todo : todos) {
                        if (!todo.isCompleted() && (todo.getRemind().equals("true"))) {

                            if (todo.getDate().equals(java.time.LocalDate.now().toString())) {

                                todayTodos.add(todo);
                                // log in console

                                if (todo.getTime() != null) {
                                    // Send Notification 15 mins before
                                    Notification notification = new Notification(todo.getTitle(), todo.getDate(),
                                            todo.getTime(), user.getEmail(), user.getTimezone(), todo.getId());
                                    notifications.add(notification);
                                }

                            } else if (todo.getDate().compareTo(java.time.LocalDate.now().toString()) < 0)
                                pastTodos.add(todo);
                        }
                    }
                    if (todayTodos.size() > 0 || pastTodos.size() > 0) {
                        String html = "";

                        try {
                            // get template from database
                            String amplifyURL = "https://api.redsols.us/amplify/api/templates/Taskify";
                            System.out.println(apiKey);

                            // set headers
                            HttpHeaders headers = new HttpHeaders();
                            headers.set("Authorization", "Bearer " + apiKey);

                            HttpEntity<String> entity = new HttpEntity<>(headers);

                            // get request to amplifyURL
                            ResponseEntity<Template> response = restTemplate.exchange(amplifyURL, HttpMethod.GET,
                                    entity, Template.class);
                            html = response.getBody().getHtml();

                        } catch (Exception e) {
                            // TODO: handle exception
                            System.out.println(e);
                        }

                        if (html != "") {
                            Document document = Jsoup.parse(html);
                            // Todays Tasks with id today-list
                            // todays div

                            if (todayTodos.size() > 0) {
                                Element todayDiv = document.getElementById("today-list");
                                // existing tasks
                                Element pElement = todayDiv.selectFirst("p");

                                // delete existing tasks
                                todayDiv.select("p").remove();

                                for (Todo todo : todayTodos) {
                                    Element newElement = pElement.clone();
                                    newElement.text(todo.getTitle());
                                    todayDiv.appendChild(newElement);
                                }
                            }

                            // Pending Tasks with id pending-list
                            // pending div
                            if (pastTodos.size() > 0) {
                                Element pendingDiv = document.getElementById("due-list");
                                // existing tasks
                                Element pElement = pendingDiv.selectFirst("p");
                                // delete existing tasks
                                pendingDiv.select("p").remove();
                                for (Todo todo : pastTodos) {
                                    Element newElement = pElement.clone();
                                    newElement.text(todo.getTitle());
                                    pendingDiv.appendChild(newElement);
                                }
                            }

                            html = document.html();

                        } else {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error in fetching template");
                        }

                        emailService.sendEmail(new Email(user.getEmail(), "Taskify | Today's Tasks", html));

                    }

                }

                if (notifications.size() > 0)
                    notificationService.saveNotifications(notifications);

                return ResponseEntity.status(HttpStatus.OK)
                        .body("Email Notifications are sent to all users");
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Access");
        }
    }

    // reminder before 15 mins

    @Scheduled(cron = "0 0/1 * * * ?")
    @GetMapping("/reminder")
    public ResponseEntity<?> sendReminder() {
        List<Notification> notifications = notificationService.getAllNotifications();

        for (Notification notification : notifications) {

            // time format 00:00

            // according to timezone

            String timezone = notification.getTimezone();
            ZoneId zoneId = ZoneId.of(timezone);

            if (notification.getTime().equals(
                    java.time.LocalTime.now(zoneId).plusMinutes(15).toString().substring(0, 5))

            ) {

                String html = "";

                try {
                    // get template from database
                    String amplifyURL = "https://api.redsols.us/amplify/api/templates/Taskify";
                    System.out.println(apiKey);

                    // set headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + apiKey);

                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    // get request to amplifyURL
                    ResponseEntity<Template> response = restTemplate.exchange(amplifyURL, HttpMethod.GET, entity,
                            Template.class);
                    html = response.getBody().getHtml();

                } catch (Exception e) {

                    System.out.println(e);
                }

                if (html != "") {
                    Document document = Jsoup.parse(html);
                    // Todays Tasks with id today-list
                    // todays div

                    Element todayDiv = document.getElementById("today-list");
                    Element dueDiv = document.getElementById("due-list");

                    // remove due div
                    dueDiv.remove();
                    // h3 inside today div
                    Element mainDiv = document.getElementById("today");
                    Element h3Element = mainDiv.selectFirst("h3");
                    h3Element.text("Reminder");
                    // existing tasks
                    Element pElement = todayDiv.selectFirst("p");

                    // delete existing tasks
                    todayDiv.select("p").remove();
                    Element newElement = pElement.clone();
                    newElement.text(notification.getTitle());
                    todayDiv.appendChild(newElement);

                    html = document.html();

                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in fetching template");
                }

                emailService.sendEmail(new Email(notification.getEmail(), "Reminder", html));
                // remove notification
                notificationService.removeNotification(notification.getId());
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Send Scheduled Emails");
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEmail() {
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

        emailService.sendEmail(new Email("v.yashwanthreddy2@gmail.com", "Today's Tasks", html));
        return ResponseEntity.status(HttpStatus.OK)
                .body("Email sent");

    }

}
