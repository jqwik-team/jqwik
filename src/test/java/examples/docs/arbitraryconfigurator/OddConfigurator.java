package examples.docs.arbitraryconfigurator;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

public class OddConfigurator extends ArbitraryConfiguratorBase {
	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
	}
}
