package examples.docs.contracts.eurocalc;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import org.assertj.core.api.*;
import org.assertj.core.data.*;

@Group
@Label("Contract: RateProvider")
class RateProviderContract {

	RateProviderContract() {
		// TODO: This part is not used (yet) in jqwik. It could be easily applied when creating mock/stub collaborators
		ContractRegistry.addPrecondition(RateProvider.class, "rate", (String fromCurrency, String toCurrency) -> {
			Assume.that(!fromCurrency.equals(toCurrency));
			return null;
		});
	}

	interface RateProviderContractTests<E extends RateProvider> {

		@Property
		default boolean willReturnRateAboveZeroForValidCurrencies(
			@ForAll("currencies") String from,
			@ForAll("currencies") String to,
			@ForAll E provider) {
			return provider.rate(from, to) > 0.0;
		}

		@Property
		default void willThrowExceptionsForInvalidCurrencies(
			@ForAll("currencies") String from,
			@ForAll("invalid") String to,
			@ForAll E provider) {

			Assertions.assertThatThrownBy(() -> provider.rate(from, to)).isInstanceOf(IllegalArgumentException.class);
			Assertions.assertThatThrownBy(() -> provider.rate(to, from)).isInstanceOf(IllegalArgumentException.class);
		}

		@Provide
		default Arbitrary<String> currencies() {
			return Arbitraries.of("EUR", "USD", "CHF", "CAD");
		}

		@Provide
		default Arbitrary<String> invalid() {
			return Arbitraries.of("A", "", "XXX", "CADCAD");
		}

	}

	@Group
	@Label("SimpleRateProvider")
	class SimpleRateProviderTests implements RateProviderContractTests<SimpleRateProvider> {
		@Provide
		Arbitrary<SimpleRateProvider> simpleRateProvider() {
			return Arbitraries.constant(new SimpleRateProvider());
		}
	}

	@Group
	@Label("Collaborator: EuroConverter")
	class EuroConverterCollaborationTests {

		@Property
		boolean willAlwaysConvertToPositiveEuroAmount(
			@ForAll("nonEuroCurrencies") String from,
			@ForAll @DoubleRange(min = 0.01, max = 1000000.0) double amount,
			@ForAll RateProvider provider) {

			double euroAmount = new EuroConverter(provider).convert(amount, from);
			return euroAmount > 0.0;
		}

		@Example
		void willCorrectlyUseExchangeRate() {
			RateProvider provider = (fromCurrency, toCurrency) -> 0.8;
			double euroAmount = new EuroConverter(provider).convert(8.0, "USD");
			Assertions.assertThat(euroAmount).isCloseTo(6.4, Offset.offset(0.01));
		}

		@Provide
		Arbitrary<String> nonEuroCurrencies() {
			return Arbitraries.of("USD", "CHF", "CAD");
		}

		@Provide
		Arbitrary<RateProvider> aRateProvider() {
			DoubleArbitrary rate = Arbitraries.doubles().between(0.1, 10.0);
			return rate.map(exchangeRate -> (fromCurrency, toCurrency) -> {
				Assertions.assertThat(fromCurrency).isNotEqualTo(toCurrency);
				return exchangeRate;
			});
		}
	}

}
