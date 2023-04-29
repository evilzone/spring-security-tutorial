package com.springsec.springsecurityclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordModel {

    private String email;
    private String oldPassword;
    private String newPassword;
}
