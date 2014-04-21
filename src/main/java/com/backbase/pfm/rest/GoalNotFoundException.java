package com.backbase.pfm.rest;

public class GoalNotFoundException extends Exception {

    private static final String MESSAGE_FORMAT = "Goal '%s' does not exist";

    public GoalNotFoundException(String goalId) {
        super(String.format(MESSAGE_FORMAT, goalId));
    }

}
