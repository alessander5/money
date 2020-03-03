package com.infy.service;

import com.infy.domain.Account;
import com.infy.domain.Transfer;
import com.infy.dto.TransferDto;

import java.util.List;
import java.util.Optional;

public interface BillingFacade {

    Account createAccount(Account account);

    Optional<Account> getAccount(long id);

    List<Account> getAllAccounts();

    Account updateAccount(Account account);

    boolean removeAccount(long id);

    Transfer createTransfer(TransferDto transferDto);

    List<Transfer> getTransfers();

    Optional<Transfer> getTransfer(long id);
}
