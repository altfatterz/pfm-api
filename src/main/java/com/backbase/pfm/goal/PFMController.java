package com.backbase.pfm.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pfm/accounts")
public class PFMController {

    private AccountRepository accountRepository;
    private AccountResourceAssembler accountResourceAssembler;

    @Autowired
    public PFMController(AccountRepository accountRepository, AccountResourceAssembler accountResourceAssembler) {
        this.accountRepository = accountRepository;
        this.accountResourceAssembler = accountResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    public ResponseEntity<Resource<Account>> getAccount(@PathVariable Long accountId) throws AccountDoesNotExistException {
        final Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        Resource<Account> resource = accountResourceAssembler.toResource(account);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(value = "/{accountId}/goals", method = RequestMethod.GET)
    public ResponseEntity<List<Goal>> getAccountGoals(@PathVariable Long accountId) throws AccountDoesNotExistException {
        final Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new AccountDoesNotExistException(accountId);
        }
        return new ResponseEntity<>(account.getGoals(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{accountId}/goals/{goalId}", method = RequestMethod.GET)
    public ResponseEntity<Goal> getAccountGoal(@PathVariable Long accountId, @PathVariable Long goalId)
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


    @ExceptionHandler({AccountDoesNotExistException.class, GoalDoesNotExistException.class})
    public ResponseEntity<Error> handleNotFounds(Exception e) {
        Error error = new Error();
        error.setCode(HttpStatus.NOT_FOUND.toString());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


}
