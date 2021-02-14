package me.untoldstories.be.user;

import me.untoldstories.be.user.pojos.User;
import me.untoldstories.be.user.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserInternalAPI {
    private final UserRepository userRepository;

    @Autowired
    public UserInternalAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User fetchUserNameByID(long userID) {
        return userRepository.fetchUserByUserID(userID);
    }

    public Map<Long, User> fetchUserNamesByIDs(String userIDList) {
        return userRepository.fetchUserNamesByIDs(userIDList);
    }
}
