package com.semicolon.safebank;

import com.semicolon.safebank.exceptions.InsufficientFundsException;
import com.semicolon.safebank.exceptions.InvalidAccountHolderException;
import com.semicolon.safebank.exceptions.InvalidAmountException;
import com.semicolon.safebank.exceptions.WithdrawalLimitExceededException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankAccount {
    public static final BigDecimal MAX_SINGLE_WITHDRAWAL =
            new BigDecimal("100000.00");

    private final String accountHolderName;
    private BigDecimal balance;
    private final List<Transaction> transactionHistory;

    public BankAccount(String accountHolderName, BigDecimal openingBalance)
            throws InvalidAccountHolderException, InvalidAmountException {

        if (accountHolderName == null || accountHolderName.isBlank()) {
            throw new InvalidAccountHolderException(
                    "Account holder name cannot be blank."
            );
        }

        if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException(
                    "Opening balance cannot be negative."
            );
        }

        this.accountHolderName = accountHolderName.trim();
        this.balance = openingBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public void deposit(BigDecimal amount) throws InvalidAmountException {
        validatePositiveAmount(amount, "Deposit");

        balance = balance.add(amount);
        transactionHistory.add(
                new Transaction(TransactionType.DEPOSIT, amount, balance)
        );
    }

    public void withdraw(BigDecimal amount)
            throws InvalidAmountException, InsufficientFundsException,
            WithdrawalLimitExceededException {

        validatePositiveAmount(amount, "Withdrawal");

        if (amount.compareTo(MAX_SINGLE_WITHDRAWAL) > 0) {
            throw new WithdrawalLimitExceededException(
                    "A single withdrawal cannot exceed "
                            + MoneyFormatter.format(MAX_SINGLE_WITHDRAWAL) + "."
            );
        }

        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Your available balance is "
                            + MoneyFormatter.format(balance) + "."
            );
        }

        balance = balance.subtract(amount);
        transactionHistory.add(
                new Transaction(TransactionType.WITHDRAWAL, amount, balance)
        );
    }

    private void validatePositiveAmount(BigDecimal amount, String operation)
            throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    operation + " amount must be greater than zero."
            );
        }
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
}
