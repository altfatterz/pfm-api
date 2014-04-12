package com.backbase.pfm.goal;

public class GoalNotFoundException extends Exception {

    private static final String MESSAGE_FORMAT = "Goal '%d' does not exist";

    public GoalNotFoundException(Long goalId) {
        super(String.format(MESSAGE_FORMAT, goalId));
    }

}
