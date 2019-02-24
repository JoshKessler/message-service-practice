package com.practice.kessler.liltwitter.business.service;

import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import com.practice.kessler.liltwitter.data.entity.UserRelationship;
import com.practice.kessler.liltwitter.data.repository.CommentRepository;
import com.practice.kessler.liltwitter.data.repository.UserRelationshipsRepository;
import com.practice.kessler.liltwitter.data.repository.TweetRepository;
import com.practice.kessler.liltwitter.data.repository.UserRepository;
import org.apache.tomcat.jni.Time;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final CommentRepository commentRepository;
    private final UserRelationshipsRepository userRelationshipsRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UserService(CommentRepository commentRepository, UserRelationshipsRepository userRelationshipsRepository, TweetRepository tweetRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRelationshipsRepository = userRelationshipsRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    public List<Tweet> getUsersTweets(String username){
        List<Tweet> usersTweets = new ArrayList<>();
        User user = userRepository.findByUserName(username);
        if (user != null){
            Iterable<Tweet> tweets = tweetRepository.findTweetByUserId(user.getId());
            if(tweets != null){
                tweets.forEach(t ->{
                    usersTweets.add(t);
                });
            }
        }

        return usersTweets;
    }

    public List<User> getFollowers(String username){
        List<User> followers = new ArrayList<>();
        User user = userRepository.findByUserName(username);
        if (user != null) {
            Iterable<Long> followerIds = userRelationshipsRepository.findAllByFollowedId(user.getId());
            if (followerIds != null) {
                followerIds.forEach(id -> {
                    Optional<User> follower = userRepository.findById(id);
                    if (follower.isPresent()) {
                        followers.add(follower.get());
                    }
                });
            }
        }
        return followers;
    }

    //TODO decide if this should return UserRelationship
    public boolean follow(String followerName, String followedName){
        User follower = userRepository.findByUserName(followerName);
        User followed = userRepository.findByUserName(followedName);
        if (follower != null && followed != null) {
            UserRelationship newFollow = new UserRelationship();
            newFollow.setFollowedId(followed.getId());
            newFollow.setFollowerId(follower.getId());
            userRelationshipsRepository.save(newFollow);
            return true;
        }
        return false;
    }

    //TODO what should this return, and update datetime issue
    public boolean tweet(String userName, String message, String timestamp){
        if (timestamp == null){
            timestamp = DATE_FORMAT.format(Time.now());
        }
        User user = userRepository.findByUserName(userName);
        if (user == null){
            return false;
        }
        Tweet tweet = new Tweet();
        //tweet.setTimestamp(timestamp);
        tweet.setUserId(user.getId());
        tweet.setTweet(message);
        tweetRepository.save(tweet);
        return true;
    }

    //TODO if optional fields are null, do I need to check for that and not set them?
    public boolean createAccount(String userName, String actualName, String location){
        User user = new User();
        user.setUserName(userName);
        user.setName(actualName);
        user.setLocation(location);
    }


}
