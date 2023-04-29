package com.springsec.springsecurityclient.service;

import com.springsec.springsecurityclient.entity.User;
import com.springsec.springsecurityclient.entity.VerificationToken;
import com.springsec.springsecurityclient.model.UserModel;

import java.util.Optional;

public interface UserService {
    public User registerUser(UserModel userModel);
    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String token);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordRwsetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);
}
