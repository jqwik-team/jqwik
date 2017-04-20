package examples.packageWithProperties;

import net.jqwik.api.*;

import java.util.*;

public class AnnotatedPropertiesTests {

	@Property
	boolean allIntegersAndNulls(@ForAll @WithNull(target = Integer.class) Integer anInt) {
		return anInt != null;
	}

	@Property(tries = 10)
	boolean aListWithNullIntegers(@ForAll @WithNull(value = 0.5) List<Integer> aList) {
		return aList.stream().allMatch(anInt -> anInt != null);
	}

	@Property(tries = 100)
	boolean integersAreWithinBounds(@ForAll @IntRange(min = -10, max = 10) int anInt) {
		return anInt >= -10 && anInt <= 10;
	}
}
