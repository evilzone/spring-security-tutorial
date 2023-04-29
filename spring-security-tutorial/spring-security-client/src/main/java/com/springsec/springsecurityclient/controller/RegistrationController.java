package com.springsec.springsecurityclient.controller;

import com.springsec.springsecurityclient.entity.User;
import com.springsec.springsecurityclient.entity.VerificationToken;
import com.springsec.springsecurityclient.event.RegistrationCompleteEvent;
import com.springsec.springsecurityclient.model.PasswordModel;
import com.springsec.springsecurityclient.model.UserModel;
import com.springsec.springsecurityclient.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel model, final HttpServletRequest request) {
        User user = userService.registerUser(model);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success!";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(!userService.checkIfValidOldPassword(user, passwordModel.getOldPassword())) {
            return "invalid old password";
        }
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Password changed successfully";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel model, final HttpServletRequest request) {
        System.out.println("zzzz ");
        User user = userService.findUserByEmail(model.getEmail());
        System.out.println("user " + user);
        String url = "";
        if(user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordRwsetToken(token);
        if(!result.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "password reset successful";
        } else {
            return "invalid token";
        }

    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")) {
            return "user verified successfully!!";
        } else {
            return "bad user!";
        }
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken.getToken());
        return "Verify link sent!";
    }


    private String passwordResetTokenMail(User user, String appUrl, String token) {
        String url = appUrl + "/savePassword?token=" + token;
        log.info("Click on the link to reset your password: {}", url);
        return url;
    }

    private String  resendVerificationTokenMail(User user, String appUrl, String token) {
        String url = appUrl + "/verifyRegistration?token=" + token;
        log.info("Click on the link to verify your account: {}", url);
        return url;
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
