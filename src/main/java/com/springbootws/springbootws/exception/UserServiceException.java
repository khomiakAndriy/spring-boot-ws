package com.springbootws.springbootws.exception;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = -9071525021733370514L;

    public UserServiceException(String message) {
        super(message);
    }
}
