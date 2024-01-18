package us.redsols.todo.controller;

import org.springframework.web.bind.annotation.*;
import us.redsols.todo.model.Todo;
import us.redsols.todo.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> fetchAllTodos() {
        return todoService.getAllTodos();
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo){
        return todoService.createTodo(todo);
    }

    @PutMapping()
    public Todo editTodo(@RequestBody Todo todo ){
        return todoService.editTodo(todo);
    }
}
