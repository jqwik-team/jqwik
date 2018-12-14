package net.jqwik.docs.defaultprovider;

import java.math.*;

public class Money {

	private final BigDecimal amount;
	private final String currency;

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public Money(BigDecimal amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	@Override
	public String toString() {
		return String.format("%s %s", amount, currency);
	}

	public Money times(int factor) {
		return new Money(amount.multiply(new BigDecimal(factor)), currency);
	}
}
