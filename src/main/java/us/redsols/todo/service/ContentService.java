package us.redsols.todo.service;

import org.springframework.stereotype.Service;
import us.redsols.todo.model.Content;
import us.redsols.todo.repo.ContentRepository;
@Service
public class ContentService {
    private ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository){
        this.contentRepository = contentRepository;
    }
    public Content getContent(String url){
        return contentRepository.findByUrl(url);
    }

    public Content createContent(Content content){
        return contentRepository.insert(content);
    }

    public void deleteContent(Content content){
        contentRepository.delete(content);
    }

    public Content editContent(Content content){
        return contentRepository.save(content);
    }

}
