package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.providers.ArbitraryProvider.*;

public class DomainContextBaseProviders {

	private static final Logger LOG = Logger.getLogger(DomainContextBaseProviders.class.getName());

	static public List<ArbitraryProvider> forContextBase(DomainContextBase base, int priority) {
		List<Method> methods = AnnotationSupport.findAnnotatedMethods(base.getClass(), Provide.class, HierarchyTraversalMode.BOTTOM_UP);
		warnIfMethodsHaveWrongReturnType(methods);
		warnIfProvideAnnotationHasValue(methods);
		return methods.stream()
					  .filter(method -> isArbitrary(method.getReturnType()))
					  .map(method -> new MethodBaseArbitraryProvider(method, base, priority))
					  .collect(Collectors.toList());
	}

	private static void warnIfProvideAnnotationHasValue(List<Method> methods) {
		methods.stream()
			   .filter(method -> isArbitrary(method.getReturnType()))
			   .map(method -> Tuple.of(method, AnnotationSupport.findAnnotation(method, Provide.class)))
			   .filter(methodAndProvide -> methodAndProvide.get2().map(a -> !a.value().isEmpty()).orElse(false))
			   .forEach(methodAndProvide -> {
				   String message = String.format(
					   "Method %s is annotated with %s but having a value does not make sense in a domain context.",
					   methodAndProvide.get1(),
					   methodAndProvide.get2().get()
				   );
				   LOG.warning(message);
			   });

	}

	private static void warnIfMethodsHaveWrongReturnType(List<Method> methods) {
		methods.stream()
			   .filter(method -> !isArbitrary(method.getReturnType()))
			   .forEach(method -> {
				   String message = String.format("Method %s is annotated with @Provide but does not return an Arbitrary subtype.", method);
				   LOG.warning(message);
			   });
	}

	private static boolean isArbitrary(Class<?> type) {
		return Arbitrary.class.isAssignableFrom(type);
	}

	private static class DomainContextBaseSubtypeProvider extends InstanceBasedSubtypeProvider {

		private final SubtypeProvider baseProvider;

		protected DomainContextBaseSubtypeProvider(Object base, SubtypeProvider baseProvider) {
			super(base);
			this.baseProvider = baseProvider;
		}

		@Override
		protected Set<Arbitrary<?>> resolve(TypeUsage parameterType) {
			return baseProvider.apply(parameterType);
		}

		@Override
		protected Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
			// TODO: Implement configuration
			return arbitrary;
		}
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
			SubtypeProvider domainSubtypeProvider = new net.jqwik.engine.properties.DomainContextBaseProviders.DomainContextBaseSubtypeProvider(base, subtypeProvider);
			return new ProviderMethodInvoker(method, targetType, base, domainSubtypeProvider).invoke();
		}

		@Override
		public int priority() {
			return priority;
		}

		private boolean targetTypeFits(TypeUsage targetType) {
			TypeUsage arbitraryReturnType = arbitraryReturnType();
			// This can lead to arbitraries being applied where they don't fit due to parameterized inner types
			return arbitraryReturnType.canBeAssignedTo(targetType) || targetType.canBeAssignedTo(arbitraryReturnType);
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
