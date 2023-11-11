package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class PropertyMethodDataResolver implements DataResolver {
	private final Class<?> containerClass;
	private final List<Object> testInstances;

	public PropertyMethodDataResolver(Class<?> containerClass, List<Object> testInstances) {
		this.containerClass = containerClass;
		this.testInstances = testInstances;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Iterable<? extends Tuple>> forMethod(Method method) {
		Optional<FromData> optionalDataFrom = AnnotationSupport.findAnnotation(method, FromData.class);
		return optionalDataFrom
				   .map(FromData::value)
				   .map(generatorName -> {
					   Supplier<JqwikException> exceptionSupplier =
						   () -> new JqwikException("No data provider method (annotated with @Data) for generator [" + generatorName + "] found");
					   return findGenerator(generatorName).orElseThrow(exceptionSupplier);
				   })
				   .map(generatorMethod -> JqwikReflectionSupport.invokeMethodOnContainer(generatorMethod, testInstances))
				   .map(invocationResult -> (Iterable<Tuple>) invocationResult);
	}

	private Optional<Method> findGenerator(String generatorName) {
		Function<Method, String> generatorNameSupplier = method -> {
			Data generateAnnotation = method.getDeclaredAnnotation(Data.class);
			return generateAnnotation.value();
		};
		TypeUsage expectedReturnType = TypeUsage.of(Iterable.class, TypeUsage.wildcard(TypeUsage.of(Tuple.class)));
		return findGeneratorMethod(generatorName, this.containerClass, Data.class, generatorNameSupplier, expectedReturnType);
	}
}
