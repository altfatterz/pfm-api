package com.backbase.pfm.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pfm/customers")
public class GoalController {

    private GoalRepository goalRepository;

    @Autowired
    public GoalController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Goal> getGoals() {
        return goalRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Goal getGoal(@PathVariable Long id) {
        return goalRepository.findOne(id);
    }


    // curl -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/customers -d '{"name":"New Car","amount":1000}'
    @RequestMapping(method = RequestMethod.POST)
    public void createGoal(@RequestBody Goal goal) {
        goalRepository.save(goal);
    }

    // TODO: does not work yet
    // curl -H "Content-Type:application/json" -X PUT localhost:8080/v1/pfm/customers/4 -d '{"name":"Vacation","amount":500}'
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateGoal(@RequestBody Goal goal) {
        System.out.println(goal.getName() + " : " + goal.getAmount());
        goalRepository.save(goal);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteGoal(@PathVariable Long id) {
        goalRepository.delete(id);
    }




}
