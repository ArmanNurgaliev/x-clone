package ru.arman.backendxclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Setter
public class RegistrationDto {
    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Login can't be empty")
    private String login;
    @NotBlank(message = "Email can't be empty")
    private String email;
    @NotBlank(message = "password can't be empty")
    @Size(min = 4, message = "Password must be at least 4 symbols")
    private String password;
    private Date dob;
}
