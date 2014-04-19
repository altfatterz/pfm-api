package com.backbase.pfm.rest;

public class GoalDoesNotExistException extends Exception {

    private static final String MESSAGE_FORMAT = "Goal '%d' for account '%d' does not exist";

    public GoalDoesNotExistException(Long accountId, Long goalId) {
        super(String.format(MESSAGE_FORMAT, goalId, accountId));
    }

}
