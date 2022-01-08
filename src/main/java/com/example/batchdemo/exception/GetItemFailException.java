package com.example.batchdemo.exception;

public class GetItemFailException extends Exception{
    public GetItemFailException(String message){
        super(message);
    }
    public GetItemFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
