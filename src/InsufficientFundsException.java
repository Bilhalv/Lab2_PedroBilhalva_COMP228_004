/*
 * Author: Pedro Bilhalva Oliveira
 * Date: October 3, 2026
 * Description: Lab 2 - Custom checked exception thrown when a withdrawal is
 *              larger than the balance available in the account. It keeps
 *              the account number and both amounts so the driver can print a
 *              detailed message.
 */

/**
 * Lab 2: Exception raised by {@link BankAccount#withdraw(double)} when the
 * requested amount is larger than the current balance.
 * <p>
 * It extends Exception (and not RuntimeException) on purpose, so it is a
 * checked exception: the caller has to catch it or declare it with throws.
 */
public class InsufficientFundsException extends Exception {

	private static final long serialVersionUID = 1L;

	// Account the withdrawal was attempted on
	private final String accountNumber;
	// Amount the user asked to withdraw
	private final double requestedAmount;
	// Amount actually available in the account
	private final double availableBalance;

	/**
	 * Builds the exception and formats the message shown to the user.
	 *
	 * @param accountNumber     account the withdrawal was attempted on
	 * @param requestedAmount   amount the user asked to withdraw
	 * @param availableBalance  balance available in the account
	 */
	public InsufficientFundsException(String accountNumber, double requestedAmount, double availableBalance) {
		super(String.format(
				"Insufficient funds in account %s: requested $%,.2f but only $%,.2f is available",
				accountNumber, requestedAmount, availableBalance));
		this.accountNumber = accountNumber;
		this.requestedAmount = requestedAmount;
		this.availableBalance = availableBalance;
	}

	/**
	 * @return the account number of the failed transaction
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @return the amount that was requested
	 */
	public double getRequestedAmount() {
		return requestedAmount;
	}

	/**
	 * @return the balance that was actually available
	 */
	public double getAvailableBalance() {
		return availableBalance;
	}
}