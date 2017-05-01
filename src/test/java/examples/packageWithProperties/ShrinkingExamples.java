package examples.packageWithProperties;

import net.jqwik.api.*;

public class ShrinkingExamples {

	@Property
	boolean shrinkSingleIntegerTo90(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		return anInt < 90;
	}
}
