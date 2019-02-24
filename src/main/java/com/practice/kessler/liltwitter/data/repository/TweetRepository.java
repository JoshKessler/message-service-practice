package com.practice.kessler.liltwitter.data.repository;

import com.practice.kessler.liltwitter.data.entity.Tweet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends CrudRepository <Tweet, Long>{
    List<Tweet> findTweetByUserId(Long id);
}
