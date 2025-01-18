package us.redsols.todo.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.User;
import us.redsols.todo.service.AuthService;
import us.redsols.todo.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // edit user
    @PutMapping("update")
    public ResponseEntity<?> edit(@RequestBody User user, HttpServletRequest req) {
        // can edit email and timezone
        // error checking
        if (user.getEmail() == null || user.getTimezone() == null) {
            return ResponseEntity.badRequest().body("Email and timezone are required");
        }

        String extractedUid = req.getAttribute("uid").toString();

        if (extractedUid.equals(user.getId())) {
            User editedUser = userService.editUser(user);
            if (editedUser == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
            }

            editedUser.setPassword(null);

            return ResponseEntity.ok(editedUser);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

    }

    public static class ChangePassword {
        private String oldPassword;
        private String newPassword;

        public ChangePassword(
                String oldPassword,
                String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword object, HttpServletRequest req) {

        // error checking
        if (object.getOldPassword() == null || object.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Current and new password are required");
        }

        if (object.getNewPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 4 characters long");
        }

        // both passwords are same
        if (object.getOldPassword().equals(object.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be same as old password");
        }

        // extract user id from token
        String extractedUid = req.getAttribute("uid").toString();
        // get user from db
        Optional<User> user = authService.getUserById(extractedUid);

        if (user.isPresent()) {
            // check if old password matches
            if (passwordEncoder.matches(object.getOldPassword(), user.get().getPassword())) {

                String hashedNewPassword = passwordEncoder.encode(object.getNewPassword());
                // change password
                user.get().setPassword(hashedNewPassword);
                // save user

                userService.changePassword(user.get());
                user.get().setPassword(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password did not match");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }

}
