package me.untoldstories.be.user;

import me.untoldstories.be.user.pojos.User;
import me.untoldstories.be.user.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static me.untoldstories.be.user.MetaData.USER_SERVICE_API_ROOT_PATH;

@RestController
@RequestMapping(USER_SERVICE_API_ROOT_PATH)
public class UserFetcher {
    private final UserRepository userRepository;

    @Autowired
    public UserFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userID}")
    public User fetchUser(@PathVariable long userID) {
        return userRepository.fetchUserByUserID(userID);
    }
}
