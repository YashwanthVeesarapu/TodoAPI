package us.redsols.todo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.redsols.todo.model.User;

import java.util.Optional;

public interface AuthRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

}
