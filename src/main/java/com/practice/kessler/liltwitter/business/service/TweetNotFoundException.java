package com.practice.kessler.liltwitter.business.service;

public class TweetNotFoundException extends Exception{
    public TweetNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
