package us.redsols.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.Todo;
import us.redsols.todo.service.TodoService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?>  fetchAllTodos( @RequestParam("uid") String uid,  @RequestHeader("Authorization") String token) {
        if(!token.isEmpty()){
            String extractedUid = jwtTokenProvider.extractUid(token);
            if(extractedUid.equals(uid)) {
                return ResponseEntity.status(HttpStatus.OK).body(todoService.getAllTodos(uid));
            }
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Access");
        }

    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody Todo rtodo){
        String username = jwtTokenProvider.getUsernameFromToken(rtodo.getToken());
        String uid = jwtTokenProvider.extractUid(rtodo.getToken());
        if(rtodo.getUsername().equals(username)) {
            Todo todo = new Todo(rtodo.getTitle(), rtodo.getDate(), rtodo.isCompleted(), rtodo.getRepeat(), rtodo.isRemind(), rtodo.isImportant(), uid);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(todoService.createTodo(todo));
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Something is wrong");
    }

    @PutMapping()
    public Todo editTodo(@RequestBody Todo todo ){
        return todoService.editTodo(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable String id, @RequestHeader("Authorization") String token, @RequestBody Todo todo) {
        String uid = jwtTokenProvider.extractUid(token);
        System.out.println(id);
        if (uid.equals(todo.getUid())) {
           todoService.deleteTodo(todo);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Success"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Todo not found or unauthorized");
        }
    }
}
