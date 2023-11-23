package ru.arman.backendxclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TweetData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tweet_data_generator")
    @SequenceGenerator(name = "tweet_data_generator", sequenceName = "tweet_data_seq", allocationSize = 1)
    private Long id;
    private Long tweetId;
    private Boolean isRetweet;
    private Timestamp postedAt;

    @ManyToMany
    @JoinTable(name = "user_tweets",
            joinColumns = @JoinColumn(name = "tweet_dto_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }
}
