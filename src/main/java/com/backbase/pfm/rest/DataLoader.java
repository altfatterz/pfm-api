package com.backbase.pfm.rest;

import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public class DataLoader {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void loadData() {

        Account account = new Account();
        account.setName("Family savings account");
        account.setType(AccountType.SAVINGS);
        account.setBalance(new BigDecimal(20000));
        accountRepository.save(account);

        Goal goal = new Goal();
        goal.setName("Vacation");
        goal.setAccount(account);
        goalRepository.save(goal);

        goal = new Goal();
        goal.setName("New house");
        goal.setAccount(account);
        goalRepository.save(goal);


        account = new Account();
        account.setName("Checking account");
        account.setType(AccountType.CHECKING);
        account.setBalance(new BigDecimal(3000));
        accountRepository.save(account);

        goal = new Goal();
        goal.setName("Groceries");
        goal.setAmount(new BigDecimal(150));
        goal.setAccount(account);
        goalRepository.save(goal);

        goal = new Goal();
        goal.setName("Monthly Bills");
        goal.setAmount(new BigDecimal(550));
        goal.setAccount(account);
        goalRepository.save(goal);

    }

}
