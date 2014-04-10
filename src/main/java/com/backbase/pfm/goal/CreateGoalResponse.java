package com.backbase.pfm.goal;

public class CreateGoalResponse {

    private Long id;

    public CreateGoalResponse() {
    }

    public CreateGoalResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
