/*
 * Author: Pedro Bilhalva Oliveira
 * Date: October 3, 2026
 * Description: Lab 2 - Represents a single bank account. Stores the account
 *              number, the holder's name and the balance, and validates
 *              every operation (opening balance, deposit and withdrawal).
 */

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Lab 2: A bank account that holds an account number, the account holder's
 * name and a balance with 2 decimal places.
 * <p>
 * Every piece of data that comes from the outside (constructor arguments and
 * transaction amounts) is validated. Invalid values raise an
 * IllegalArgumentException, while a withdrawal larger than the balance raises
 * the custom checked exception InsufficientFundsException.
 */
public class BankAccount {

	// Minimum number of digits an account number must have
	private static final int MIN_ACCOUNT_NUMBER_DIGITS = 9;

	// Account number, kept as text so leading zeros are not lost
	private final String accountNumber;
	// Name of the person or company that owns the account
	private final String accountHolderName;
	// Current balance, always stored with 2 decimal places
	private double balance;

	/**
	 * Creates a bank account and validates the information received.
	 *
	 * @param accountNumber     account number, at least 9 digits and digits only
	 * @param accountHolderName name of the holder, cannot be blank
	 * @param balance           opening balance, cannot be negative
	 * @throws IllegalArgumentException if the number, the name or the balance is invalid
	 */
	public BankAccount(String accountNumber, String accountHolderName, double balance) {
		this.accountNumber = validateAccountNumber(accountNumber);
		this.accountHolderName = validateAccountHolderName(accountHolderName);
		this.balance = validateOpeningBalance(balance);
	}

	/**
	 * Adds money to the account.
	 *
	 * @param amount amount to deposit, must be greater than zero
	 * @throws IllegalArgumentException if the amount is zero, negative or not a finite number
	 */
	public void deposit(double amount) {
		validateAmount("Deposit", amount);
		balance = round2(balance + amount);
	}

	/**
	 * Takes money out of the account. The amount is validated first, so a
	 * negative amount is reported as an invalid amount and not as insufficient
	 * funds.
	 *
	 * @param amount amount to withdraw, must be greater than zero
	 * @throws IllegalArgumentException   if the amount is zero, negative or not a finite number
	 * @throws InsufficientFundsException if the amount is larger than the available balance
	 */
	public void withdraw(double amount) throws InsufficientFundsException {
		validateAmount("Withdrawal", amount);

		// The withdrawal cannot be larger than what the account holds
		if (amount > balance) {
			throw new InsufficientFundsException(accountNumber, amount, balance);
		}
		balance = round2(balance - amount);
	}

	/**
	 * @return the account number
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @return the current balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * Checks that a transaction amount can be used. Double.isFinite is needed
	 * because Double.parseDouble accepts "NaN" and "Infinity", which would
	 * otherwise destroy the balance.
	 *
	 * @param transactionType "Deposit" or "Withdrawal", used in the error message
	 * @param amount          the amount to validate
	 * @throws IllegalArgumentException if the amount is not a finite number greater than zero
	 */
	private static void validateAmount(String transactionType, double amount) {
		if (!Double.isFinite(amount) || amount <= 0) {
			throw new IllegalArgumentException(String.format(
					"%s amount must be greater than zero (received: $%,.2f)", transactionType, amount));
		}
	}

	/**
	 * Checks that the account number has at least 9 digits and nothing else.
	 * The regular expression \d{9,} means "9 or more digits, nothing else".
	 *
	 * @param accountNumber the number to validate
	 * @return the validated account number
	 * @throws IllegalArgumentException if the number is not 9 or more digits long
	 */
	private static String validateAccountNumber(String accountNumber) {
		if (accountNumber == null || !accountNumber.matches("\\d{" + MIN_ACCOUNT_NUMBER_DIGITS + ",}")) {
			throw new IllegalArgumentException(
					"Account number must be at least " + MIN_ACCOUNT_NUMBER_DIGITS
							+ " digits and contain digits only (received: \"" + accountNumber + "\")");
		}
		return accountNumber;
	}

	/**
	 * Checks that the holder's name was filled in. isBlank() is true for an
	 * empty string and also for a string made only of spaces.
	 *
	 * @param accountHolderName the name to validate
	 * @return the validated name without leading or trailing spaces
	 * @throws IllegalArgumentException if the name is null or blank
	 */
	private static String validateAccountHolderName(String accountHolderName) {
		if (accountHolderName == null || accountHolderName.isBlank()) {
			throw new IllegalArgumentException("Account holder name cannot be blank");
		}
		return accountHolderName.trim();
	}

	/**
	 * Checks the opening balance. Negative balances are refused, and so are
	 * "NaN" and "Infinity", which are technically not negative numbers but
	 * would make the account unusable.
	 *
	 * @param balance the opening balance to validate
	 * @return the opening balance rounded to 2 decimal places
	 * @throws IllegalArgumentException if the balance is negative or not a finite number
	 */
	private static double validateOpeningBalance(double balance) {
		if (!Double.isFinite(balance) || balance < 0) {
			throw new IllegalArgumentException(
					String.format("Opening balance cannot be negative (received: $%,.2f)", balance));
		}
		return round2(balance);
	}

	/**
	 * Rounds a value to 2 decimal places using BigDecimal, which is exact.
	 * Multiplying by 100 and dividing (the usual trick) produces results such
	 * as 1250.5599999999999 instead of 1250.56.
	 *
	 * @param value the value to round
	 * @return the value rounded to 2 decimal places
	 */
	private static double round2(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * @return a one line description of the account, used when printing it
	 */
	@Override
	public String toString() {
		return String.format("Account %s | Holder: %-20s | Balance: $%,.2f",
				accountNumber, accountHolderName, balance);
	}
}