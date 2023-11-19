package com.Inverse.Authentication.Server.user;

import com.Inverse.Authentication.Server.registration.RegistrationRequest;
import com.Inverse.Authentication.Server.registration.token.VerificationToken;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getUsers();
    User registerUser(RegistrationRequest request);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    void saveUserVerificationToken(User theUser, String verificationToken);


    @Transactional
    String validateToken(String theToken);
}
