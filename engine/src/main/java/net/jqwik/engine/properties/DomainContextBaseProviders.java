package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

public class DomainContextBaseProviders {

	static public List<ArbitraryProvider> forContextBase(DomainContextBase base, int priority) {
		List<Method> methods = AnnotationSupport.findAnnotatedMethods(base.getClass(), Provide.class, HierarchyTraversalMode.BOTTOM_UP);
		return methods.stream()
					  .filter(method -> Arbitrary.class.isAssignableFrom(method.getReturnType()))
					  .map(method -> new ArbitraryProvider() {
						  @Override
						  public boolean canProvideFor(TypeUsage targetType) {
							  return targetTypeFits(method, targetType);
						  }

						  @Override
						  public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
							  return new ProviderMethodInvoker(base, subtypeProvider).invoke(method, targetType);
						  }

						  @Override
						  public int priority() {
							  return priority;
						  }
					  })
					  .collect(Collectors.toList());
	}

	private static boolean targetTypeFits(Method method, TypeUsage targetType) {
		// TypeUsage methodReturnType = TypeUsageImpl.forParameter() forType(method.getAnnotatedReturnType());
		return true;
	}
}
