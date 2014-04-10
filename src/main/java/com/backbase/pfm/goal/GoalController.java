package com.backbase.pfm.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/pfm/goals")
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
    public ResponseEntity<Goal> getGoal(@PathVariable Long id) {
        Goal goal = goalRepository.findOne(id);
        if (goal == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(goal, HttpStatus.OK);
    }

    // curl -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/customers -d '{"name":"New Car","amount":1000}'
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CreateGoalResponse> createGoal(@Valid @RequestBody Goal goal) {
        Goal savedGoal = goalRepository.save(goal);
        CreateGoalResponse response = new CreateGoalResponse(savedGoal.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // TODO: does not work yet
    // curl -H "Content-Type:application/json" -X PUT localhost:8080/v1/pfm/customers/4 -d '{"name":"Vacation","amount":500}'
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateGoal(@PathVariable Long id, @RequestBody Goal goal) {
        System.out.println(goal.getName() + " : " + goal.getAmount() + " : " + goal.isNew());
        goalRepository.save(goal);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Goal> deleteGoal(@PathVariable Long id) {
        try {
            goalRepository.delete(id);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
