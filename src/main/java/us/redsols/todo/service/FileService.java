package us.redsols.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public FileService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String storeFile(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        return gridFsTemplate.store(inputStream, fileName, contentType).toString();
    }

    public InputStream getFile(String fileId) throws IOException {
        GridFsResource resource = gridFsTemplate.getResource(fileId);
        return resource.getInputStream();
    }

    public void deleteFile(String filename) throws IOException {
        Query query = new Query(Criteria.where("filename").is(filename));
        gridFsTemplate.delete(query);
    }
}
