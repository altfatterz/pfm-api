package com.backbase.pfm;

import com.backbase.pfm.goal.Goal;
import com.backbase.pfm.goal.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class DataLoader {

    @Autowired
    private GoalRepository goalRepository;

    @Transactional
    public void loadData() {

        Goal goal = new Goal();
        goal.setName("Vacation");
        goalRepository.save(goal);

        goal = new Goal();
        goal.setName("Groceries");
        goal.setAmount(150);
        goalRepository.save(goal);

        goal = new Goal();
        goal.setName("Monthly Bills");
        goal.setAmount(550);
        goalRepository.save(goal);

    }

}
