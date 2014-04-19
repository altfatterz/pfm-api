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

        Goal goal = new Goal();
        goal.setName("Vacation");
        account.addGoal(goal);

        goal = new Goal();
        goal.setName("New house");
        account.addGoal(goal);

        accountRepository.save(account);

        account = new Account();
        account.setName("Checking account");
        account.setType(AccountType.CHECKING);
        account.setBalance(new BigDecimal(3000));

        goal = new Goal();
        goal.setName("Groceries");
        goal.setAmount(new BigDecimal(150));
        account.addGoal(goal);

        goal = new Goal();
        goal.setName("Monthly Bills");
        goal.setAmount(new BigDecimal(550));
        account.addGoal(goal);

        accountRepository.save(account);

    }

}
