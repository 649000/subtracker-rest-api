package com.subtracker.exception;

public class SubscriptionException extends Exception{

    public SubscriptionException(String message){
        super(message);
    }

    public SubscriptionException(Throwable throwable) {
        super(throwable);
    }
}
