package net.jqwik.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

public class PropertyMethodDataResolver implements DataResolver {
	private final Class<?> containerClass;
	private final Object testInstance;

	public PropertyMethodDataResolver(Class<?> containerClass, Object testInstance) {
		this.containerClass = containerClass;
		this.testInstance = testInstance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Iterable<? extends Tuple>> forMethod(Method method) {
		Optional<DataFrom> optionalDataFrom = AnnotationSupport.findAnnotation(method, DataFrom.class);
		return optionalDataFrom
				   .map(DataFrom::value)
				   .flatMap(this::findGenerator)
				   .map(generatorMethod -> JqwikReflectionSupport.invokeMethodPotentiallyOuter(generatorMethod, testInstance))
				   .map(invocationResult -> (Iterable<Tuple>) invocationResult);
	}

	private Optional<Method> findGenerator(String generatorName) {
		Function<Method, String> generatorNameSupplier = method -> {
			Data generateAnnotation = method.getDeclaredAnnotation(Data.class);
			return generateAnnotation.value();
		};
		TypeUsage targetType = TypeUsage.of(Iterable.class, TypeUsage.wildcard(TypeUsage.of(Tuple.class)));
		return JqwikReflectionSupport.findGeneratorMethod(generatorName, this.containerClass, Data.class, generatorNameSupplier, targetType);
	}
}
