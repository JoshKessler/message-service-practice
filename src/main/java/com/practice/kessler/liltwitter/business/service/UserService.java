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

import javax.swing.text.html.Option;
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

    public List<Tweet> getUsersTweets(String userName) throws UserNotFoundException {
        List<Tweet> usersTweets = new ArrayList<>();
        User user = userRepository.findByUserName(userName);
        if (user != null){
            Iterable<Tweet> tweets = tweetRepository.findTweetByUserId(user.getId());
            if(tweets != null){
                tweets.forEach(t ->{
                    usersTweets.add(t);
                });
            }
            return usersTweets;
        }
        throw new UserNotFoundException("No user with that username found.");
    }

    public User getUser(String userName) throws UserNotFoundException {
        List<Tweet> usersTweets = new ArrayList<>();
        User user = userRepository.findByUserName(userName);
        if (user == null){
            throw new UserNotFoundException("No user with that username found.");
        }
        return user;
    }

    public List<User> getFollowers(String username) throws UserNotFoundException {
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
            return followers;
        }
        throw new UserNotFoundException("No user with that username found.");
    }

    public UserRelationship follow(String followerName, String followedName) throws UserNotFoundException {
        User follower = userRepository.findByUserName(followerName);
        User followed = userRepository.findByUserName(followedName);
        if (follower == null){
            throw new UserNotFoundException("No user with the username " + followerName + " found.");
        }
        if (followed == null){
            throw new UserNotFoundException("No user with the username " + followedName + " found.");
        }

        UserRelationship newFollow = new UserRelationship();
        newFollow.setFollowedId(followed.getId());
        newFollow.setFollowerId(follower.getId());
        return userRelationshipsRepository.save(newFollow);
    }

    //TODO figure out datetime issue
    public Tweet tweet(String userName, String message, String timestamp) throws UserNotFoundException {
        if (timestamp == null){
            timestamp = DATE_FORMAT.format(Time.now());
        }
        User user = userRepository.findByUserName(userName);
        if (user == null){
            throw new UserNotFoundException("Your username not found.");
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
    public Comment comment(String tweetId, String message, String commenterUserName) throws TweetNotFoundException, RelationshipNotFoundException, UserNotFoundException {
        User commenter = userRepository.findByUserName(commenterUserName);
        if (commenter == null){
            throw new UserNotFoundException("Your username not found.");
        }
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
                } else throw new RelationshipNotFoundException("You can't comment on this tweet because you're not following" + tweeter.get().getUserName() + ".");
            }
            else throw new RuntimeException("Well this is awkward. We found the tweet but its author seems to have gone missing and your comment didn't get posted.");
        } else throw new TweetNotFoundException("No tweet found with that ID.");
    }

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        Iterable<User> storedUsers = userRepository.findAll();
        storedUsers.forEach(user -> {
            users.add(user);
        });
        return users;
    }

    public List<User> getFollowedUsers(String userName) throws UserNotFoundException {
        List<User> following = new ArrayList<>();
        User user = userRepository.findByUserName(userName);
        if (user == null){
            throw new UserNotFoundException("That username not found");
        }
        following.addAll(user.getFollowedUsers());
        return following;
    }

    public List<Comment> getCommentsAssociatedWithTweet(String tweetId) throws TweetNotFoundException {
        List<Comment> result = new ArrayList<>();
        Iterable<Comment> comments = commentRepository.findAllByOriginalTweetId(Long.parseLong(tweetId));
        comments.forEach(comment -> {
            result.add(comment);
        });
        if (result.isEmpty()){
            Optional<Tweet> tweet = tweetRepository.findById(Long.parseLong(tweetId));
            if (!tweet.isPresent()){
                throw new TweetNotFoundException("No tweet with that ID found.");
            }
        }
        return result;
    }

}
