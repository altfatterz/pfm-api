package com.backbase.pfm.goal;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
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
            throw new GoalNotFoundException(id);
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
     * CREATE GOAL
     */
    // by default curl sends request with "application/x-www-form-urlencoded" Content-Type
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"name":"New Car","amount":1000}'
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"name":"New Car","amount":-1000}'
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"name":"","amount":1000}'
    // curl -i -H "Content-Type:application/json" -X POST localhost:8080/v1/pfm/goals -d '{"amount":1000}'
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> createGoal(@Valid @RequestBody Goal goal) {
        Goal createdGoal = goalRepository.save(goal);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:8080/v1/pfm/goals/" + createdGoal.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleTypeMismatchException(Exception e) {
        Error error = new Error();
        error.setCode(HttpStatus.BAD_REQUEST.toString());
        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof JsonParseException) {
                error.setMessage("invalid JSON payload");
            }
        } else {
            error.setMessage("bad request");
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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

    // curl -i -v -X DELETE localhost:8080/v1/pfm/goals/5  -> error
    // curl -i -v -X DELETE localhost:8080/v1/pfm/goals/1  -> 200 OK
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) throws GoalNotFoundException {
        try {
            goalRepository.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new GoalNotFoundException(id);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
