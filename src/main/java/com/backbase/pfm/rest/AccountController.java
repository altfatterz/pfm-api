package com.backbase.pfm.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.*;
import java.util.List;

@RestController
@Api(value = "Accounts", description = "PFM API")
@RequestMapping("/v1/pfm/accounts")
public class AccountController {

    private AccountRepository accountRepository;
    private AccountResourceAssembler accountResourceAssembler;

    @Autowired
    public AccountController(AccountRepository accountRepository, AccountResourceAssembler accountResourceAssembler) {
        this.accountRepository = accountRepository;
        this.accountResourceAssembler = accountResourceAssembler;
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


//    @ApiOperation(value = "Get goal", notes = "Get a goal of an account")
//    @RequestMapping(value = "/{accountId}/goals/{goalId}", method = RequestMethod.GET)
//    public ResponseEntity<Goal> getAccountGoal(@PathVariable Long accountId, @PathVariable Long goalId)
//            throws AccountDoesNotExistException, GoalDoesNotExistException {
//        final Account account = accountRepository.findOne(accountId);
//        if (account == null) {
//            throw new AccountDoesNotExistException(accountId);
//        }
//        Goal goal = account.getGoal(goalId);
//        if (goal == null) {
//            throw new GoalDoesNotExistException(accountId, goalId);
//        }
//        return new ResponseEntity<>(goal, HttpStatus.OK);
//    }


    @ExceptionHandler({AccountDoesNotExistException.class, GoalDoesNotExistException.class})
    public ResponseEntity<ErrorResponse> handleNotFounds(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.NOT_FOUND.toString());
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public void copyFields(Account existingAccount, Account account) {
        if (account.getName() != null) {
            existingAccount.setName(account.getName());
        }
    }

}
