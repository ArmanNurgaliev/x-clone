package ru.arman.backendxclone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TweetDto {
    private String content;
    private String image;
}
