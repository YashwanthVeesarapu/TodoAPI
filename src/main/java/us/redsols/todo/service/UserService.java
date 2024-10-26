package us.redsols.todo.service;

import org.springframework.stereotype.Service;

import us.redsols.todo.model.User;
import us.redsols.todo.repo.AuthRepository;

@Service
public class UserService {

    private AuthRepository authRepository;

    public UserService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User editUser(User user) {
        User newUser = authRepository.findById(user.getId()).get();
        newUser.setEmail(user.getEmail());
        newUser.setTimezone(user.getTimezone());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        return authRepository.save(newUser);
    }

    public String changePassword(User user) {
        authRepository.save(user);
        return "Password changed successfully";
    }

}
