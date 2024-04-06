package us.redsols.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    private final AuthService authService;

    private final JwtTokenProvider jwtTokenProvider;

    public TodoController(TodoService todoService, JwtTokenProvider jwtTokenProvider,
            NotificationService notificationService, AuthService authService) {
        this.todoService = todoService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> fetchAllTodos(@RequestParam("uid") String uid,
            @RequestHeader("Authorization") String token) {
        if (!token.isEmpty()) {

            // validate token
            Boolean isValid = jwtTokenProvider.validateToken(token);

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            String extractedUid = jwtTokenProvider.extractUid(token);
            if (extractedUid.equals(uid)) {
                return ResponseEntity.status(HttpStatus.OK).body(todoService.getAllTodos(uid));
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Access");
        }

    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody Todo rtodo) {
        String username = jwtTokenProvider.getUsernameFromToken(rtodo.getToken());
        String uid = jwtTokenProvider.extractUid(rtodo.getToken());
        if (rtodo.getUsername().equals(username)) {

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(todoService.createTodo(rtodo));
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Something is wrong");
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
    public ResponseEntity<?> deleteTodo(@PathVariable String id, @RequestHeader("Authorization") String token,
            @RequestBody Todo todo) {
        String uid = jwtTokenProvider.extractUid(token);
        if (uid.equals(todo.getUid())) {
            todoService.deleteTodo(todo);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Success"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Todo not found or unauthorized");
        }
    }
}
