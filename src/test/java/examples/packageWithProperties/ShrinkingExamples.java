package examples.packageWithProperties;

import net.jqwik.api.*;

import java.math.*;
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
	boolean shrinkAListToSize2(@ForAll @MaxSize(1000) @IntRange(min = -5, max = 5) List<Integer> aList) {
		return aList.size() <= 1;
	}

	@Property
	boolean shrinkBigIntegerTo2(@ForAll BigInteger big) {
		return big.compareTo(new BigInteger("2")) < 0;
	}

	@Property
	boolean shrinkLongTo2(@ForAll Long aLong) {
		return aLong < 2;
	}

	@Property
	boolean shrinkDoubleTo2(@ForAll double aDouble) {
		return aDouble < 1.9;
	}

	@Property
	boolean shrinkFloatTo2(@ForAll float aFloat) {
		return aFloat < 1.9;
	}

	@Property
	boolean shrinkArrayToLength2(@ForAll @MaxSize(100) Integer[] anIntArray) {
		return anIntArray.length < 2;
	}
}
