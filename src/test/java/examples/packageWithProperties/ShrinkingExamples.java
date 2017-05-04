package examples.packageWithProperties;

import net.jqwik.api.*;

public class ShrinkingExamples {

	@Property
	boolean shrinkSingleIntegerTo1(@ForAll int anInt) {
		return anInt <= 0;
	}

	@Property
	boolean shrinkTwoIntegers(
		@ForAll @IntRange(min = 10, max = 20) int int1,
		@ForAll @IntRange(min = 10, max = 20) int int2) {
		return int1 >= int2;
	}
}
