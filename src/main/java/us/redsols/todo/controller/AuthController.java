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
import us.redsols.todo.constants.AppConstants;
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

// timezones = [
//     { value: 'America/Los_Angeles', label: 'Pacific Time' },
//     { value: 'America/New_York', label: 'Eastern Time' },
//     { value: 'America/Chicago', label: 'Central Time' },
//     { value: 'America/Denver', label: 'Mountain Time' },
//     { value: 'America/Anchorage', label: 'Alaska Time' },
//     { value: 'Pacific/Honolulu', label: 'Hawaii Time' },
//     { value: 'Europe/London', label: 'London' },
//     { value: 'Europe/Paris', label: 'Paris' },
//     { value: 'Asia/Tokyo', label: 'Tokyo' },
//     { value: 'Australia/Sydney', label: 'Sydney' },
//     { value: 'Asia/Dubai', label: 'Dubai' },
//     { value: 'Asia/Kolkata', label: 'India Standard Time' },
//     { value: 'Asia/Shanghai', label: 'Shanghai' },
//     { value: 'Asia/Singapore', label: 'Singapore' },
//     { value: 'Asia/Hong_Kong', label: 'Hong Kong' },
//     { value: 'Asia/Bangkok', label: 'Bangkok' },
//     { value: 'Asia/Jakarta', label: 'Jakarta' },
//     { value: 'Asia/Kuala_Lumpur', label: 'Kuala Lumpur' },
//     { value: 'Asia/Manila', label: 'Manila' },
//   ];

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

        // Check if timezone is provided
        if (user.getTimezone() == null || user.getTimezone().isEmpty()) {

            user.setTimezone("America/New_York"); // Default timezone if not provided
        } else {
            // Validate timzone is present in the list of timezones
            boolean validTimezone = AppConstants.TIMEZONES.stream()
                    .anyMatch(tz -> tz.get("value").equals(user.getTimezone()));
            if (!validTimezone) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid timezone provided");
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = authService.addUser(user);
        String token = jwtTokenProvider.generateToken(newUser.getUsername(), newUser.getId(), newUser.getTimezone());
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

        String tz = existingUser.get() != null ? existingUser.get().getTimezone() : "America/New_York"; // Default
                                                                                                        // timezone if
                                                                                                        // not found

        if (existingUser.isPresent()) {
            if (passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
                String token = jwtTokenProvider.generateToken(existingUser.get().getUsername(),
                        existingUser.get().getId(), tz);

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
                responseBody.put("timezone", responseUser.getTimezone());

                return ResponseEntity.ok(responseBody);
            } else {
                // Passwords do not match, handle accordingly (e.g., return an error response)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect username or password");
        }
    }

    @GetMapping("verify")
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
                    response.put("timezone", jwtTokenProvider.extractTimezone(token));
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
