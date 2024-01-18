package us.redsols.todo.service;

import org.springframework.stereotype.Service;
import us.redsols.todo.model.User;
import us.redsols.todo.repo.AuthRepository;

import java.util.Optional;

@Service
public class AuthService {
    private AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Optional<User> getUserByUsername(String username) {
        return authRepository.findByUsername(username);
    }

    public User loginUser(User user){
        return authRepository.insert(user);
    }
}
