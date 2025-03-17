package us.redsols.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.Notification;
import us.redsols.todo.model.Todo;
import us.redsols.todo.model.User;
import us.redsols.todo.service.AuthService;
import us.redsols.todo.service.NotificationService;
import us.redsols.todo.service.TodoService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("todos")
public class TodoController {

    private final TodoService todoService;
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthService authService;

    public TodoController(TodoService todoService, JwtTokenProvider jwtTokenProvider,
            NotificationService notificationService, AuthService authService) {
        this.todoService = todoService;
        this.notificationService = notificationService;
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public ResponseEntity<?> fetchAllTodos(HttpServletRequest req) {

        // validate token
        // Boolean isValid = jwtTokenProvider.validateToken(token);

        // access token from http cookie

        Cookie[] cookies = req.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // extract uid from token
        String uid = jwtTokenProvider.extractUid(token);

        // String userId = (String) req.getAttribute("uid");
        // System.out.println("userId: " + userId);

        // // cookie "access_token"

        return ResponseEntity.status(HttpStatus.OK).body(todoService.getAllTodos(uid));

    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody Todo rtodo, HttpServletRequest req) {
        String uid = req.getAttribute("uid").toString();
        rtodo.setUid(uid);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(todoService.createTodo(rtodo));
    }

    @PutMapping()
    public Todo editTodo(@RequestBody Todo todo) {
        // today
        if (todo.getRemind().equals("true") && todo.getTime() != null
                && todo.getDate().equals(java.time.LocalDate.now().toString())) {
            // check if notification already exists
            // if not add notification
            Optional<User> user = authService.getUserById(todo.getUid());

            if (user != null) {
                Notification notification = notificationService.getNotificationByTodoId(todo.getId());
                if (notification == null) {
                    notificationService.addNotification(new Notification(
                            todo.getTitle(),
                            todo.getDate(),
                            todo.getTime(),
                            user.get().getEmail(),
                            user.get().getTimezone(),
                            todo.getId()));
                } else {
                    // update notification
                    notification.setTitle(todo.getTitle());
                    notification.setDate(todo.getDate());
                    notification.setTime(todo.getTime());
                    notification.setEmail(user.get().getEmail());
                    notification.setTimezone(user.get().getTimezone());
                    notification.setTodoId(todo.getId());
                    notificationService.editNotification(notification);

                }
            }

        }
        ;

        return todoService.editTodo(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable String id, HttpServletRequest req,
            @RequestBody Todo todo) {
        String uid = req.getAttribute("uid").toString();
        if (uid.equals(todo.getUid())) {
            todoService.deleteTodo(todo);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Success"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Todo not found or unauthorized");
        }
    }
}
