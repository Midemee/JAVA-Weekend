package com.semicolon.safebank;

import com.semicolon.safebank.exceptions.InsufficientFundsException;
import com.semicolon.safebank.exceptions.InvalidAccountHolderException;
import com.semicolon.safebank.exceptions.InvalidAmountException;
import com.semicolon.safebank.exceptions.WithdrawalLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {
    private BankAccount account;

    @BeforeEach
    void setUp() throws InvalidAccountHolderException, InvalidAmountException {
        account = new BankAccount("Mide Ashiwaju", new BigDecimal("200000.00"));
    }

    @Test
    void shouldCreateAccountWithValidDetails() {
        assertEquals("Mide Ashiwaju", account.getAccountHolderName());
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("200000.00")));
        assertTrue(account.getTransactionHistory().isEmpty());
    }

    @Test
    void shouldRejectBlankAccountHolderName() {
        assertThrows(
                InvalidAccountHolderException.class,
                () -> new BankAccount("   ", new BigDecimal("1000"))
        );
    }

    @Test
    void shouldRejectNegativeOpeningBalance() {
        assertThrows(
                InvalidAmountException.class,
                () -> new BankAccount("Ada", new BigDecimal("-1"))
        );
    }

    @Test
    void shouldDepositPositiveAmountAndRecordTransaction() throws InvalidAmountException {
        account.deposit(new BigDecimal("5000"));

        assertEquals(0, account.getBalance().compareTo(new BigDecimal("205000")));
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(TransactionType.DEPOSIT,
                account.getTransactionHistory().get(0).getType());
    }

    @Test
    void shouldRejectDepositOfNegativeFiveHundredWithoutChangingAccount()
            throws InvalidAmountException {
        BigDecimal originalBalance = account.getBalance();
        int originalHistorySize = account.getTransactionHistory().size();

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(new BigDecimal("-500"))
        );

        assertEquals(0, account.getBalance().compareTo(originalBalance));
        assertEquals(originalHistorySize, account.getTransactionHistory().size());
    }

    @Test
    void shouldRejectZeroDeposit() {
        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(BigDecimal.ZERO)
        );
    }

    @Test
    void shouldRejectWithdrawalOfOneHundredTwentyThousandFromTwoHundredThousand()
            throws InvalidAmountException {
        BigDecimal originalBalance = account.getBalance();

        assertThrows(
                WithdrawalLimitExceededException.class,
                () -> account.withdraw(new BigDecimal("120000"))
        );

        assertEquals(0, account.getBalance().compareTo(originalBalance));
        assertTrue(account.getTransactionHistory().isEmpty());
    }

    @Test
    void shouldAllowWithdrawalAtTheSingleWithdrawalLimit()
            throws InvalidAmountException, InsufficientFundsException,
            WithdrawalLimitExceededException {
        account.withdraw(new BigDecimal("100000"));

        assertEquals(0, account.getBalance().compareTo(new BigDecimal("100000")));
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(TransactionType.WITHDRAWAL,
                account.getTransactionHistory().get(0).getType());
    }

    @Test
    void shouldAllowSuccessfulDepositImmediatelyAfterFailedDeposit()
            throws InvalidAmountException {
        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(new BigDecimal("-500"))
        );

        assertDoesNotThrow(() -> account.deposit(new BigDecimal("500")));

        assertEquals(0, account.getBalance().compareTo(new BigDecimal("200500")));
        assertEquals(1, account.getTransactionHistory().size());
    }

    @Test
    void shouldNotAllowCallersToModifyTransactionHistory()
            throws InvalidAmountException {
        account.deposit(new BigDecimal("100"));

        List<Transaction> history = account.getTransactionHistory();
        assertThrows(
                UnsupportedOperationException.class,
                () -> history.clear()
        );
        assertEquals(1, account.getTransactionHistory().size());
    }
}
