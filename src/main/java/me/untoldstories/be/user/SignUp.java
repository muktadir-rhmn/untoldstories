package me.untoldstories.be.user;

import me.untoldstories.be.error.exceptions.ErrorMessagePerFieldException;
import me.untoldstories.be.user.auth.SigninNotRequired;
import me.untoldstories.be.user.repos.UserRepository;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static me.untoldstories.be.user.MetaData.USER_SERVICE_API_ROOT_PATH;

class SignUpRequest {

    @Pattern(regexp = "[a-zA-Z0-9]*", message = "Only English letters and numbers allowed")
    @NotBlank(message = "User Name is required")
    public String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must have at least 8 characters and at most 30 characters")
    public String password;
}

@RestController
@RequestMapping(USER_SERVICE_API_ROOT_PATH)
public final class SignUp {
    private final UserRepository usersRepository;
    private final PasswordHasher passwordHasher;

    @Autowired
    public SignUp(UserRepository usersRepository, PasswordHasher passwordHasher) {
        this.usersRepository = usersRepository;
        this.passwordHasher = passwordHasher;
    }

    @SigninNotRequired
    @PostMapping("/signup")
    public SingleMessageResponse signUp(@RequestBody @Valid SignUpRequest signUp) {
        String hashedPassword = passwordHasher.hash(signUp.password);
        boolean hasCreated = usersRepository.createUserIfNotExists(signUp.userName, hashedPassword);

        if (hasCreated) return SingleMessageResponse.SUCCESS_RESPONSE;

        ErrorMessagePerFieldException exception = new ErrorMessagePerFieldException();
        exception.addError("userName", "User name already signed up");
        throw exception;
    }
}
