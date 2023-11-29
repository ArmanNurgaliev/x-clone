package ru.arman.backendxclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.arman.backendxclone.model.TweetData;
import ru.arman.backendxclone.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TweetDtoRepository extends JpaRepository<TweetData, Long> {
    Optional<TweetData> findByTweetIdAndIsRetweet(Long tweet_id, Boolean retweet);

    List<TweetData> findByUsersIn(Collection<User> user);

    List<TweetData> findByTweetIdIn(List<Long> ids);

//    List<TweetData> findByTweetIdAAndIsRetweet(Long tweet_id, false);

//    void deleteAllByUser(User user);
}

