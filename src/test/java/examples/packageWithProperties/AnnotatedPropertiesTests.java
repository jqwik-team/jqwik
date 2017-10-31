package examples.packageWithProperties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class AnnotatedPropertiesTests {

	@Property
	boolean allIntegersAndNulls(@ForAll @WithNull(target = Integer.class) Integer anInt) {
		return anInt != null;
	}

	@Property(tries = 10)
	boolean aListWithNullIntegers(@ForAll @WithNull(value = 0.5, target = Integer.class) List<Integer> aList) {
		return aList.stream().allMatch(anInt -> anInt != null);
	}

	@Property(tries = 100)
	boolean integersAreWithinBounds(@ForAll @IntRange(min = -10, max = 10) int anInt) {
		return anInt >= -10 && anInt <= 10;
	}

	@Property(tries = 10)
	void defaultStrings(@ForAll @StringLength(max = 13) String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 50)
	void stringsByChars(@ForAll @Chars({ 'a', 'b', ' ' }) String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 50)
	void stringsByFromTo(@ForAll @Chars(from = 'a', to = 'Z') String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 50)
	void stringsCombined(@ForAll @Chars(from = '0', to = '9', value = {'a', 'b'}) String aRandomString) {
		System.out.println(String.format("[%s]", aRandomString));
	}

	@Property(tries = 10)
	void aListWithMaxSize(@ForAll @Size(max = 15) @StringLength(max = 4) @Chars({'x', 'y', 'z'}) List<String> listOfStrings) {
		System.out.println(String.format("%s", listOfStrings));
	}

	@Property(tries = 10)
	void aSetWithMaxSize(@ForAll @Size(max = 15) @IntRange(min = 1, max = 41) Set<Integer> setOfIntegers) {
		System.out.println(String.format("%s", setOfIntegers));
	}
}
