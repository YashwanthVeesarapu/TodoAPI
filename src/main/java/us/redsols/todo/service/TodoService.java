package us.redsols.todo.service;

import org.springframework.stereotype.Service;
import us.redsols.todo.repo.TodoRepository;
import us.redsols.todo.model.Todo;

import java.util.List;
@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos(){
        return  todoRepository.findAll();
    }

    public Todo createTodo(Todo todo){
        return  todoRepository.insert(todo);
    }

    public  Todo editTodo(Todo todo){
        return  todoRepository.save(todo);
    }
}
