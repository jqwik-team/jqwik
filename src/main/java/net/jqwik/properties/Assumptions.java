package net.jqwik.properties;

import org.opentest4j.*;

import java.util.function.*;

public class Assumptions {
	public static void assumeThat(boolean condition) {
		if (!condition)
			throw new TestAbortedException();
	}

	public static void assumeThat(Supplier<Boolean> booleanSupplier) {
		assumeThat(booleanSupplier.get());
	}
}
