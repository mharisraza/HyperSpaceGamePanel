package com.hyperspacegamepanel.models.user;

import lombok.Data;

@Data
public class UpdateUserForm {

    private String fullName;
    private String username;
    private String password;
    private String confirmPassword;
}
