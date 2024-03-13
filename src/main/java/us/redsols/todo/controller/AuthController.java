package us.redsols.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.User;
import us.redsols.todo.service.AuthService;
import us.redsols.todo.service.TodoService;

import java.sql.PreparedStatement;
import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthController {

    private AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    private BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User user){
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password are required");
        }
        // Check minimum password length
        if (user.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 4 characters long");
        }
        if (user.getUsername().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must be at least 4 characters long");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = authService.addUser(user);
        String token = jwtTokenProvider.generateToken(newUser.getUsername(), newUser.getId());
        newUser.setAccessToken(token);
        newUser.setPassword("");
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user){

        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password are required");
        }
        // Check minimum password length
        if (user.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 4 characters long");
        }
        if (user.getUsername().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must be at least 4 characters long");
        }

        Optional<User> existingUser = authService.getUserByUsername(user.getUsername());

        if(existingUser.isPresent()){
            if (passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
                String token = jwtTokenProvider.generateToken(existingUser.get().getUsername(), existingUser.get().getId());
                existingUser.get().setAccessToken(token);
                existingUser.get().setPassword("");
                return ResponseEntity.ok(existingUser.get());
            } else {
                // Passwords do not match, handle accordingly (e.g., return an error response)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect username or password");
        }
    }


    public boolean checkToken(@RequestBody String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return true;
        } else {
            return false;
        }
    }
}
