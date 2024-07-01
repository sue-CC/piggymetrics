package com.piggymetrics.account.grpc.client;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;

import java.security.Principal;

public interface AccountClient {
   Account getAccountByName(String accountName);
   Account getCurrentAccount(Principal principal);
   String saveCurrentAccount(Principal principal, Account account);
   Account createNewAccount(User user);
}
