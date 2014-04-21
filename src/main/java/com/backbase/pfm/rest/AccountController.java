package com.backbase.pfm.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.*;
import java.net.URI;
import java.util.List;

@RestController
@Api(value = "Accounts", description = "PFM API")
@RequestMapping("/v1/pfm/accounts")
public class AccountController {

    private AccountRepository accountRepository;
    private AccountResourceAssembler accountResourceAssembler;
    private GoalRepository goalRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository, AccountResourceAssembler accountResourceAssembler, GoalRepository goalRepository) {
        this.accountRepository = accountRepository;
        this.accountResourceAssembler = accountResourceAssembler;
        this.goalRepository = goalRepository;
    }

    @ApiOperation(value = "Get accounts", notes = "Returns accounts")
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @ApiOperation(value = "Get account", notes = "Returns an account by id")
    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<Account>> getAccount(@PathVariable String accountId) throws AccountDoesNotExistException {
        final Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        Resource<Account> resource = accountResourceAssembler.toResource(account);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    // TODO check why swagger has problems with using PATCH
    // ISSUE: https://github.com/martypitt/swagger-springmvc/issues/249
    @ApiOperation(value = "Update an account", notes = "Update an account")
    @RequestMapping(value = "/{accountId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<Account>> updateAccount(@PathVariable String accountId, @RequestBody Account account) throws AccountDoesNotExistException {
        final Account existingAccount = accountRepository.findOne(accountId);
        if (existingAccount == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        copyFields(existingAccount, account);
        accountRepository.save(existingAccount);

        Resource<Account> resource = accountResourceAssembler.toResource(existingAccount);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @ApiOperation(value = "Get goals", notes = "Get goals of an account")
    @RequestMapping(value = "/{accountId}/goals", method = RequestMethod.GET)
    public ResponseEntity<List<Goal>> getAccountGoals(@PathVariable String accountId) throws AccountDoesNotExistException {
        final Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        return new ResponseEntity<>(account.getGoals(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get goal", notes = "Get a goal of an account")
    @RequestMapping(value = "/{accountId}/goals/{goalId}", method = RequestMethod.GET)
    public ResponseEntity<Goal> getAccountGoal(@PathVariable String accountId, @PathVariable String goalId)
            throws AccountDoesNotExistException, GoalDoesNotExistException {
        final Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        Goal goal = account.getGoal(goalId);
        if (goal == null) {
            throw new GoalDoesNotExistException(accountId, goalId);
        }
        return new ResponseEntity<>(goal, HttpStatus.OK);
    }

    @ApiOperation(value = "Create goal", notes = "Create a goal to an account")
    @RequestMapping(value = "/{accountId}/goals", method = RequestMethod.POST)
    public ResponseEntity<Void> createGoal(@PathVariable String accountId, @Valid @RequestBody Goal goal)
            throws AccountDoesNotExistException {
        Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        goal.setAccount(account);
        Goal savedGoal = goalRepository.save(goal);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:8080/v1/pfm/accounts/" + accountId + "/goals/" + savedGoal.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete goal", notes = "Create a goal to an account")
    @RequestMapping(value = "/{accountId}/goals/{goalId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGoal(@PathVariable String accountId, @PathVariable String goalId) throws GoalNotFoundException {
        try {
            goalRepository.delete(goalId);
        } catch (EmptyResultDataAccessException e) {
            throw new GoalNotFoundException(goalId);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Update goal", notes = "Replace a goal with another one")
    @RequestMapping(value = "/{accountId}/goals/{goalId}", method = RequestMethod.PUT)
    public ResponseEntity<Goal> updateGoal(@PathVariable String accountId,
                                           @PathVariable String goalId,
                                           @RequestBody @Valid Goal updatedGoal)
            throws GoalNotFoundException, GoalDoesNotExistException, AccountDoesNotExistException {

        Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        Goal goal = account.getGoal(goalId);
        if (goal == null) {
            throw new GoalDoesNotExistException(accountId, goalId);
        }
        goal.setName(updatedGoal.getName());
        goal.setAmount(updatedGoal.getAmount());
        goalRepository.save(goal);

        return new ResponseEntity<>(goal, HttpStatus.OK);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleError(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.BAD_REQUEST.toString());
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
                errorResponse.setMessage(sb.toString());
            }
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //TODO: com.fasterxml.jackson.databind.exc.InvalidFormatException: Can not construct instance of java.math.BigDecimal from String value 'BigDecimal': not a valid representation

    @ExceptionHandler({AccountDoesNotExistException.class, GoalDoesNotExistException.class})
    public ResponseEntity<ErrorResponse> handleNotFounds(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.NOT_FOUND.toString());
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.BAD_REQUEST.toString());
        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof JsonParseException) {
                errorResponse.setMessage("invalid JSON payload");
            }
            System.out.println(cause);
        } else {
            errorResponse.setMessage("bad request");
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public void copyFields(Account existingAccount, Account account) {
        if (account.getName() != null) {
            existingAccount.setName(account.getName());
        }
    }

}
