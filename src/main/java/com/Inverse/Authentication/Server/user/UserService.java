package com.Inverse.Authentication.Server.user;

import com.Inverse.Authentication.Server.exception.UserAlreadyExistsException;
import com.Inverse.Authentication.Server.registration.RegistrationRequest;
import com.Inverse.Authentication.Server.registration.token.VerificationToken;
import com.Inverse.Authentication.Server.registration.token.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service

public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder ,VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(@RequestBody RegistrationRequest request) {
        Optional<User> userByEmail = this.findByEmail(request.email());
        Optional<User> userByUsername = this.findByUsername(request.email());

        if (userByEmail.isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with email " + request.email() + " already exists");
        }

        if (userByUsername.isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with username " + request.username() + " already exists");
        }
        var newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        tokenRepository.save(verificationToken);

    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = tokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid verification token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            tokenRepository.delete(token);
            return "Token already expired";
        }


        user.setisEnabled(true);
        userRepository.save(user);
        return "valid";
    }
}
