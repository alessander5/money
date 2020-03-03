package com.infy.service;

import com.google.inject.persist.Transactional;
import com.infy.dto.TransferDto;
import com.infy.repo.CrudRepository;
import com.infy.domain.Account;
import com.infy.domain.Transfer;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
public class BillingService implements BillingFacade {

    private CrudRepository<Account> accountRepository;
    private CrudRepository<Transfer> transferRepository;

    @Inject
    public BillingService(final Provider<EntityManager> entityManagerProvider) {
        this.accountRepository = new CrudRepository<Account>() {
                @Override
                public Class<Account> getEntityClass() {
                    return Account.class;
                }

                @Override
                public Provider<EntityManager> getEntityManagerProvider() {
                    return entityManagerProvider;
                }
            };
        this.transferRepository = new CrudRepository<Transfer>() {
            @Override
            public Class<Transfer> getEntityClass() {
                return Transfer.class;
            }

            @Override
            public Provider<EntityManager> getEntityManagerProvider() {
                return entityManagerProvider;
            }
        };
    }

    @Override
    public Account createAccount(Account account) {
        return accountRepository.create(account);
    }

    @Override
    public Optional<Account> getAccount(long id) {
        return accountRepository.getById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.getAll();
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.update(account);
    }

    @Override
    public boolean removeAccount(long accountId) {
        return accountRepository.remove(accountId);
    }

    @Override
    public Transfer createTransfer(TransferDto transferDto) {
        Account consumer = accountRepository.getById(transferDto.getConsumerId())
            .orElseThrow(() -> new IllegalArgumentException("Could not found consumer by id: " + transferDto.getConsumerId()));
        Account supplier = accountRepository.getById(transferDto.getSupplierId())
            .orElseThrow(() -> new IllegalArgumentException("Could not found supplier by id: " + transferDto.getSupplierId()));
        accountRepository.update(supplier.setBalance(supplier.getBalance() - transferDto.getAmount()));
        accountRepository.update(consumer.setBalance(consumer.getBalance() + transferDto.getAmount()));
        return transferRepository.create(new Transfer()
            .setName(supplier.getName() + " to " + consumer.getName())
            .setCreated(new Date())
            .setAmount(transferDto.getAmount())
            .setConsumerId(consumer.getId())
            .setSupplierId(supplier.getId()));
    }

    @Override
    public List<Transfer> getTransfers() {
        return transferRepository.getAll();
    }

    @Override
    public Optional<Transfer> getTransfer(long id) {
        return transferRepository.getById(id);
    }
}
