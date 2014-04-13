package com.backbase.pfm.goal;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
final class AccountResourceAssembler implements ResourceAssembler<Account, Resource<Account>> {

    @Override
    public Resource<Account> toResource(Account account) {
        Resource<Account> resource = new Resource<>(account);
        resource.add(linkTo(PFMController.class).slash(account.getId()).slash("goals").withRel("goals"));
        return resource;
    }
}
