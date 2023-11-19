package com.Inverse.Authentication.Server.registration;


import com.Inverse.Authentication.Server.event.RegistrationCompleteEvent;
import com.Inverse.Authentication.Server.registration.RegistrationRequest;
import com.Inverse.Authentication.Server.registration.token.VerificationToken;
import com.Inverse.Authentication.Server.registration.token.VerificationTokenRepository;
import com.Inverse.Authentication.Server.user.User;
import com.Inverse.Authentication.Server.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @Autowired
    public RegistrationController(UserService userService, ApplicationEventPublisher publisher,VerificationTokenRepository tokenRepository) {
        this.userService = userService;
        this.publisher = publisher;
        this.tokenRepository = tokenRepository;
    }


    @PostMapping
    private String registerUser(@RequestBody RegistrationRequest registrationRequest , final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        // publish registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));

        return "Success! Please check your email for registration comfirmation and Welcome Inverse Platform by Neural Inverse.";
    }


    public String applicationUrl(HttpServletRequest request) {
        return "http://"+ request.getServerName()+":"+ request.getServerPort()+request.getContextPath();
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam ("token") String token){
        VerificationToken theToken = tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "Your account has been verified.Please log in to continue.";
        }
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verified! Welcome to the INverse Platform by Neural Inverse. Now you can log in to your account and start using Inverse platform." ;
        }
        return "Your email verification token is invalid. Please try again.";
    }
}
