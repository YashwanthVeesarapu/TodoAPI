package us.redsols.todo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import us.redsols.todo.model.Notification;

import java.util.List;

public interface NotificationsRepository extends MongoRepository<Notification, String> {
    List<Notification> findByEmail(String email);

    Notification findByTodoId(String todoId);
}
