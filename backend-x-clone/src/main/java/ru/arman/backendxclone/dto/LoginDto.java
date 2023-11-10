package ru.arman.backendxclone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginDto {
    private String name;
    private String password;
}
