package com.backbase.pfm.rest;

public class GoalDoesNotExistException extends Exception {

    private static final String MESSAGE_FORMAT = "Goal '%s' for account '%s' does not exist";

    public GoalDoesNotExistException(String accountId, String goalId) {
        super(String.format(MESSAGE_FORMAT, goalId, accountId));
    }

}
