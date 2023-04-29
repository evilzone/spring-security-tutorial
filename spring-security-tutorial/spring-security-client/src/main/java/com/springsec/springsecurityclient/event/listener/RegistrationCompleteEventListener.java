package com.springsec.springsecurityclient.event.listener;

import com.springsec.springsecurityclient.entity.User;
import com.springsec.springsecurityclient.event.RegistrationCompleteEvent;
import com.springsec.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // create the verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        // send mail to user
        userService.saveVerificationTokenForUser(token, user);

        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;
        //sendVerificationEmail();
        log.info("click the link to verify your account: {}", url);
    }
}
