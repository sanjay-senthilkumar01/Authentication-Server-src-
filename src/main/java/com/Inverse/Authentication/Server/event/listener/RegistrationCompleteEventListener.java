package com.Inverse.Authentication.Server.event.listener;


import com.azure.communication.email.*;
import com.azure.communication.email.models.*;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.Inverse.Authentication.Server.event.RegistrationCompleteEvent;
import com.Inverse.Authentication.Server.user.User;
import com.Inverse.Authentication.Server.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component

public class RegistrationCompleteEventListener implements ApplicationListener <RegistrationCompleteEvent> {
    private final UserService userService;
    private  User theUser;
    private final JavaMailSender mailSender;

    @Autowired
    public RegistrationCompleteEventListener(UserService userService ,  JavaMailSender mailSender) {

        this.userService = userService;
        this.mailSender = mailSender;
    }
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1.Get Newly created User
        theUser = event.getUser();
        //2. CreateEmail verification token for the user
        String verificationToken = UUID.randomUUID().toString();
        //3.Save the verification token for the user
        userService.saveUserVerificationToken(theUser, verificationToken);


        //4 Build the verification url to be sent to the user
        String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;

        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        //5. Send the email.


    }
    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {

        String connectionString = "endpoint=https://inverse-mailing.unitedstates.communication.azure.com/;accesskey=fRvRxKOE/bHr20KxihRpNu5kjVt+xRB2sUKdsFT+hYMk1rEWfeZM01hvr8cZTspyTqvTi+J0rl6KJSdlujk25w==";
        EmailClient emailClient = new EmailClientBuilder().connectionString(connectionString).buildClient();
        EmailAddress toAddress = new EmailAddress(theUser.getEmail());

        // Create and send email using Azure Communication Email
        EmailMessage emailMessage = new EmailMessage()
                .setSenderAddress("DoNotReply@neuralinverse.live")
                .setToRecipients(toAddress)
                .setSubject("Welcome to Inverse Platform - Email Verification")
                .setBodyHtml("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "  <meta charset=\"UTF-8\">\n" +
                        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css\">\n" +
                        "  <title>Inverse Platform - Welcome!</title>\n" +
                        "  <style>\n" +
                        "    body {\n" +
                        "      font-family: 'Arial', sans-serif;\n" +
                        "      line-height: 1.6;\n" +
                        "      color: #E9E9E9;\n" +
                        "      background-color: #090c15;\n" +
                        "      margin: 0;\n" +
                        "      padding: 0;\n" +
                        "    }\n" +
                        "    .header {\n" +
                        "      background-color: #090c15;\n" +
                        "      padding: 10px;\n" +
                        "      text-align: left;\n" +
                        "      border-bottom: 2px solid #27ae60; /* Thunder Green */\n" +
                        "      display: flex;\n" +
                        "      align-items: center;\n" +
                        "      justify-content: center;\n" +
                        "      flex-direction: column;\n" +
                        "    }\n" +
                        "    .logo {\n" +
                        "      max-width: 60px; /* Adjust the size as needed */\n" +
                        "      margin-bottom: 10px;\n" +
                        "      animation: bounceIn 1s ease-out;\n" +
                        "    }\n" +
                        "    .header-text {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 24px;\n" +
                        "    }\n" +
                        "    .container {\n" +
                        "      max-width: 600px;\n" +
                        "      margin: 30px auto;\n" +
                        "      padding: 20px;\n" +
                        "      background-color: #090c15;\n" +
                        "      border-radius: 12px;\n" +
                        "      box-shadow: 0 0 30px rgba(0, 0, 0, 0.2);\n" +
                        "      text-align: center;\n" +
                        "      animation: fadeIn 0.8s ease-out;\n" +
                        "    }\n" +
                        "    h1 {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 24px;\n" +
                        "      margin-bottom: 10px;\n" +
                        "    }\n" +
                        "    p {\n" +
                        "      color: #E9E9E9;\n" +
                        "      margin-bottom: 20px;\n" +
                        "      font-size: 16px;\n" +
                        "      line-height: 1.6;\n" +
                        "    }\n" +
                        "    a {\n" +
                        "      display: inline-block;\n" +
                        "      padding: 15px 30px;\n" +
                        "      margin-top: 20px;\n" +
                        "      background-color: #0D1620; /* Darker shade of Thunder Green */\n" +
                        "      color: #E9E9E9;\n" +
                        "      text-decoration: none;\n" +
                        "      border-radius: 8px;\n" +
                        "      transition: background-color 0.3s;\n" +
                        "      font-weight: bold;\n" +
                        "      font-size: 16px;\n" +
                        "      border: 2px solid #27ae60; /* Thunder Green border */\n" +
                        "    }\n" +
                        "    a:hover {\n" +
                        "      background-color: #1b6e3b; /* Slightly darker shade for hover */\n" +
                        "    }\n" +
                        "    .footer {\n" +
                        "      margin-top: 30px;\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 16px;\n" +
                        "    }\n" +
                        "    .features {\n" +
                        "      margin-top: 30px;\n" +
                        "      display: flex;\n" +
                        "      flex-wrap: wrap;\n" +
                        "      justify-content: space-around;\n" +
                        "    }\n" +
                        "    .feature-box {\n" +
                        "      flex: 1;\n" +
                        "      padding: 20px;\n" +
                        "      margin: 10px;\n" +
                        "      background-color: #ada8a8; /* Light gray background */\n" +
                        "      border-radius: 8px;\n" +
                        "      text-align: center;\n" +
                        "      border: 2px solid #27ae60; /* Thunder Green border */\n" +
                        "      position: relative;\n" +
                        "    }\n" +
                        "    .feature-icon {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 32px;\n" +
                        "      margin-bottom: 10px;\n" +
                        "    }\n" +
                        "    .line {\n" +
                        "      position: absolute;\n" +
                        "      bottom: 50%;\n" +
                        "      left: 50%;\n" +
                        "      transform: translate(-50%, 50%);\n" +
                        "      width: 40%;\n" +
                        "      height: 2px;\n" +
                        "      background-color: #27ae60; /* Thunder Green */\n" +
                        "    }\n" +
                        "    .feature-title {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 20px;\n" +
                        "      margin-bottom: 10px;\n" +
                        "    }\n" +
                        "    .feature-description {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 16px;\n" +
                        "      line-height: 1.4;\n" +
                        "    }\n" +
                        "    .subscription-box {\n" +
                        "      background-color: #0D1620; /* Light gray background */\n" +
                        "      border-radius: 8px;\n" +
                        "      border: 2px solid #ada8a8; /* Thunder Green border */\n" +
                        "      padding: 20px;\n" +
                        "      margin: 20px 0;\n" +
                        "      text-align: center;\n" +
                        "    }\n" +
                        "    .subscription-icon {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 32px;\n" +
                        "      margin-bottom: 10px;\n" +
                        "    }\n" +
                        "    .subscription-title {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 20px;\n" +
                        "      margin-bottom: 10px;\n" +
                        "    }\n" +
                        "    .subscription-description {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 16px;\n" +
                        "      line-height: 1.4;\n" +
                        "    }\n" +
                        "    .pricing {\n" +
                        "      margin-top: 30px;\n" +
                        "      border-top: 2px solid #27ae60; /* Thunder Green border */\n" +
                        "      padding-top: 20px;\n" +
                        "    }\n" +
                        "    .pricing-text {\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 16px;\n" +
                        "      line-height: 1.4;\n" +
                        "    }\n" +
                        "    .check-pricing {\n" +
                        "      display: inline-block;\n" +
                        "      padding: 15px 30px;\n" +
                        "      margin-top: 20px;\n" +
                        "      background-color: #0D1620; /* Darker shade of Thunder Green */\n" +
                        "      color: #E9E9E9;\n" +
                        "      text-decoration: none;\n" +
                        "      border-radius: 8px;\n" +
                        "      transition: background-color 0.3s;\n" +
                        "      font-weight: bold;\n" +
                        "      font-size: 16px;\n" +
                        "      border: 2px solid #27ae60; /* Thunder Green border */\n" +
                        "    }\n" +
                        "    .check-pricing:hover {\n" +
                        "      background-color: #1b6e3b; /* Slightly darker shade for hover */\n" +
                        "    }\n" +
                        "    .container {\n" +
                        "      text-align: center;\n" +
                        "    }\n" +
                        "\n" +
                        "    .footer {\n" +
                        "      text-align: center;\n" +
                        "      color: #E9E9E9;\n" +
                        "      font-size: 16px;\n" +
                        "      margin-top: 30px;\n" +
                        "    }\n" +
                        "\n" +
                        "    /* Animations */\n" +
                        "    @keyframes fadeIn {\n" +
                        "      from { opacity: 0; }\n" +
                        "      to { opacity: 1; }\n" +
                        "    }\n" +
                        "\n" +
                        "    @keyframes bounceIn {\n" +
                        "      from { transform: scale(0.8); }\n" +
                        "      to { transform: scale(1); }\n" +
                        "    }\n" +
                        "\n" +
                        "    /* Responsive Styles */\n" +
                        "    @media only screen and (max-width: 600px) {\n" +
                        "      .header-text {\n" +
                        "        font-size: 20px;\n" +
                        "      }\n" +
                        "      .logo {\n" +
                        "        max-width: 40px;\n" +
                        "      }\n" +
                        "      h1 {\n" +
                        "        font-size: 20px;\n" +
                        "      }\n" +
                        "      p {\n" +
                        "        font-size: 14px;\n" +
                        "      }\n" +
                        "      a {\n" +
                        "        padding: 12px 24px;\n" +
                        "        font-size: 14px;\n" +
                        "      }\n" +
                        "      .footer {\n" +
                        "        font-size: 14px;\n" +
                        "      }\n" +
                        "      .features {\n" +
                        "        flex-direction: column;\n" +
                        "      }\n" +
                        "      .feature-box {\n" +
                        "        flex: 1 0 100%;\n" +
                        "        margin: 10px 0;\n" +
                        "      }\n" +
                        "      .line {\n" +
                        "        display: none;\n" +
                        "      }\n" +
                        "      .subscription-box {\n" +
                        "        padding: 10px;\n" +
                        "        margin: 10px 0;\n" +
                        "      }\n" +
                        "      .subscription-icon {\n" +
                        "        font-size: 24px;\n" +
                        "      }\n" +
                        "      .subscription-title {\n" +
                        "        font-size: 16px;\n" +
                        "      }\n" +
                        "      .subscription-description {\n" +
                        "        font-size: 12px;\n" +
                        "      }\n" +
                        "      .pricing-text {\n" +
                        "        font-size: 14px;\n" +
                        "      }\n" +
                        "      .check-pricing {\n" +
                        "        padding: 12px 24px;\n" +
                        "        font-size: 14px;\n" +
                        "      }\n" +
                        "    }\n" +
                        "  </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"header\">\n" +
                        "    <img src=\"https://neuralinverse.live/assets/img/N10T%20(3).png\" alt=\"Inverse Platform Logo\" class=\"logo\">\n" +
                        "    <div class=\"header-text\">Inverse By Neural Inverse</div>\n" +
                        "  </div>\n" +
                        "  <div class=\"container\">\n" +
                        "    <h1>Welcome to Inverse Platform!</h1>\n" +
                        "    <p>Hi " +theUser.getFirstName() +",</p>\n" +
                        "    <p>We're delighted to have you as part of Inverse Platform. To complete your registration, please follow the link below:</p>\n" +
                        "    <a href="+ url +">Verify your email and activate your account</a>\n" +
                        "    <p style=\"color: #939895;\">For any questions or assistance, please contact our support team at inverse@neuralinverse.live.</p>\n" +
                        "    <hr style=\"border-color: #27ae60;\"> <!-- Thunder Green line separator -->\n" +
                        "    <p>Explore the exciting features of Inverse Platform:</p>\n" +
                        "    <div class=\"features\">\n" +
                        "      <div class=\"feature-box\">\n" +
                        "        <i class=\"fas fa-users feature-icon\"></i>\n" +
                        "        <div class=\"line\"></div>\n" +
                        "        <div class=\"feature-title\">Collaboration</div>\n" +
                        "        <div class=\"feature-description\">Enhance teamwork and collaboration with real-time communication tools.</div>\n" +
                        "      </div>\n" +
                        "      <div class=\"feature-box\">\n" +
                        "        <i class=\"fas fa-rocket feature-icon\"></i>\n" +
                        "        <div class=\"line\"></div>\n" +
                        "        <div class=\"feature-title\">Fast Development</div>\n" +
                        "        <div class=\"feature-description\">Accelerate your development process with powerful and efficient tools.</div>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "    <div class=\"features\">\n" +
                        "      <div class=\"feature-box\">\n" +
                        "        <i class=\"fas fa-cogs feature-icon\"></i>\n" +
                        "        <div class=\"line\"></div>\n" +
                        "        <div class=\"feature-title\">Scalability</div>\n" +
                        "        <div class=\"feature-description\">Build and scale your projects easily, adapting to your growing needs.</div>\n" +
                        "      </div>\n" +
                        "      <div class=\"feature-box\">\n" +
                        "        <i class=\"fas fa-lightbulb feature-icon\"></i>\n" +
                        "        <div class=\"line\"></div>\n" +
                        "        <div class=\"feature-title\">Innovation</div>\n" +
                        "        <div class=\"feature-description\">Drive innovation with cutting-edge technologies and continuous improvements.</div>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "    <div class=\"subscription-box\">\n" +
                        "      <a href=\"https://neuralinverse.live/inverse\" style=\"color: #27ae60;\">\n" +
                        "        <i class=\"fas fa-info-circle subscription-icon\"></i>\n" +
                        "        <div class=\"subscription-title\">Know more about the platform</div>\n" +
                        "        <div class=\"subscription-description\">Explore additional information and features at neuralinverse.live/inverse.</div>\n" +
                        "      </a>\n" +
                        "    </div>\n" +
                        "    <div class=\"pricing\">\n" +
                        "      <p class=\"pricing-text\">Unlock premium features with our subscription plans. Choose the plan that suits your needs:</p>\n" +
                        "      <a href=\"#pricing\" class=\"check-pricing\">Check Pricing</a>\n" +
                        "    </br>\n" +
                        "      <hr style=\"border-color: #ada8a8;\"> <!-- Thunder Green line separator -->\n" +
                        "    </div>\n" +
                        "        \n" +
                        "        <div class=\"tutorial\">\n" +
                        "          <h2>Getting Started Tutorial</h2>\n" +
                        "          <p>Explore our step-by-step tutorial to quickly get started with Inverse Platform:</p>\n" +
                        "          <a href=\"#\" style=\"color:rgb(109, 111, 110)0; text-decoration: none; font-weight: bold;\">Getting Started Tutorial</a>\n" +
                        "        </div>\n" +
                        "    \n" +
                        "        <hr style=\"border-color: #ada8a8;\">\n" +
                        "    \n" +
                        "        <div class=\"ecosystem\">\n" +
                        "          <h2>About the Inverse Ecosystem</h2>\n" +
                        "          <p>Discover the Inverse Ecosystem, where innovation meets collaboration. Our platform is designed to empower individuals and teams to achieve more together.</p>\n" +
                        "        </div>\n" +
                        "    \n" +
                        "        <hr style=\"border-color: #ada8a8;\"> \n" +
                        "    \n" +
                        "    </div>\n" +
                        "    <p class=\"footer\">Thank you for choosing Inverse Platform!<br>Best Regards,<br>The Inverse Platform Team<br></p>\n" +
                        "\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>\n");

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();


    }

}
