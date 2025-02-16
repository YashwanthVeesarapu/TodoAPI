package us.redsols.todo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping()
public class BaseController {
    @GetMapping()
    public String base() {
        return "Hello World";
    }

}
