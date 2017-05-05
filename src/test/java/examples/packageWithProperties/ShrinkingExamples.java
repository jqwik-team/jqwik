package examples.packageWithProperties;

import net.jqwik.api.*;

import java.util.*;

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

	@Property
	boolean shrinkAListToSize2(@ForAll @MaxSize(10) @IntRange(min = -5, max = 5) List<Integer> aList) {
		return aList.size() <= 1;
	}
}
