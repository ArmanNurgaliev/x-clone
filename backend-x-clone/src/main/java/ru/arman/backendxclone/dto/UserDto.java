package ru.arman.backendxclone.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {
    private String name;
    private String bio;
    private String website;
    private String location;
    private String image;
    private String backgroundImage;
}
