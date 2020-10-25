package com.subtracker.exception;

public class UserException extends Exception{

    public UserException(String message){
        super(message);
    }

    public UserException(Throwable throwable) {
        super(throwable);
    }
}
