package examples.packageWithProperties;

import net.jqwik.api.*;

import java.util.*;

public class AnnotatedPropertiesTests {

	@Property
	boolean allIntegersAndNulls(@ForAll @WithNull(target = Integer.class) Integer anInt) {
		System.out.println(anInt);
		return anInt != null;
	}

	@Property(tries = 10)
	boolean aListWithNullIntegers(@ForAll @WithNull(value = 0.5) List<Integer> aList) {
		System.out.println(aList);
		return aList.stream().allMatch(anInt -> anInt != null);
	}
}
