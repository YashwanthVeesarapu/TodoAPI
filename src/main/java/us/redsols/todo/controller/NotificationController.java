package us.redsols.todo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.redsols.todo.model.Todo;
import us.redsols.todo.model.User;
import us.redsols.todo.service.AuthService;
import us.redsols.todo.service.EmailService;
import us.redsols.todo.service.TodoService;

@RestController
@RequestMapping("notification")
public class NotificationController {

    AuthService authService;
    TodoService todoService;
    EmailService emailService;

    public NotificationController(AuthService authService, TodoService todoService, EmailService emailService) {
        this.authService = authService;
        this.todoService = todoService;
        this.emailService = emailService;
    }

    @Value("${ADMIN_TOKEN}")
    private String adminToken;

    @GetMapping
    public ResponseEntity<?> fetchAllTodos(
            @RequestHeader("Authorization") String token) {

        if (!token.isEmpty()) {
            if (token.equals(adminToken)) {
                // Send Email Notifications to all users

                Iterable<User> users = authService.getAllUsers();

                for (User user : users) {
                    // Send Email Notification to user if task is due today
                    List<Todo> todos = todoService.getAllTodos(user.getId());
                    List<Todo> todayTodos = new ArrayList<>();
                    List<Todo> pastTodos = new ArrayList<>();

                    for (Todo todo : todos) {
                        if (!todo.isCompleted() && (todo.getRemind().equals("true"))) {

                            if (todo.getDate().equals(java.time.LocalDate.now().toString()))
                                todayTodos.add(todo);
                            else if (todo.getDate().compareTo(java.time.LocalDate.now().toString()) < 0)
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
                        // Send Email
                        emailService.sendEmail(user.getEmail(), html, "Today's Tasks");
                    }

                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Email Notifications are sent to all users");
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Access");
        }

    }

}
