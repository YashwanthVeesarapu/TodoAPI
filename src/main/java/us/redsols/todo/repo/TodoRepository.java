package us.redsols.todo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.redsols.todo.model.Todo;

import java.util.List;


public interface TodoRepository extends MongoRepository<Todo, String> {

    List<Todo> findByUid(String uid);

}
