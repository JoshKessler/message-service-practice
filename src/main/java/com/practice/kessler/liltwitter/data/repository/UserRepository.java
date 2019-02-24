package com.practice.kessler.liltwitter.data.repository;

import com.practice.kessler.liltwitter.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository <User, Long>{
}
