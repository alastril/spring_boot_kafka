package com.myboot.exceptions;

public class CustomUserNotFoundException extends Exception {
    public CustomUserNotFoundException(String message) {
        super(message);
    }
}
