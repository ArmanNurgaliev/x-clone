package ru.arman.backendxclone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.exception.TweetDtoException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.service.TweetDtoService;

import java.util.List;

@RestController
@RequestMapping("/api/tweetDto")
@RequiredArgsConstructor
public class TweetDtoController {
    private final TweetDtoService tweetDtoService;

    @GetMapping
    public ResponseEntity<List<TweetData>> getAuthUserTweets(Authentication authentication) throws UserException {
        return ResponseEntity.ok(tweetDtoService.getAuthUserTweets(authentication));
    }

    @GetMapping("/id/{tweet_dto_id}")
    public ResponseEntity<TweetData> getTweetDtoById(@PathVariable Long tweet_dto_id) throws TweetDtoException {
        return ResponseEntity.ok(tweetDtoService.getTweetDtoById(tweet_dto_id));
    }

    @GetMapping("/user/{login}")
    public ResponseEntity<List<TweetData>> getTweetDtosByUserLogin(@PathVariable String login) throws UserException {
        return ResponseEntity.ok(tweetDtoService.getTweetDtosByUserLogin(login));
    }

    @GetMapping("/following")
    public ResponseEntity<List<TweetData>> getFollowingsTweets(Authentication authentication) throws UserException {
        return ResponseEntity.ok(tweetDtoService.getFollowingsTweets(authentication));
    }

    @GetMapping("/liked/{user_id}")
    public ResponseEntity<List<TweetData>> getLikedTweets(@PathVariable Long user_id) throws UserException {
        return ResponseEntity.ok(tweetDtoService.getLikedTweets(user_id));
    }

    @GetMapping("/replies/{comment_id}")
    public ResponseEntity<TweetData> getRepliedTweets(Authentication authentication, @PathVariable Long comment_id) throws UserException, CommentException {
        return ResponseEntity.ok(tweetDtoService.getRepliedTweets(authentication, comment_id));
    }

    @GetMapping("/media/{user_id}")
    public ResponseEntity<List<TweetData>> getTweetsMedia(@PathVariable Long user_id) throws UserException {
        return ResponseEntity.ok(tweetDtoService.getTweetsMedia(user_id));
    }

    @PutMapping("/repost/{tweet_id}")
    public ResponseEntity<MessageResponseDto> repostTweet(Authentication authentication,
                                                          @PathVariable Long tweet_id) throws UserException, TweetException, TweetDtoException {
        return ResponseEntity.ok(tweetDtoService.repostTweet(authentication, tweet_id));
    }

    @PutMapping("/undoRepost/{tweet_id}")
    public ResponseEntity<MessageResponseDto> undoRepostTweet(Authentication authentication,
                                                              @PathVariable Long tweet_id) throws UserException, TweetException, TweetDtoException {
        return ResponseEntity.ok(tweetDtoService.undoRepostTweet(authentication, tweet_id));
    }

    @GetMapping("/reposted/{tweet_id}")
    public ResponseEntity<Boolean> isTweetRepostedByUser(Authentication authentication,
                                                         @PathVariable Long tweet_id) throws UserException {
        return ResponseEntity.ok(tweetDtoService.isTweetReposted(authentication, tweet_id));
    }

    @DeleteMapping("/delete/{tweet_dto_id}")
    public ResponseEntity<MessageResponseDto> deleteTweet(Authentication authentication, @PathVariable Long tweet_dto_id) throws TweetDtoException {
        return ResponseEntity.ok(tweetDtoService.deleteTweet(authentication, tweet_dto_id));
    }
}