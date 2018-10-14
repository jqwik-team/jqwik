package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;

import static org.assertj.core.api.Assertions.*;

class ExhaustiveShrinkablesGeneratorTests {

	@Example
	void singleIntParameter() {
		ExhaustiveShrinkablesGenerator shrinkablesGenerator = createGenerator("intFrom0to5");
		assertThat(shrinkablesGenerator.maxCount()).isEqualTo(6);

		assertThat(shrinkablesGenerator.hasNext()).isTrue();
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(0));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(1));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(2));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(3));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(4));
		assertThat(shrinkablesGenerator.hasNext()).isTrue();
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(5));
		assertThat(shrinkablesGenerator.hasNext()).isFalse();
	}

	@Example
	void ambiguousArbitraryResolutionFailsToCreateExhaustiveShrinkablesGenerator() {
		assertThatThrownBy( () -> createGenerator("genericNumber")).isInstanceOf(JqwikException.class);
	}

	//@Example
	void twoIntParameters() {
		ExhaustiveShrinkablesGenerator shrinkablesGenerator = createGenerator("intFrom1to3And4to5");
		assertThat(shrinkablesGenerator.maxCount()).isEqualTo(6);

		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(1), Shrinkable.unshrinkable(4));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(2), Shrinkable.unshrinkable(4));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(3), Shrinkable.unshrinkable(4));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(1), Shrinkable.unshrinkable(5));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(2), Shrinkable.unshrinkable(5));
		assertThat(shrinkablesGenerator.next()).containsExactly(Shrinkable.unshrinkable(3), Shrinkable.unshrinkable(5));
		assertThat(shrinkablesGenerator.hasNext()).isFalse();
	}

	private void assertAtLeastOneGenerated(ShrinkablesGenerator generator, List expected) {
		for (int i = 0; i < 500; i++) {
			List<Shrinkable> shrinkables = generator.next();
			if (values(shrinkables).equals(expected))
				return;
		}
		fail("Failed to generate at least once");
	}

	private void assertNeverGenerated(ShrinkablesGenerator generator, List expected) {
		for (int i = 0; i < 500; i++) {
			List<Shrinkable> shrinkables = generator.next();
			if (values(shrinkables).equals(expected))
				fail(String.format("%s should never be generated", values(shrinkables)));
		}
	}

	private List<Object> values(List<Shrinkable> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private ExhaustiveShrinkablesGenerator createGenerator(String methodName) {
		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(MyProperties.class, new MyProperties());
		return createGenerator(methodName, arbitraryResolver);
	}

	private ExhaustiveShrinkablesGenerator createGenerator(String methodName, ArbitraryResolver arbitraryResolver) {
		PropertyMethodDescriptor methodDescriptor = createDescriptor(methodName);
		List<MethodParameter> parameters = TestHelper.getParameters(methodDescriptor);

		return ExhaustiveShrinkablesGenerator.forParameters(parameters, arbitraryResolver);
	}

	private PropertyMethodDescriptor createDescriptor(String methodName) {
		return TestHelper.createPropertyMethodDescriptor(MyProperties.class, methodName, "0", 1000, 5, ShrinkingMode.FULL);
	}

	private static class MyProperties {

		public void intFrom0to5(@ForAll @IntRange(min = 0, max = 5) int anInt) {}

		public void intFrom1to3And4to5(
			@ForAll @IntRange(min = 1, max = 3) int int1,
			@ForAll @IntRange(min = 4, max = 5) int int2
		) {}

		public void genericNumber(@ForAll Number aNumber) {}
	}
}
