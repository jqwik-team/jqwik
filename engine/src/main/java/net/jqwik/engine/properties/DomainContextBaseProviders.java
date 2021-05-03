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
					  .filter(method -> isArbitrary(method.getReturnType()))
					  .map(method -> new MethodBaseArbitraryProvider(method, base, priority))
					  .collect(Collectors.toList());
	}

	private static boolean isArbitrary(Class<?> type) {
		return Arbitrary.class.isAssignableFrom(type);
	}

	private static class MethodBaseArbitraryProvider implements ArbitraryProvider {

		private MethodBaseArbitraryProvider(Method method, Object base, int priority) {
			this.method = method;
			this.base = base;
			this.priority = priority;
		}

		private final Method method;
		private final Object base;
		private final int priority;

		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetTypeFits(targetType);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return new ProviderMethodInvoker(base, subtypeProvider).invoke(method, targetType);
		}

		@Override
		public int priority() {
			return priority;
		}

		private boolean targetTypeFits(TypeUsage targetType) {
			return arbitraryReturnType().canBeAssignedTo(targetType);
		}

		private TypeUsage arbitraryReturnType() {
			TypeUsage arbitraryType = arbitraryType(TypeUsage.forType(method.getGenericReturnType()));
			return arbitraryType.getTypeArgument(0);
		}

		private TypeUsage arbitraryType(TypeUsage baseType) {
			if (!baseType.isOfType(Arbitrary.class)) {
				for (TypeUsage anInterface : baseType.getInterfaces()) {
					if (isArbitrary(anInterface.getRawType())) {
						return arbitraryType(anInterface);
					}
				}
			}
			return baseType;
		}
	}
}
