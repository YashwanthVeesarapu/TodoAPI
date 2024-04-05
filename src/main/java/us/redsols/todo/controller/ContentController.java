package us.redsols.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.redsols.todo.model.Content;
import us.redsols.todo.model.User;
import us.redsols.todo.service.ContentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public ResponseEntity<?> fetchContent(@RequestParam("url") String url) {
        Content content = contentService.getContent(url);
        if (content == null) {
            return ResponseEntity.status(404).body("Not found");
        }
        return ResponseEntity.status(200).body(contentService.getContent(url));
    }

    @PostMapping
    public ResponseEntity<?> postContent(@RequestBody Content content) {
        try {
            Content con = contentService.createContent(content);
            return ResponseEntity.status(200).body(con);

        } catch (Exception e) {
            return ResponseEntity.status(404).body("Not found");
        }
    }

    @DeleteMapping("/{url}")
    public ResponseEntity<?> removeContent(@PathVariable String url) {
        try {
            Content content = contentService.getContent(url);
            contentService.deleteContent(content);
            return ResponseEntity.status(200).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error");

        }
    }

    @PutMapping
    public ResponseEntity<?> putContent(@RequestBody Content content) {
        try {
            contentService.editContent(content);
            return ResponseEntity.status(200).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> contentLogin(@RequestBody User user) {
        String password = "Redsols@123";
        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Map<String, String> response = new HashMap<>();
        response.put("token", "token");

        return ResponseEntity.status(200).body(response);

    }
}
