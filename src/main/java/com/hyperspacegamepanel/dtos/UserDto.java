package com.hyperspacegamepanel.dtos;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;

    @NotBlank(message = "Full Name is required.")
    private String fullName;

    @NotBlank(message = "Email Address is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    private String role;
    
    private boolean isEnabled = true;

}
