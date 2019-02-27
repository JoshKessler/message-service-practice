package com.practice.kessler.liltwitter.business.service;

import com.practice.kessler.liltwitter.business.domain.TweetWithComments;
import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.Tweet;
import com.practice.kessler.liltwitter.data.entity.User;
import com.practice.kessler.liltwitter.data.entity.UserRelationship;
import com.practice.kessler.liltwitter.data.repository.CommentRepository;
import com.practice.kessler.liltwitter.data.repository.UserRelationshipsRepository;
import com.practice.kessler.liltwitter.data.repository.TweetRepository;
import com.practice.kessler.liltwitter.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {
    private final CommentRepository commentRepository;
    private final UserRelationshipsRepository userRelationshipsRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static DateTimeFormatter getDateFormat() {
        return DATE_FORMAT;
    }

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
        throw new UserNotFoundException("No user with username " +userName + " found.");
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
            Iterable<UserRelationship> relationships = userRelationshipsRepository.findAllByFollowedId(user.getId());
            if (relationships != null) {
                relationships.forEach(r -> {
                    Optional<User> follower = userRepository.findById(r.getFollowerId());
                    if (follower.isPresent()) {
                        followers.add(follower.get());
                    }
                });
            }
            return followers;
        }
        throw new UserNotFoundException("No user with that username found.");
    }

    //TODO user can currently follow self (this should be enforced in DB schema instead)
    public UserRelationship follow(String requesterName, String followedName) throws UserNotFoundException, RelationshipAlreadyExistsException {
        User follower = userRepository.findByUserName(requesterName);
        User followed = userRepository.findByUserName(followedName);
        if (follower == null){
            throw new UserNotFoundException("Your username, " + requesterName + ", not found.");
        }
        if (followed == null){
            throw new UserNotFoundException("No user with the username " + followedName + " found.");
        }

        //this should really be enforced by database schema instead
        UserRelationship userRelationship = userRelationshipsRepository.findByFollowerIdAndFollowedId(follower.getId(), followed.getId());
        if (userRelationship != null){
            throw new RelationshipAlreadyExistsException("You're already following this user.");
        }

        UserRelationship newFollow = new UserRelationship();
        newFollow.setFollowedId(followed.getId());
        newFollow.setFollowerId(follower.getId());
        return userRelationshipsRepository.save(newFollow);
    }

    public Tweet tweet(String userName, String message) throws UserNotFoundException {
        ZonedDateTime submittedTime = ZonedDateTime.now();

        User user = userRepository.findByUserName(userName);
        if (user == null){
            throw new UserNotFoundException("Your username not found.");
        }
        Tweet tweet = new Tweet();
        tweet.setUserId(user.getId());
        tweet.setTweet(message);
        tweet.setTimestamp(submittedTime);
        return tweetRepository.save(tweet);
    }

    public User createAccount(String userName, String actualName, String location){
        User user = new User();
        user.setUserName(userName);
        user.setName(actualName);
        user.setLocation(location);
        return userRepository.save(user);
    }

    public Comment comment(String tweetId, String message, String commenterUserName) throws TweetNotFoundException, RelationshipNotFoundException, UserNotFoundException {
        HashMap<String, Long> userIds = validateTweetRelationship(commenterUserName, tweetId);
        ZonedDateTime submittedTime = ZonedDateTime.now();
        Comment comment = new Comment();
        comment.setOriginalTweetId(Long.parseLong(tweetId));
        comment.setWrittenById(userIds.get("commenterId"));
        comment.setComment(message);
        comment.setTimestamp(submittedTime);
        return commentRepository.save(comment);
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
        User user = userRepository.findByUserName(userName);
        if (user == null){
            throw new UserNotFoundException("That username not found");
        }

        return getFollowedUsers(user);
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

    public List<Comment> getCommentsAssociatedWithTweetIfFollowing(String requesterName, String tweetId) throws UserNotFoundException, RelationshipNotFoundException, TweetNotFoundException {
        validateTweetRelationship(requesterName, tweetId);
        return getCommentsAssociatedWithTweet(tweetId);
    }

    //can return either list of tweets or list of tweets mapped to associated comments
    public List<TweetWithComments> getAllUserContentWithRequester(String requesterName, String posterUserName) throws UserNotFoundException {
        User poster = userRepository.findByUserName(posterUserName);
        if (poster == null ){
            throw new UserNotFoundException("No user with the username " + posterUserName + " found.");
        }
        User requester = userRepository.findByUserName(requesterName);
        List<Tweet> usersTweets = getUsersTweets(posterUserName);
        List<TweetWithComments> tweetsWithComments = new ArrayList<>();
        usersTweets.forEach(tweet -> {
            try {
                List<Comment> comments = getCommentsAssociatedWithTweet(String.valueOf(tweet.getId()));
                tweetsWithComments.add(new TweetWithComments(tweet, comments));
            } catch (TweetNotFoundException e) {
                e.printStackTrace();
                tweetsWithComments.add(new TweetWithComments(tweet, null));
            }
        });
        return tweetsWithComments;
    }

    private List<User> getFollowedUsers(User user){
        List<UserRelationship> relationships = userRelationshipsRepository.findAllByFollowerId(user.getId());
        List<User> result = new ArrayList<>();
        relationships.forEach(r ->{
            Optional<User> opt = userRepository.findById(r.getFollowedId());
            if (opt.isPresent()){
                result.add(opt.get());
            }
        });
        return result;
    }

    public boolean validateUserRelationship (String requesterName, String posterUserName){
        User requester = userRepository.findByUserName(requesterName); //called after another method checks for null so don't check here
        User poster = userRepository.findByUserName(posterUserName);

        return getFollowedUsers(requester).contains(poster);
    }

    private HashMap<String, Long> validateTweetRelationship(String commenterUserName, String tweetId) throws UserNotFoundException, RelationshipNotFoundException, TweetNotFoundException {
        HashMap<String, Long> userIds = new HashMap<>();
        User commenter = userRepository.findByUserName(commenterUserName);
        if (commenter == null){
            throw new UserNotFoundException("Your username not found.");
        }
        Optional<Tweet> tweet = tweetRepository.findById(Long.parseLong(tweetId));
        if (tweet.isPresent()) {
            long tweeterUserId = tweet.get().getUserId();
            Optional<User> tweeter = userRepository.findById(tweeterUserId);
            if (tweeter.isPresent()) {
                if (getFollowedUsers(commenter).contains(tweeter.get())) {
                    userIds.put("commenterId", commenter.getId());
                    userIds.put("tweeterId", tweeterUserId);
                    return userIds;
                } else throw new RelationshipNotFoundException("You can't comment on this tweet because you're not following " + tweeter.get().getUserName() + ".");
            }
            else throw new RuntimeException("Well this is awkward. We found the tweet but its author seems to have gone missing and your comment didn't get posted.");
        } else throw new TweetNotFoundException("No tweet found with that ID.");
    }

    public Tweet getSpecificTweet(long id) throws TweetNotFoundException {
        Optional<Tweet> tweet = tweetRepository.findById(id);
        if (tweet.isPresent()){
            return tweet.get();
        }
        throw new TweetNotFoundException("No tweet with that ID found.");
    }

}
