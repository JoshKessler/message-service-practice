package com.practice.kessler.liltwitter.data.repository;

import com.practice.kessler.liltwitter.data.entity.Comment;
import com.practice.kessler.liltwitter.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository <Comment, Long>{
    List<Comment> findAllByOriginalTweetId(Long id);
}
