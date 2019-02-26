package com.practice.kessler.liltwitter.business.service;

public class RelationshipNotFoundException extends Exception{
    public RelationshipNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
