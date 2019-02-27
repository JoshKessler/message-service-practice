package com.practice.kessler.liltwitter.data.repository;

import com.practice.kessler.liltwitter.data.entity.User;
import com.practice.kessler.liltwitter.data.entity.UserRelationship;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRelationshipsRepository extends CrudRepository <UserRelationship, Long>{
    List<UserRelationship> findAllByFollowedId(Long followed);

}
