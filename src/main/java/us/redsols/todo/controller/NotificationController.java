package us.redsols.todo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.redsols.todo.model.Email;
import us.redsols.todo.model.Notification;
import us.redsols.todo.model.Todo;
import us.redsols.todo.model.User;
import us.redsols.todo.service.AuthService;
import us.redsols.todo.service.EmailService;
import us.redsols.todo.service.NotificationService;
import us.redsols.todo.service.TodoService;

@RestController
@RequestMapping("notification")
public class NotificationController {

    AuthService authService;
    TodoService todoService;
    EmailService emailService;
    NotificationService notificationService;

    public NotificationController(AuthService authService, TodoService todoService, EmailService emailService,
            NotificationService notificationService) {
        this.authService = authService;
        this.todoService = todoService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Value("${ADMIN_TOKEN}")
    private String adminToken;

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

                                System.out.println(todo.getTime());

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

                        // Today's Tasks
                        if (todayTodos.size() > 0) {
                            html += "<div class='task-list'>";
                            html += "<h2>Today's Tasks</h2>";
                            for (Todo todo : todayTodos) {
                                html += "<p>" + todo.getTitle() + "</p>";
                            }
                            html += "</div>";
                        }

                        // Pending Tasks
                        if (pastTodos.size() > 0) {
                            html += "<div class='task-list'>";
                            html += "<h2>Pending Tasks</h2>";
                            for (Todo todo : pastTodos) {
                                html += "<p class='past-task'>" + todo.getTitle() + "</p>";
                            }
                            html += "</div>";
                        }

                        html += "</div>";
                        html += "</body>";
                        html += "</html>";

                        // emailService.sendEmail(new Email(user.getEmail(), "Today's Tasks", html));
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
        System.out.println("run");
        List<Notification> notifications = notificationService.getAllNotifications();

        for (Notification notification : notifications) {

            // time format 00:00

            if (notification.getTime().equals(
                    java.time.LocalTime.now().plusMinutes(15).toString().substring(0, 5))

            ) {
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
                html += "<h2>Reminder</h2>";
                html += "<p>" + notification.getTitle() + "</p>";
                html += "<p>Time: " + notification.getTime() + "</p>";
                html += "</div>";
                html += "</div>";
                html += "</body>";
                html += "</html>";

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
