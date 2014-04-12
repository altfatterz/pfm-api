package com.backbase.pfm.goal;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<Goal> getGoal(@PathVariable Long id) throws GoalNotFoundException {
        Goal goal = goalRepository.findOne(id);
        if (goal == null) {
            throw new GoalNotFoundException("Goal not found with id " + id);
        }
        return new ResponseEntity<>(goal, HttpStatus.OK);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Error> handleTypeMismatchException(TypeMismatchException e) {
        Error error = new Error();
        error.setCode(HttpStatus.BAD_REQUEST.toString());
        error.setMessage(" ID should be numeric. Could not convert \"" + e.getValue() + "\" into a numeric.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<Error> handleGoalNotFoundException(GoalNotFoundException e) {
        Error error = new Error();
        error.setCode(HttpStatus.NOT_FOUND.toString());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * CREATE GOAL *
     */
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"name":"New Car","amount":1000}'
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"name":"New Car","amount":-1000}'
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"name":"","amount":1000}'
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"amount":1000}'
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CreateGoalResponse> createGoal(@Valid @RequestBody Goal goal) {
        Goal savedGoal = goalRepository.save(goal);
        CreateGoalResponse response = new CreateGoalResponse(savedGoal.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleError(MethodArgumentNotValidException e) {
        Error error = new Error();
        error.setCode(HttpStatus.BAD_REQUEST.toString());
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult != null) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(fieldError.getField());
                sb.append(" field [");
                sb.append(fieldError.getRejectedValue());
                sb.append("] value is rejected, ");
                sb.append(fieldError.getDefaultMessage());
                error.setMessage(sb.toString());
            }
        }
       return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    // TODO: does not work yet
    // curl -H "Content-Type:application/json" -X PUT localhost:8080/v1/pfm/goals/4 -d '{"name":"Vacation","amount":500}'
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
