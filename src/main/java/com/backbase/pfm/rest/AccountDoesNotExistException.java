package com.backbase.pfm.rest;

public class AccountDoesNotExistException extends Exception {

    private static final String MESSAGE_FORMAT = "Account '%s' does not exist";

    public AccountDoesNotExistException(String accountId) {
        super(String.format(MESSAGE_FORMAT, accountId));
    }

}
