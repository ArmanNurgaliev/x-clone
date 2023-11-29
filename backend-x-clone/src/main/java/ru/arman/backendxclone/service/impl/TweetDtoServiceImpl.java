package ru.arman.backendxclone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.dto.MessageResponseDto;
import ru.arman.backendxclone.exception.CommentException;
import ru.arman.backendxclone.exception.TweetDtoException;
import ru.arman.backendxclone.exception.TweetException;
import ru.arman.backendxclone.exception.UserException;
import ru.arman.backendxclone.model.Comment;
import ru.arman.backendxclone.model.Tweet;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.model.User;
import ru.arman.backendxclone.repository.TweetDtoRepository;
import ru.arman.backendxclone.repository.TweetRepository;
import ru.arman.backendxclone.service.CommentService;
import ru.arman.backendxclone.service.TweetDtoService;
import ru.arman.backendxclone.service.TweetService;
import ru.arman.backendxclone.service.UserService;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetDtoServiceImpl implements TweetDtoService {
    private final UserService userService;
    private final TweetService tweetService;
    private final TweetDtoRepository tweetDtoRepository;
    private final TweetRepository tweetRepository;
    private final CommentService commentService;

    @Override
    public List<TweetData> getAuthUserTweets(Authentication authentication) throws UserException {
        User user = userService.getUserByName(authentication.getName());
        List<TweetData> tweets = tweetDtoRepository.findByUsersIn(List.of(user));
        tweets.sort(Comparator.comparing(TweetData::getPostedAt).reversed());

        return tweets;
    }

    @Override
    public TweetData getTweetDtoById(Long tweet_dto_id) throws TweetDtoException {
        return tweetDtoRepository.findById(tweet_dto_id)
                .orElseThrow(() -> new TweetDtoException("TweetDto not found with id: " + tweet_dto_id));
    }

    @Override
    public MessageResponseDto repostTweet(Authentication authentication, Long tweet_id) throws UserException, TweetException, TweetDtoException {
        User user = userService.getUserByName(authentication.getName());

        TweetData tweetDto = tweetDtoRepository.findByTweetIdAndIsRetweet(tweet_id, false)
                .orElseThrow(() -> new TweetDtoException("Tweet dto not found with tweet id: " + tweet_id));

        TweetData repostedTweetDto = new TweetData();
        repostedTweetDto.setPostedAt(new Timestamp(System.currentTimeMillis()));
        repostedTweetDto.setIsRetweet(true);
        repostedTweetDto.setTweetId(tweet_id);
        repostedTweetDto.addUser(user);
        tweetDtoRepository.save(repostedTweetDto);

        Tweet tweet = tweetService.getTweetById(tweet_id);
        tweet.setRepostedNumber(tweet.getRepostedNumber()+1);
        tweetRepository.save(tweet);

        return new MessageResponseDto("Tweet reposted by " + user.getName());
    }

    @Override
    public MessageResponseDto undoRepostTweet(Authentication authentication, Long tweet_id) throws UserException, TweetException, TweetDtoException {
        User user = userService.getUserByName(authentication.getName());

        TweetData tweetDto = tweetDtoRepository.findByTweetIdAndIsRetweet(tweet_id, true)
                .orElseThrow(() -> new TweetDtoException("Tweet dto not found with tweet id: " + tweet_id));

        tweetDtoRepository.delete(tweetDto);

        Tweet tweet = tweetService.getTweetById(tweet_id);
        tweet.setRepostedNumber(tweet.getRepostedNumber()-1);
        tweetRepository.save(tweet);

        return new MessageResponseDto("Tweet unreposted by " + user.getName());
    }

    @Override
    public List<TweetData> getTweetDtosByUserLogin(String login) throws UserException {
        User user = userService.getUserByLogin(login);

        List<TweetData> tweets = tweetDtoRepository.findByUsersIn(List.of(user));
        tweets.sort(Comparator.comparing(TweetData::getPostedAt).reversed());

        return tweets;
    }

    @Override
    public Boolean isTweetReposted(Authentication authentication, Long tweet_id) throws UserException {
        User user = userService.getUserByName(authentication.getName());

        return tweetDtoRepository.findByTweetIdAndIsRetweet(tweet_id, true)
                .map(TweetData::getUsers).stream().anyMatch(t -> t.contains(user));
    }

    @Override
    public MessageResponseDto deleteTweet(Authentication authentication, Long tweet_dto_id) throws TweetDtoException {
        TweetData tweetDto = getTweetDtoById(tweet_dto_id);

        if (!tweetDto.getIsRetweet()) {
            Tweet tweet = tweetRepository.findById(tweetDto.getTweetId()).get();
            tweetRepository.delete(tweet);
        }
        tweetDtoRepository.delete(tweetDto);

        return new MessageResponseDto("Tweet with id " + tweetDto.getTweetId() + " deleted");
    }

    @Override
    public List<TweetData> getFollowingsTweets(Authentication authentication) throws UserException {
        User user = userService.getUserByName(authentication.getName());

        List<TweetData> tweetData = tweetDtoRepository.findByUsersIn(user.getFollowing());
        tweetData.sort(Comparator.comparing(TweetData::getPostedAt).reversed());
        return tweetData;
    }

    @Override
    public List<TweetData> getLikedTweets(Long user_id) throws UserException {
        User user = userService.getUserById(user_id);
        List<Long> tweetIds = user.getLikedTweets().stream().map(Tweet::getTweet_id).toList();

        return tweetDtoRepository.findByTweetIdIn(tweetIds).stream().distinct().toList();
    }

    @Override
    public TweetData getRepliedTweets(Authentication authentication, Long comment_id) throws CommentException {
        Comment comment = commentService.getCommentById(comment_id);
        return tweetDtoRepository.findByTweetIdAndIsRetweet(comment.getTweet().getTweet_id(), false).get();
    }

    @Override
    public List<TweetData> getTweetsMedia(Long user_id) throws UserException {
        List<Long> tweet_ids = tweetService.getUserTweets(user_id).stream()
                .filter(t -> t.getImageData() != null)
                .map(Tweet::getTweet_id).toList();

        return tweetDtoRepository.findByTweetIdIn(tweet_ids).stream().distinct().toList();
    }


}
