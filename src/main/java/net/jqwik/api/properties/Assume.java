package net.jqwik.api.properties;

import net.jqwik.properties.*;

import java.util.function.*;

public class Assume {
	public static void that(boolean condition) {
		Assumptions.assumeThat(condition);
	}

	public static void that(Supplier<Boolean> booleanSupplier) {
		Assumptions.assumeThat(booleanSupplier);
	}
}
