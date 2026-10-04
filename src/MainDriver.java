/*
 * Author: Pedro Bilhalva Oliveira
 * Date: October 3, 2026
 * Description: Lab 2 - Driver of the banking application. Creates three bank
 *              accounts, performs deposits and withdrawals read from the
 *              console and handles every exception so the program never
 *              stops running.
 */

/**
 * Lab 2: Driver of the banking system.
 * <p>
 * It shows the flow required by the lab: create at least three BankAccount
 * objects, run deposits and withdrawals on each one, and handle invalid input
 * and banking errors with try, catch and finally so the program keeps going
 * after every mistake.
 */
// The lab requires at least three bank account objects
private static final int ACCOUNT_COUNT = 3;

/**
 * Entry point of the application.
 */
void main() {
    Scanner scanner = new Scanner(System.in);

    IO.println("==============================================");
    IO.println("       Pedro Bilhalva - COMP228 Lab 2");
    IO.println("   Java Exception Handling - Banking System");
    IO.println("==============================================");

    // Step 1: create the three accounts before any transaction
    BankAccount[] accounts = createAccounts(scanner);

    // Step 2: deposits and withdrawals on each account
    for (BankAccount account : accounts) {
        IO.println();
        IO.println("--- Transactions for " + account + " ---");
        runTransactions(scanner, account);
    }

    // Step 3: show both exception types being caught on purpose
    demonstrateExceptions(accounts[0]);

    // Step 4: print the balance of every account and the total
    printSummary(accounts);

    scanner.close();
    IO.println();
    IO.println("Program ended normally. Goodbye!");
}

/**
 * Creates the array of bank accounts by calling createAccount once per
 * position.
 *
 * @param scanner source of the user's input
 * @return an array with ACCOUNT_COUNT bank accounts
 */
private static BankAccount[] createAccounts(Scanner scanner) {
    BankAccount[] accounts = new BankAccount[ACCOUNT_COUNT];

    IO.println();
    IO.println("Please create " + ACCOUNT_COUNT + " bank accounts.");
    for (int i = 0; i < accounts.length; i++) {
        IO.println();
        IO.println("Account " + (i + 1) + " of " + ACCOUNT_COUNT + ":");
        accounts[i] = createAccount(scanner);
    }
    return accounts;
}

/**
 * Reads the information of one account and builds it. The constructor
 * validates the data, so the loop repeats until the user types valid
 * information instead of ending the program.
 *
 * @param scanner source of the user's input
 * @return a validated bank account
 */
private static BankAccount createAccount(Scanner scanner) {
    while (true) {
        String accountNumber = readLine(scanner, "Enter account number (at least 9 digits): ");
        String accountHolderName = readLine(scanner, "Enter account holder name: ");
        double openingBalance = readAmount(scanner, "Enter opening balance: ");

        try {
            BankAccount account = new BankAccount(accountNumber, accountHolderName, openingBalance);
            IO.println("Account created -> " + account);
            return account;
        } catch (IllegalArgumentException e) {
            // Bad account information: show why and ask for it again
            IO.println("ERROR: " + e.getMessage());
            IO.println("Please re-enter the account information.");
        }
    }
}

/**
 * Reads transactions for one account until the user chooses to quit. Two
 * flags remember what was already done, because the lab requires at least
 * one successful deposit and one successful withdrawal per account.
 *
 * @param scanner  source of the user's input
 * @param account  the account being transacted
 */
private static void runTransactions(Scanner scanner, BankAccount account) {
    boolean hasDeposit = false;
    boolean hasWithdrawal = false;

    while (true) {
        char type = readTransactionType(scanner);
        if (type == 'Q') {
            // The account can only be left after both transaction types
            if (!hasDeposit || !hasWithdrawal) {
                IO.println("ERROR: this account still needs at least one successful deposit"
                        + " and one successful withdrawal before you can quit.");
                continue;
            }
            IO.println("No more transactions for account " + account.getAccountNumber() + ".");
            return;
        }
        double amount = readAmount(scanner, "Enter amount: ");

        // Only a transaction that went through counts for the requirement
        if (processTransaction(account, type, amount)) {
            if (type == 'D') {
                hasDeposit = true;
            } else {
                hasWithdrawal = true;
            }
        }
    }
}

/**
 * Asks for the type of transaction and accepts d, w or q in any case.
 *
 * @param scanner source of the user's input
 * @return 'D' for deposit, 'W' for withdrawal or 'Q' to quit
 */
private static char readTransactionType(Scanner scanner) {
    while (true) {
        String input = readLine(scanner, "Transaction type - (D)eposit, (W)ithdrawal, (Q)uit: ");
        char type = Character.toUpperCase(input.charAt(0));
        if (type == 'D' || type == 'W' || type == 'Q') {
            return type;
        }
        IO.println("ERROR: invalid transaction type \"" + input + "\". Enter D, W or Q.");
    }
}

