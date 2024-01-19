package us.redsols.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.RequestTodo;
import us.redsols.todo.model.Todo;
import us.redsols.todo.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("todos")
public class TodoController {

    private final TodoService todoService;

    private final JwtTokenProvider jwtTokenProvider;

    public TodoController(TodoService todoService,JwtTokenProvider jwtTokenProvider) {
        this.todoService = todoService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping
    public List<Todo> fetchAllTodos(@RequestParam("token") String token) {
        String uid = jwtTokenProvider.extractUid(token);
        return todoService.getAllTodos(uid);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody RequestTodo rtodo){
        String username = jwtTokenProvider.getUsernameFromToken(rtodo.getToken());
        String uid = jwtTokenProvider.extractUid(rtodo.getToken());
        if(rtodo.getUsername().equals(username)) {
            Todo todo = new Todo(rtodo.getTitle(), rtodo.getDate(), rtodo.isRepeat(), rtodo.isRemind(), uid);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(todoService.createTodo(todo));
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Something is wrong");
    }

    @PutMapping()
    public Todo editTodo(@RequestBody Todo todo ){
        return todoService.editTodo(todo);
    }
}
