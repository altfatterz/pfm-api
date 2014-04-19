package com.backbase.pfm.rest;

public class AccountDoesNotExistException extends Exception {

    private static final String MESSAGE_FORMAT = "Account '%d' does not exist";

    public AccountDoesNotExistException(Long accountId) {
        super(String.format(MESSAGE_FORMAT, accountId));
    }

}
