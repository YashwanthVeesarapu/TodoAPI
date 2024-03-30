package us.redsols.todo.service;

import org.springframework.stereotype.Service;

import us.redsols.todo.model.EditUser;
import us.redsols.todo.model.User;
import us.redsols.todo.repo.AuthRepository;

@Service
public class UserService {

    private AuthRepository authRepository;

    public UserService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User editUser(EditUser user) {
        User newUser = authRepository.findById(user.getUid()).get();
        newUser.setEmail(user.getEmail());
        newUser.setTimezone(user.getTimezone());
        return authRepository.save(newUser);
    }

}
