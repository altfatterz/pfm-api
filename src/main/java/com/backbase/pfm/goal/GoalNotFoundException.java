package com.backbase.pfm.goal;

public class GoalNotFoundException extends Exception {

    private String message;

    public GoalNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
