package examples.docs.contracts.eurocalc;

public class SimpleRateProvider implements RateProvider {
	@Override
	public double rate(String fromCurrency, String toCurrency) {
		checkCurrencyValid(fromCurrency);
		checkCurrencyValid(toCurrency);
		return 1.0;
	}

	private void checkCurrencyValid(String currency) {
		switch (currency) {
			case "EUR":
				break;
			case "USD":
				break;
			case "CHF":
				break;
			case "CAD":
				break;
			default: {
				throw new IllegalArgumentException(currency + " is not a valid currency");
			}
		}
	}
}
