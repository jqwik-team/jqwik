package net.jqwik.properties;

import org.opentest4j.*;

import java.util.function.*;

public class Assume {
	public static void that(boolean condition) {
		if (!condition)
			throw new TestAbortedException();
	}

	public static void that(Supplier<Boolean> booleanSupplier) {
		that(booleanSupplier.get());
	}
}
