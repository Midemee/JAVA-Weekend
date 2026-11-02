package com.semicolon.safebank;

import com.semicolon.safebank.exceptions.InsufficientFundsException;
import com.semicolon.safebank.exceptions.InvalidAccountHolderException;
import com.semicolon.safebank.exceptions.InvalidAmountException;
import com.semicolon.safebank.exceptions.WithdrawalLimitExceededException;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

public class SafeBankApplication {

    public static void main(String[] args) {
        SafeBankApplication application = new SafeBankApplication();
        application.run(new Scanner(System.in), System.out);
    }

    public void run(Scanner scanner, PrintStream output) {
        output.println("==================================");
        output.println("       WELCOME TO SAFEBANK");
        output.println("==================================");

        BankAccount account = createAccount(scanner, output);
        if (account == null) {
            output.println("Application closed.");
            return;
        }

        output.println("\nWelcome, " + account.getAccountHolderName() + "!");

        boolean running = true;
        while (running && scanner.hasNextLine()) {
            displayMenu(output);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleDeposit(account, scanner, output);
                case "2" -> handleWithdrawal(account, scanner, output);
                case "3" -> displayAccountDetails(account, output);
                case "4" -> {
                    output.println("Thank you for banking with SafeBank. Goodbye!");
                    running = false;
                }
                default -> output.println(
                        "Invalid menu selection. Please choose an option from 1 to 4."
                );
            }
        }
    }

    private BankAccount createAccount(Scanner scanner, PrintStream output) {
        while (true) {
            output.print("Enter account holder's name: ");
            if (!scanner.hasNextLine()) {
                return null;
            }

            String name = scanner.nextLine();

            output.print("Enter opening balance (₦): ");
            BigDecimal openingBalance = readAmount(scanner, output);
            if (openingBalance == null) {
                return null;
            }

            try {
                return new BankAccount(name, openingBalance);
            } catch (InvalidAccountHolderException | InvalidAmountException exception) {
                output.println("Unable to create account: " + exception.getMessage());
                output.println("Please enter the account details again.\n");
            }
        }
    }

    private BigDecimal readAmount(Scanner scanner, PrintStream output) {
        while (true) {
            if (!scanner.hasNextLine()) {
                return null;
            }

            String input = scanner.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException exception) {
                output.println("Invalid amount. Please enter a numeric value, for example 5000.");
                output.print("Enter amount (₦): ");
            }
        }
    }

    private void displayMenu(PrintStream output) {
        output.println("\n========== SAFEBANK MENU ==========");
        output.println("1. Deposit money");
        output.println("2. Withdraw money");
        output.println("3. View balance and transaction history");
        output.println("4. Exit");
        output.print("Select an option: ");
    }

    private void handleDeposit(BankAccount account, Scanner scanner, PrintStream output) {
        output.print("Enter deposit amount (₦): ");
        BigDecimal amount = readAmount(scanner, output);
        if (amount == null) {
            return;
        }

        try {
            account.deposit(amount);
            output.println("Deposit successful. New balance: "
                    + MoneyFormatter.format(account.getBalance()));
        } catch (InvalidAmountException exception) {
            output.println("Deposit rejected: " + exception.getMessage());
        }
    }

    private void handleWithdrawal(BankAccount account, Scanner scanner, PrintStream output) {
        output.print("Enter withdrawal amount (₦): ");
        BigDecimal amount = readAmount(scanner, output);
        if (amount == null) {
            return;
        }

        try {
            account.withdraw(amount);
            output.println("Withdrawal successful. New balance: "
                    + MoneyFormatter.format(account.getBalance()));
        } catch (InvalidAmountException | InsufficientFundsException
                 | WithdrawalLimitExceededException exception) {
            output.println("Withdrawal rejected: " + exception.getMessage());
        }
    }

    private void displayAccountDetails(BankAccount account, PrintStream output) {
        output.println("\n========== ACCOUNT DETAILS ==========");
        output.println("Account holder: " + account.getAccountHolderName());
        output.println("Current balance: " + MoneyFormatter.format(account.getBalance()));
        output.println("\nSuccessful transaction history:");

        if (account.getTransactionHistory().isEmpty()) {
            output.println("No successful transactions yet.");
            return;
        }

        for (Transaction transaction : account.getTransactionHistory()) {
            output.println(transaction);
        }
    }
}