/**
 * Runs one transaction. This method holds the try, catch and finally block
 * required by the lab: the error is reported, the program continues, and
 * the finally block always states that the transaction was processed.
 *
 * @param account the account being transacted
 * @param type    'D' for deposit or 'W' for withdrawal
 * @param amount  the amount of the transaction
 * @return true if the transaction was completed, false if it was rejected
 */
private static boolean processTransaction(BankAccount account, char type, double amount) {
    String transactionType = type == 'D' ? "Deposit" : "Withdrawal";
    try {
        if (type == 'D') {
            account.deposit(amount);
        } else {
            // withdraw declares the custom exception, so it has to be handled here
            account.withdraw(amount);
        }
        IO.println("SUCCESS: " + transactionType.toLowerCase() + " of " + money(amount) + " completed.");
        return true;
    } catch (IllegalArgumentException | InsufficientFundsException e) {
        // Multi-catch: one block handles the invalid amount and the insufficient funds
        IO.println("ERROR: " + e.getMessage());
        return false;
    } finally {
        // Runs whether the transaction succeeded or failed
        IO.println("Transaction processed. New balance: " + money(account.getBalance()));
    }
}

/**
 * Causes two errors on purpose, one of each type, to show that the custom
 * exception and the validation error are both caught and that the program
 * keeps running afterwards.
 *
 * @param account the account used to trigger the errors
 */
private static void demonstrateExceptions(BankAccount account) {
    IO.println();
    IO.println("--- Exception handling demonstration (intentional errors) ---");
    IO.println("Account used: " + account);

    // A negative deposit is refused by the validation of the amount
    try {
        account.deposit(-25.00);
    } catch (IllegalArgumentException e) {
        IO.println("Caught IllegalArgumentException -> " + e.getMessage());
    } finally {
        IO.println("Transaction processed. Balance unchanged: " + money(account.getBalance()));
    }

    // A withdrawal larger than the balance raises the custom exception
    try {
        account.withdraw(999999.99);
    } catch (InsufficientFundsException e) {
        IO.println("Caught InsufficientFundsException -> " + e.getMessage());
        IO.println("   Account: " + e.getAccountNumber()
                + " | Requested: " + money(e.getRequestedAmount())
                + " | Available: " + money(e.getAvailableBalance()));
    } finally {
        IO.println("Transaction processed. Balance unchanged: " + money(account.getBalance()));
    }

    IO.println("The program kept running after both exceptions were handled.");
}

/**
 * Prints every account and the sum of all the balances.
 *
 * @param accounts the accounts of the application
 */
private static void printSummary(BankAccount[] accounts) {
    IO.println();
    IO.println("==============================================");
    IO.println("            FINAL ACCOUNT SUMMARY");
    IO.println("==============================================");
    Arrays.stream(accounts)
            .forEach(account -> IO.println("  " + account));
    double total = Arrays.stream(accounts)
            .mapToDouble(BankAccount::getBalance)
            .sum();
    IO.println("----------------------------------------------");
    System.out.printf("  Total balance across %d accounts: $%,.2f%n", accounts.length, total);
    IO.println("==============================================");
}

/**
 * Reads a line of text and asks again while the user types nothing.
 *
 * @param scanner source of the user's input
 * @param prompt  message shown before reading
 * @return the line typed by the user, without surrounding spaces
 */
private static String readLine(Scanner scanner, String prompt) {
    while (true) {
        IO.print(prompt);

        // hasNextLine is checked first, otherwise nextLine throws
        // NoSuchElementException when the input ends
        if (!scanner.hasNextLine()) {
            endOfInput();
        }
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            return input;
        }
        IO.println("ERROR: this value cannot be blank. Please try again.");
    }
}

/**
 * Reads an amount and asks again while the text is not a number.
 * <p>
 * The line is read with nextLine and converted with Double.parseDouble,
 * which throws NumberFormatException. Reading the amount with nextDouble
 * instead would mix nextLine with the typed reads and silently swallow the
 * next answer typed by the user.
 *
 * @param scanner source of the user's input
 * @param prompt  message shown before reading
 * @return the amount typed by the user
 */
private static double readAmount(Scanner scanner, String prompt) {
    while (true) {
        IO.print(prompt);
        if (!scanner.hasNextLine()) {
            endOfInput();
        }
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            IO.println("ERROR: \"" + input + "\" is not a valid number. Please try again.");
        }
    }
}

/**
 * Ends the program in an orderly way when there is no more input to read,
 * for example when the output of the program was redirected from a file.
 */
private static void endOfInput() {
    IO.println();
    IO.println("End of input reached. Closing the program.");
    System.exit(0);
}

/**
 * Formats an amount as money with thousands separators and 2 decimals.
 *
 * @param value the amount to format
 * @return the amount formatted, for example $1,250.56
 */
private static String money(double value) {
    return String.format("$%,.2f", value);
}