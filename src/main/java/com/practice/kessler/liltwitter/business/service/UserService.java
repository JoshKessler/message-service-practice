package com.practice.kessler.liltwitter.business.service;

import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import com.practice.kessler.liltwitter.data.entity.UserRelationship;
import com.practice.kessler.liltwitter.data.repository.CommentRepository;
import com.practice.kessler.liltwitter.data.repository.UserRelationshipsRepository;
import com.practice.kessler.liltwitter.data.repository.TweetRepository;
import com.practice.kessler.liltwitter.data.repository.UserRepository;
import org.apache.tomcat.jni.Time;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public List<Tweet> getUsersTweets(String userName){
        List<Tweet> usersTweets = new ArrayList<>();
        User user = userRepository.findByUserName(userName);
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
    public UserRelationship follow(String followerName, String followedName){
        User follower = userRepository.findByUserName(followerName);
        User followed = userRepository.findByUserName(followedName);
        if (follower != null && followed != null) {
            UserRelationship newFollow = new UserRelationship();
            newFollow.setFollowedId(followed.getId());
            newFollow.setFollowerId(follower.getId());
            return userRelationshipsRepository.save(newFollow);
        }
        return null;
    }

    //TODO what should this return, and update datetime issue
    public Tweet tweet(String userName, String message, String timestamp){
        if (timestamp == null){
            timestamp = DATE_FORMAT.format(Time.now());
        }
        User user = userRepository.findByUserName(userName);
        if (user == null){
            return null;
        }
        Tweet tweet = new Tweet();
        //tweet.setTimestamp(timestamp);
        tweet.setUserId(user.getId());
        tweet.setTweet(message);
        return tweetRepository.save(tweet);
    }

    //TODO if optional fields are null, do I need to check for that and not set them?
    public User createAccount(String userName, String actualName, String location){
        User user = new User();
        user.setUserName(userName);
        user.setName(actualName);
        user.setLocation(location);
        return userRepository.save(user);
    }

    //TODO change string return to error, update datetime issue
    public Comment comment(String tweetId, String message, String commenterUserName) {
        User commenter = userRepository.findByUserName(commenterUserName);
        Optional<Tweet> tweet = tweetRepository.findById(Long.parseLong(tweetId));
        if (tweet.isPresent()) {
            long tweeterUserId = tweet.get().getUserId();
            Optional<User> tweeter = userRepository.findById(tweeterUserId);
            if (tweeter.isPresent()) {
                if (commenter.getFollowedUsers().contains(tweeter.get())) {
                    Comment comment = new Comment();
                    comment.setOriginalTweetId(Long.parseLong(tweetId));
                    comment.setWrittenById(commenter.getId());
                    //comment.setTimestamp(Time.now());
                    return commentRepository.save(comment);
                    //"Can't comment on someone you're not following"
                } else return null;
            }

            else return null; //"something went wrong. found tweet, but can't locate its author"
        } else return null; //"No tweet with that ID"
    }

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        Iterable<User> storedUsers = userRepository.findAll();
        storedUsers.forEach(user -> {
            users.add(user);
        });
        return users;
    }


    public List<User> getFollowedUsers(String userName) {
        List<User> following = new ArrayList<>();
        User user = userRepository.findByUserName(userName);
        if (user != null) {
            following.addAll(user.getFollowedUsers());
            }
        return following;
    }

    public List<Comment> getCommentsAssociatedWithTweet(String tweetId){
        List<Comment> result = new ArrayList<>();
        Iterable<Comment> comments = commentRepository.findAllByOriginalTweetId(Long.parseLong(tweetId));
        comments.forEach(comment -> {
            result.add(comment);
        });
        return result;
    }

}
