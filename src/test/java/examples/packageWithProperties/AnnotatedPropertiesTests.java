package examples.packageWithProperties;

import java.util.*;

import net.jqwik.api.*;

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

	@Property(tries = 10)
	void defaultStrings(@ForAll @MaxSize(13) String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 50)
	void stringsByChars(@ForAll @ValidChars({ 'a', 'b', ' ' }) String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 50)
	void stringsByFromTo(@ForAll @ValidChars(from = 'a', to = 'Z') String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 50)
	void stringsCombined(@ForAll @ValidChars(from = '0', to = '9', value = {'a', 'b'}) String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

}
