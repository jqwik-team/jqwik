package examples.packageWithProperties;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

public class NumberCoverageExamples {


	public static final Condition<Long> EVEN = new Condition<>(x -> x % 2 == 0, "even");
	public static final Condition<Long> SMALL = new Condition<>(x -> x < 2000, "< 2000");

	@Provide
	Arbitrary<Long> evenNumbers() {
		return Arbitraries.longs().filter(l -> l % 2 == 0);
	}

	@Property
	void evenNumbersAreEven(@ForAll("evenNumbers") long evenNumber) {
		assertThat(evenNumber)
			.is(EVEN);
	}

	@Property
	void evenNumbersAreEvenAndSmall(@ForAll("evenNumbers") long evenNumber) {
		//System.out.println(evenNumber);
		assertThat(evenNumber)
			.is(EVEN)
			.is(SMALL);
	}

	@Provide
	Arbitrary<Float> evenFloats() {
		return Arbitraries.floats().filter(f -> f.longValue() % 2 == 0);
	}

	@Property
	void floatsAreSmall(@ForAll("evenFloats") float evenNumber) {
		assertThat((long) evenNumber).is(SMALL);
	}

	@Property
	void bigDecimalDistribution(@ForAll BigDecimal bigDecimal) {
		String hasDecimals = bigDecimal.remainder(BigDecimal.ONE)
									   .compareTo(BigDecimal.ZERO) != 0 ? "decimals" : "no decimals";
		String aboveMillion = bigDecimal.compareTo(BigDecimal.valueOf(1000000L)) > 0 ? ">1M" : "<=1M";
		Statistics.collect(hasDecimals, aboveMillion);
	}

	@Property @Report(Reporting.GENERATED)
	void bigIntegerDistribution(@ForAll BigInteger bigInteger) {
		String ranges = bigInteger.compareTo(BigInteger.valueOf(100)) < 0 ? "<100"
			: bigInteger.compareTo(BigInteger.valueOf(1000000)) < 0 ? "<1M" : ">1M";
		Statistics.collect(ranges);
	}
}
