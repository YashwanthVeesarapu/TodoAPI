package us.redsols.todo.controller;

// import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.redsols.todo.config.JwtTokenProvider;
import us.redsols.todo.model.User;
import us.redsols.todo.model.UserLogin;
import us.redsols.todo.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
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
        newUser.setPassword(null);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserLogin user, HttpServletResponse response) {

        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
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

        if (existingUser.isPresent()) {
            if (passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
                String token = jwtTokenProvider.generateToken(existingUser.get().getUsername(),
                        existingUser.get().getId());

                Cookie cookie = new Cookie("access_token", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 7); // 7 days
                cookie.setSecure(true);

                response.addCookie(cookie);

                User responseUser = existingUser.get();

                // responseUser.setAccessToken(token);
                // responseUser.setPassword(null);

                // Create the response map
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("uid", responseUser.getId());

                return ResponseEntity.ok(responseBody);
            } else {
                // Passwords do not match, handle accordingly (e.g., return an error response)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect username or password");
        }
    }

    @GetMapping("me")
    public ResponseEntity<?> verifyUser(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    token = cookie.getValue();

                    Boolean isValid = jwtTokenProvider.validateToken(token);
                    if (!isValid) {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Unauthorized");

                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }

                    String uid = jwtTokenProvider.extractUid(token);

                    Map<String, String> response = new HashMap<>();
                    response.put("uid", uid);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Unauthorized");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        System.out.println("Logging out");
        // Remove all exsting access token cookies
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(true);

        res.addCookie(cookie);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public boolean checkToken(@RequestBody String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return true;
        } else {
            return false;
        }
    }
}
