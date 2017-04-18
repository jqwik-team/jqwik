package net.jqwik.properties;

import java.util.function.*;

import org.opentest4j.*;

public class Assume {
	public static void that(boolean condition) {
		if (!condition)
			throw new TestAbortedException();
	}

	public static void that(Supplier<Boolean> booleanSupplier) {
		that(booleanSupplier.get());
	}
}
