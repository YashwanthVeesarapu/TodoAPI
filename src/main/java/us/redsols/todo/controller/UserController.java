package us.redsols.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.EditUser;
import us.redsols.todo.model.User;
import us.redsols.todo.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // edit user
    @PutMapping("edit")
    public ResponseEntity<?> edit(@RequestBody EditUser user, @RequestHeader("Authorization") String token) {
        // can edit email and timezone
        // error checking

        if (user.getEmail() == null || user.getTimezone() == null) {
            return ResponseEntity.badRequest().body("Email and timezone are required");
        }

        if (!token.isEmpty()) {
            String extractedUid = jwtTokenProvider.extractUid(token);

            if (extractedUid.equals(user.getUid())) {
                User newUser = userService.editUser(user);
                if (newUser == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
                }
                return ResponseEntity.ok(newUser);
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Access");
        }

    }

}
