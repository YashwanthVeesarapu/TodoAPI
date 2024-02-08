package us.redsols.todo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.redsols.todo.model.Content;

public interface ContentRepository extends MongoRepository<Content, String> {
    Content findByUrl(String url);
}
