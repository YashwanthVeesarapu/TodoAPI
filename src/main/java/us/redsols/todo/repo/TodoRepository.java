package us.redsols.todo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.redsols.todo.model.Todo;


public interface TodoRepository extends MongoRepository<Todo, String> {


}
