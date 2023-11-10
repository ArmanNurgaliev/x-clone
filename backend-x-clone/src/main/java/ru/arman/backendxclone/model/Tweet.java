package ru.arman.backendxclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tweets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tweet_generator")
    @SequenceGenerator(name = "tweet_generator", sequenceName = "tweets_seq", allocationSize = 1)
    private Long tweet_id;

    private String content;

    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] imageData;

    private Timestamp createdAt;

    private int repostedNumber = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "tweet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tweet_likes",
            joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedByUsers = new HashSet<>();


    public void likeTweet(User user) {
        this.likedByUsers.add(user);
        user.getLikedTweets().add(this);
    }

    public void unLikeTweet(User user) {
        this.likedByUsers.remove(user);
        user.getLikedTweets().remove(this);
    }
}
