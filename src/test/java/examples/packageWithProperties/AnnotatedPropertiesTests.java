package examples.packageWithProperties;

import net.jqwik.api.*;

public class AnnotatedPropertiesTests {

	@Property
	boolean allIntegersAndNulls(@ForAll @WithNull Integer anInt) {
		System.out.println(anInt);
		return anInt != null;
	}
}
