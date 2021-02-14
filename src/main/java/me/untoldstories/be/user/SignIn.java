package me.untoldstories.be.user;

import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.user.auth.SigninNotRequired;
import me.untoldstories.be.user.auth.TokenManager;
import me.untoldstories.be.user.entities.UserEntity;
import me.untoldstories.be.user.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static me.untoldstories.be.user.MetaData.USER_SERVICE_API_ROOT_PATH;

class SignInRequest {
    @NotBlank(message = "User Name is required")
    public String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must have between 8 and 30 characters")
    public String password;
}

class SignInResponse {
    public String token;
    public Long userID;

    public SignInResponse(String token, Long userID) {
        this.token = token;
        this.userID = userID;
    }
}

@RestController
@RequestMapping(USER_SERVICE_API_ROOT_PATH)
public final class SignIn {
    private final PasswordHasher passwordHasher;
    private final UserRepository usersRepository;
    private final TokenManager tokenManager = TokenManager.getInstance();

    @Autowired
    public SignIn(UserRepository usersRepository, PasswordHasher passwordHasher) {
        this.usersRepository = usersRepository;
        this.passwordHasher = passwordHasher;
    }

    @SigninNotRequired
    @PostMapping("/signin")
    public SignInResponse signIn(@RequestBody @Valid SignInRequest signIn) {
        return manageSignIn(signIn);
    }

    /// caching exception
    private final SingleErrorMessageException noUserException = new SingleErrorMessageException("User name & password do not match any account");
    private SignInResponse manageSignIn(SignInRequest request) {
        UserEntity userEntity = usersRepository.getUserEntityByUserName(request.userName);

        String hashedPassword = passwordHasher.hash(request.password);
        if (userEntity == null || !userEntity.password.equals(hashedPassword)) throw noUserException;

        String token = tokenManager.generateToken(userEntity.id, userEntity.userName);

        return new SignInResponse(token, userEntity.id);
    }

}
