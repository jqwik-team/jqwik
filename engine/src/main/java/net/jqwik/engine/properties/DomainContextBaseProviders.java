package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.providers.ArbitraryProvider.*;
import net.jqwik.engine.support.*;

import static java.util.Collections.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class DomainContextBaseProviders {

	private static final Logger LOG = Logger.getLogger(DomainContextBaseProviders.class.getName());

	static public Collection<ArbitraryProvider> forContextBase(DomainContextBase base, int priority) {
		return JqwikStreamSupport.concat(
			providersFromProviderMethods(base, priority),
			providersFromInnerClasses(base, priority),
			providersFromBaseItself(base, priority)
		).collect(Collectors.toList());
	}

	private static Stream<ArbitraryProvider> providersFromProviderMethods(DomainContextBase base, int priority) {
		List<Method> methods = AnnotationSupport.findAnnotatedMethods(base.getClass(), Provide.class, HierarchyTraversalMode.BOTTOM_UP);
		warnIfMethodsHaveWrongReturnType(methods);
		warnIfProvideAnnotationHasValue(methods);
		return methods.stream()
					  .filter(method -> isArbitrary(method.getReturnType()))
					  .map(method -> new MethodBasedArbitraryProvider(method, base, priority));
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

	private static Stream<ArbitraryProvider> providersFromInnerClasses(DomainContextBase base, int priority) {
		Predicate<Class<?>> implementsArbitraryProvider =
			clazz -> ArbitraryProvider.class.isAssignableFrom(clazz) && !JqwikReflectionSupport.isPrivate(clazz);
		List<Class<?>> arbitraryProviderClasses = ReflectionSupport.findNestedClasses(base.getClass(), implementsArbitraryProvider);
		warnIfClassesHaveNoFittingConstructor(arbitraryProviderClasses);
		return arbitraryProviderClasses.stream()
									   .filter(DomainContextBaseProviders::hasFittingConstructor)
									   .map(clazz -> createArbitraryProvider(clazz, base, priority));
	}

	private static Stream<ArbitraryProvider> providersFromBaseItself(DomainContextBase base, int priority) {
		if (base instanceof ArbitraryProvider) {
			return Stream.of(new ArbitraryProviderWithPriority((ArbitraryProvider) base, priority));
		}
		return Stream.empty();
	}

	private static void warnIfClassesHaveNoFittingConstructor(List<Class<?>> classes) {
		classes.stream()
			   .filter(aClass -> !hasFittingConstructor(aClass))
			   .forEach(DomainContextBaseProviders::warnThatNoDefaultConstructorPresent);

	}

	private static void warnThatNoDefaultConstructorPresent(Class<?> aClass) {
		String message = String.format(
			"Class <%s> does not have a default constructor and cannot be instantiated as %s.",
			aClass.getName(),
			ArbitraryProvider.class
		);
		LOG.warning(message);
	}

	private static boolean hasFittingConstructor(Class<?> clazz) {
		if (JqwikReflectionSupport.isStatic(clazz)) {
			return hasDefaultConstructor(clazz);
		}
		return hasConstructor(clazz, clazz.getDeclaringClass());
	}

	private static ArbitraryProvider createArbitraryProvider(Class<?> clazz, DomainContextBase base, int priority) {
		ArbitraryProvider arbitraryProviderInstance = (ArbitraryProvider) JqwikReflectionSupport.newInstanceInTestContext(clazz, base);
		if (JqwikReflectionSupport.implementsMethod(clazz, "priority", new Class[0], ArbitraryProvider.class)) {
			return arbitraryProviderInstance;
		}
		return new ArbitraryProviderWithPriority(arbitraryProviderInstance, priority);
	}

	private static boolean isArbitrary(Class<?> type) {
		return Arbitrary.class.isAssignableFrom(type);
	}

	private static class ArbitraryProviderWithPriority implements ArbitraryProvider {

		private final ArbitraryProvider instance;
		private final int priority;

		private ArbitraryProviderWithPriority(ArbitraryProvider instance, int priority) {
			this.instance = instance;
			this.priority = priority;
		}

		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return instance.canProvideFor(targetType);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return instance.provideFor(targetType, subtypeProvider);
		}

		@Override
		public int priority() {
			return priority;
		}
	}

	private static class DomainContextBaseSubtypeProvider extends InstanceBasedSubtypeProvider {

		private final SubtypeProvider baseProvider;

		protected DomainContextBaseSubtypeProvider(Object base, SubtypeProvider baseProvider) {
			super(Collections.singletonList(base));
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

	private static class MethodBasedArbitraryProvider implements ArbitraryProvider {

		private MethodBasedArbitraryProvider(Method method, Object base, int priority) {
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
			return ProviderMethod.forMethod(method, targetType, singletonList(base), domainSubtypeProvider).invoke();
		}

		@Override
		public int priority() {
			return priority;
		}

		private boolean targetTypeFits(TypeUsage targetType) {
			TypeUsage arbitraryReturnType = arbitraryReturnType();
			return arbitraryReturnType.canBeAssignedTo(targetType);
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
