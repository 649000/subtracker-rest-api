package com0.subtracker.exception;

public class SubscriptionException extends Exception{

    public SubscriptionException(String message){
        super(message);
    }

    public SubscriptionException(Throwable throwable) {
        super(throwable);
    }
}
