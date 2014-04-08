package com.backbase.pfm.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/pfm/customers")
public class GoalController {

    private GoalRepository goalRepository;

    @Autowired
    public GoalController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @RequestMapping
    public List<Goal> getGoals() {
        return goalRepository.findAll();
    }

    @RequestMapping("/{id}")
    public Goal getGoal(@PathVariable Long id) {
        return goalRepository.findOne(id);
    }

}
