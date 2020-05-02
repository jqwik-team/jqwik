import java.math.*;
import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

class Experiments {

	@Example
	public void bigIntegerPerformance(@ForAll Random random) {

		BigIntegerArbitrary bigIntegerArbitrary = Arbitraries.bigIntegers();
		RandomGenerator<BigInteger> generator = bigIntegerArbitrary.generator(10000);

		long before = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Shrinkable<BigInteger> value = generator.next(random);
		}
		long after = System.currentTimeMillis();

		System.out.println("TIME: " + (after - before)/100.0 + " secs");
	}
}

