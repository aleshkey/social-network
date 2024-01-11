package com.socialnetwork.back.error;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String string) {
        super(string);
    }
}
