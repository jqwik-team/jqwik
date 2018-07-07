package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.stream.*;

class DefaultShrinkablesGeneratorTests {


	@Example
	void simpleParameters(@ForAll Random random) {
		DefaultShrinkablesGenerator shrinkablesGenerator = createGenerator("simpleParameters");
		List<Shrinkable> shrinkables = shrinkablesGenerator.next(random);

		Assertions.assertThat(shrinkables.get(0).value()).isInstanceOf(String.class);
		Assertions.assertThat(shrinkables.get(1).value()).isInstanceOf(Integer.class);
	}

	private DefaultShrinkablesGenerator createGenerator(String methodName) {
		PropertyMethodDescriptor methodDescriptor = createDescriptor(methodName);
		PropertyMethodArbitraryResolver arbitraryResolver = new PropertyMethodArbitraryResolver(MyProperties.class, new MyProperties());
		List<MethodParameter> parameters = getParameters(methodDescriptor);

		return DefaultShrinkablesGenerator.forParameters(parameters, arbitraryResolver, 1000);
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
