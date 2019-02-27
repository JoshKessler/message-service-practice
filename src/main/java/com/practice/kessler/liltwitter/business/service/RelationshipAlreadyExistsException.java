package com.practice.kessler.liltwitter.business.service;

public class RelationshipAlreadyExistsException extends Throwable {
    public RelationshipAlreadyExistsException(String errorMessage){
            super(errorMessage);
        }
}
