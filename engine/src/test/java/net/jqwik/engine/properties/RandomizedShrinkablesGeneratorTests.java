package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class RandomizedShrinkablesGeneratorTests {

	@Example
	void useSimpleRegisteredArbitraryProviders(@ForAll Random random) {
		RandomizedShrinkablesGenerator shrinkablesGenerator = createGenerator(random, "simpleParameters");
		List<Shrinkable> shrinkables = shrinkablesGenerator.next();

		Assertions.assertThat(shrinkables.get(0).value()).isInstanceOf(String.class);
		Assertions.assertThat(shrinkables.get(1).value()).isInstanceOf(Integer.class);
	}

	@Example
	void severalFittingArbitraries(@ForAll Random random) {

		ArbitraryResolver arbitraryResolver = new ArbitraryResolver() {
			@Override
			public Set<Arbitrary<?>> forParameter(MethodParameter parameter) {
				Set<Arbitrary<?>> arbitraries = new HashSet<>();
				if (parameter.getType().equals(String.class)) {
					arbitraries.add(Arbitraries.constant("a"));
					arbitraries.add(Arbitraries.constant("b"));
				}
				if (parameter.getType().equals(int.class)) {
					arbitraries.add(Arbitraries.constant(1));
					arbitraries.add(Arbitraries.constant(2));
					arbitraries.add(Arbitraries.constant(3));
				}
				return arbitraries;
			}
		};

		RandomizedShrinkablesGenerator shrinkablesGenerator = createGenerator(random, "simpleParameters", arbitraryResolver);

		assertAtLeastOneGenerated(shrinkablesGenerator, asList("a", 1));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("a", 2));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("a", 3));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("b", 1));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("b", 2));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("b", 3));
	}

	@Example
	void sameTypeVariableGetsSameArbitrary(@ForAll Random random) {

		ArbitraryResolver arbitraryResolver = new ArbitraryResolver() {
			@Override
			public Set<Arbitrary<?>> forParameter(MethodParameter parameter) {
				Set<Arbitrary<?>> arbitraries = new HashSet<>();
				arbitraries.add(Arbitraries.constant("a"));
				arbitraries.add(Arbitraries.constant("b"));
				return arbitraries;
			}
		};

		RandomizedShrinkablesGenerator shrinkablesGenerator = createGenerator(random, "twiceTypeVariableT", arbitraryResolver);

		assertAtLeastOneGenerated(shrinkablesGenerator, asList("a", "a"));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("b", "b"));
		assertNeverGenerated(shrinkablesGenerator, asList("a", "b"));
		assertNeverGenerated(shrinkablesGenerator, asList("b", "a"));
	}

	@Example
	void sameTypeVariableInParameterOfType(@ForAll Random random) {

		ArbitraryResolver arbitraryResolver = new ArbitraryResolver() {
			@Override
			public Set<Arbitrary<?>> forParameter(MethodParameter parameter) {
				Set<Arbitrary<?>> arbitraries = new HashSet<>();
				Arbitrary<String> a = Arbitraries.constant("a");
				Arbitrary<String> b = Arbitraries.constant("b");
				if (parameter.getType() instanceof TypeVariable) {
					arbitraries.add(a);
					arbitraries.add(b);
				} else {
					arbitraries.add(a.list().ofSize(1));
					arbitraries.add(b.list().ofSize(1));
				}
				return arbitraries;
			}
		};

		RandomizedShrinkablesGenerator shrinkablesGenerator = createGenerator(random, "typeVariableAlsoInList", arbitraryResolver);

		assertAtLeastOneGenerated(shrinkablesGenerator, asList("a", asList("a")));
		assertAtLeastOneGenerated(shrinkablesGenerator, asList("b", asList("b")));

		// TODO: This is really hard to implement and probably requires core changes in Arbitrary/RandomGenerator
		//assertNeverGenerated(shrinkablesGenerator, random, asList("a", asList("b")));
		//assertNeverGenerated(shrinkablesGenerator, random, asList("b", asList("a")));
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

	private RandomizedShrinkablesGenerator createGenerator(Random random, String methodName) {
		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(
			MyProperties.class, new MyProperties(),
			DomainContext.global()
		);
		return createGenerator(random, methodName, arbitraryResolver);
	}

	private RandomizedShrinkablesGenerator createGenerator(Random random, String methodName, ArbitraryResolver arbitraryResolver) {
		PropertyMethodDescriptor methodDescriptor = createDescriptor(methodName);
		List<MethodParameter> parameters = TestHelper.getParameters(methodDescriptor);

		return RandomizedShrinkablesGenerator.forParameters(parameters, arbitraryResolver, random, 1000);
	}

	private PropertyMethodDescriptor createDescriptor(String methodName) {
		return TestHelper.createPropertyMethodDescriptor(MyProperties.class, methodName, "0", 1000, 5, ShrinkingMode.FULL);
	}

	private static class MyProperties {

		public void simpleParameters(@ForAll String aString, @ForAll int anInt) {}

		public <T> void twiceTypeVariableT(@ForAll T t1, @ForAll T t2) {}

		public <T> void typeVariableAlsoInList(@ForAll T t, @ForAll List<T> tList) {}
	}
}
