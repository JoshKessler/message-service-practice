package com.practice.kessler.liltwitter.business.service;

public class InvalidRequestDataException extends Throwable{
    public InvalidRequestDataException(String errorMessage){
        super(errorMessage);
    }
}
