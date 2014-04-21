package com.backbase.pfm.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "accounts")
@JsonIgnoreProperties("new")
public class Account extends AbstractPersistable<String> {

    private String name;
    private AccountType type;
    private BigDecimal balance;
    private BigDecimal safeToSpend;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Goal> goals = Collections.emptyList();

    public void addGoal(Goal goal) {
        if (goals == Collections.EMPTY_LIST) {
            goals = new ArrayList<>();
        }
        goals.add(goal);
        calcSafeToSpend();
    }

    public Goal getGoal(String id) {
        for (Goal goal : goals) {
            if (goal.getId().equals(id)) {
                return goal;
            }
        }
        return null;
    }

    private void calcSafeToSpend() {
        for (Goal goal : goals) {
            safeToSpend = safeToSpend.subtract(goal.getAmount());
        }
    }

    public BigDecimal getSafeToSpend() {
        return safeToSpend;
    }

    @JsonIgnore
    public List<Goal> getGoals() {
        return goals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
        this.safeToSpend = balance;
    }
}
