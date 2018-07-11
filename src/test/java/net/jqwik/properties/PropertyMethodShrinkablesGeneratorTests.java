package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;
import org.assertj.core.api.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.fail;

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

		assertAtLeastOneGenerated(shrinkablesGenerator, random, Arrays.asList("a", 1));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, Arrays.asList("a", 2));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, Arrays.asList("a", 3));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, Arrays.asList("b", 1));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, Arrays.asList("b", 2));
		assertAtLeastOneGenerated(shrinkablesGenerator, random, Arrays.asList("b", 3));
	}

	private void assertAtLeastOneGenerated(ShrinkablesGenerator generator, Random random, List expected) {
		for (int i = 0; i < 500; i++) {
			List<Shrinkable> shrinkables = generator.next(random);
			if (values(shrinkables).equals(expected))
				return;
		}
		fail("Failed to generate at least once");
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
	}
}
