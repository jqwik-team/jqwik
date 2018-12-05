package examples.docs.contracts.eurocalc;

public interface RateProvider {
	double rate(String fromCurrency, String toCurrency);
}
