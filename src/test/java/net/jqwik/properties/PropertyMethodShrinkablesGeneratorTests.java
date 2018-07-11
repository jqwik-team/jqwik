package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;
import org.assertj.core.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;

class PropertyMethodShrinkablesGeneratorTests {


	@Example
	void useSimpleRegisteredArbitraryProviders(@ForAll Random random) {
		PropertyMethodShrinkablesGenerator shrinkablesGenerator = createGenerator("simpleParameters");
		List<Shrinkable> shrinkables = shrinkablesGenerator.next(random);

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

		PropertyMethodShrinkablesGenerator shrinkablesGenerator = createGenerator("simpleParameters", arbitraryResolver);

		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("a", 1));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("a", 2));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("a", 3));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("b", 1));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("b", 2));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("b", 3));
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

		PropertyMethodShrinkablesGenerator shrinkablesGenerator = createGenerator("twiceTypeVariableT", arbitraryResolver);

		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("a", "a"));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("b", "b"));
		assertNeverGenerated(shrinkablesGenerator, random, asList("a", "b"));
		assertNeverGenerated(shrinkablesGenerator, random, asList("b", "a"));
	}

	@Example
	void sameTypeVariableInParameter(@ForAll Random random) {

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

		PropertyMethodShrinkablesGenerator shrinkablesGenerator = createGenerator("typeVariableAlsoInList", arbitraryResolver);

		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("a", asList("a")));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, asList("b", asList("b")));
		assertNeverGenerated(shrinkablesGenerator, random, asList("a", asList("b")));
		assertNeverGenerated(shrinkablesGenerator, random, asList("b", asList("a")));
	}

	private void assertAtLeastOneGenerated(ShrinkablesGenerator generator, Random random, List expected) {
		for (int i = 0; i < 500; i++) {
			List<Shrinkable> shrinkables = generator.next(random);
			if (values(shrinkables).equals(expected))
				return;
		}
		fail("Failed to generate at least once");
	}

	private void assertNeverGenerated(ShrinkablesGenerator generator, Random random, List expected) {
		for (int i = 0; i < 500; i++) {
			List<Shrinkable> shrinkables = generator.next(random);
			if (values(shrinkables).equals(expected))
				fail(String.format("%s should never be generated", values(shrinkables)));
		}
	}

	private List<Object> values(List<Shrinkable> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private PropertyMethodShrinkablesGenerator createGenerator(String methodName) {
		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(MyProperties.class, new MyProperties());
		return createGenerator(methodName, arbitraryResolver);
	}

	private PropertyMethodShrinkablesGenerator createGenerator(String methodName, ArbitraryResolver arbitraryResolver) {
		PropertyMethodDescriptor methodDescriptor = createDescriptor(methodName);
		List<MethodParameter> parameters = getParameters(methodDescriptor);

		return PropertyMethodShrinkablesGenerator.forParameters(parameters, arbitraryResolver, 1000);
	}

	private List<MethodParameter> getParameters(PropertyMethodDescriptor methodDescriptor) {
		return Arrays //
					  .stream(JqwikReflectionSupport.getMethodParameters(methodDescriptor.getTargetMethod(), methodDescriptor.getContainerClass())) //
					  .collect(Collectors.toList());

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
