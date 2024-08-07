package net.jqwik.engine.support;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;

import static java.util.stream.Collectors.*;

import static net.jqwik.engine.support.OverriddenMethodAnnotationSupport.*;

public class JqwikReflectionSupport {

	private static final Logger LOG = Logger.getLogger(JqwikReflectionSupport.class.getName());

	@SuppressWarnings("deprecation") // Deprecated as of Java 9
	private static <T extends AccessibleObject> T makeAccessible(T object) {
		if (!object.isAccessible()) {
			object.setAccessible(true);
		}
		return object;
	}

	/**
	 * Create instance of a class that can potentially be a non static inner class
	 *
	 * @param <T>   The type of the instance to create
	 * @param clazz The class to instantiate
	 * @return the newly created instance
	 */
	public static <T> T newInstanceWithDefaultConstructor(Class<T> clazz) {
		if (isInnerClass(clazz)) {
			Object parentInstance = newInstanceWithDefaultConstructor(clazz.getDeclaringClass());
			return ReflectionSupport.newInstance(clazz, parentInstance);
		} else {
			return ReflectionSupport.newInstance(clazz);
		}
	}

	/**
	 * Create instance of a class that can potentially be a non static inner class
	 *
	 * @param clazz The class to instantiate
	 * @return all newly created instances with from most outer to most inner
	 */
	public static List<Object> newInstancesWithDefaultConstructor(Class<?> clazz) {
		if (isInnerClass(clazz)) {
			List<Object> instances = newInstancesWithDefaultConstructor(clazz.getDeclaringClass());
			Object inner = ReflectionSupport.newInstance(clazz, instances.get(instances.size() - 1));
			instances.add(inner);
			return instances;
		} else {
			List<Object> instances = new ArrayList<>();
			instances.add(ReflectionSupport.newInstance(clazz));
			return instances;
		}
	}

	/**
	 * Create instance of a class that can potentially be a non static inner class
	 * and its outer instance might be {@code context}
	 *
	 * @param <T>     The type of the instance to create
	 * @param clazz   The class to instantiate
	 * @param context The potential context instance
	 * @return the newly created instance
	 */
	public static <T> T newInstanceInTestContext(Class<T> clazz, Object context) {
		if (!isInnerClass(clazz)) {
			return ReflectionSupport.newInstance(clazz);
		}
		Class<?> outerClass = clazz.getDeclaringClass();
		Object parentInstance = outerClass.isAssignableFrom(context.getClass()) ?
									context : newInstanceWithDefaultConstructor(outerClass);
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor(outerClass);
			return newInstance(constructor, parentInstance);
		} catch (NoSuchMethodException e) {
			return JqwikExceptionSupport.throwAsUncheckedException(e);
		}
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... args) {
		try {
			return makeAccessible(constructor).newInstance(args);
		} catch (Throwable t) {
			return JqwikExceptionSupport.throwAsUncheckedException(t);
		}
	}

	/**
	 * Find all {@linkplain Method methods} as in ReflectionSupport.findMethods(..) but also use outer classes to look for
	 * methods.
	 *
	 * <p>
	 *     Duplicate methods (through) inheritance are de-duplicated. The first occurrence of a method is kept.
	 * </p>
	 *
	 * @param clazz         The class in which you start the search
	 * @param predicate     The condition to check for all candidate methods
	 * @param traversalMode Traverse hierarchy up or down. Determines the order in resulting list.
	 * @return List of found methods
	 */
	public static List<Method> findMethodsPotentiallyOuter(
		Class<?> clazz,
		Predicate<Method> predicate,
		HierarchyTraversalMode traversalMode
	) {
		List<Class<?>> searchClasses = getDeclaringClasses(clazz, traversalMode);
		Set<Method> foundMethods = new LinkedHashSet<>();
		for (Class<?> searchClass : searchClasses) {
			foundMethods.addAll(ReflectionSupport.findMethods(searchClass, predicate, traversalMode));
		}
		return new ArrayList<>(foundMethods);
	}

	/**
	 * Find all {@linkplain Field field} but also use outer classes to look for
	 * methods.
	 *
	 * @param clazz         The class in which you start the search
	 * @param predicate     The condition to check for all candidate methods
	 * @param traversalMode Traverse hierarchy up or down. Determines the order in resulting list.
	 * @return List of found fields
	 */
	public static List<Field> findFieldsPotentiallyOuter(
		Class<?> clazz,
		Predicate<Field> predicate,
		HierarchyTraversalMode traversalMode
	) {
		List<Class<?>> searchClasses = getDeclaringClasses(clazz, traversalMode);
		List<Field> foundFields = new ArrayList<>();
		for (Class<?> searchClass : searchClasses) {
			foundFields.addAll(ReflectionSupport.findFields(searchClass, predicate, traversalMode));
		}
		return foundFields;
	}

	/**
	 * Read a field's value as in ReflectionSupport.getField(..) but potentially use outer instances if the field belongs to an inner class.
	 *
	 * @param field The field to read
	 * @param targetInstances The container instances to read the field from, from outermost to innermost
	 * @return The value of the field
	 */
	public static Object readFieldOnContainer(Field field, List<Object> targetInstances) {
		makeAccessible(field);
		return readField(field, new ArrayDeque<>(targetInstances));
	}

	private static Object readField(Field field, Deque<Object> instances) {
		Object target = instances.removeLast();
		List<Field> declaredFields = Arrays.stream(target.getClass().getDeclaredFields()).collect(toList());
		if (declaredFields.contains(field)) {
			try {
				return field.get(target);
			} catch (Exception exception) {
				return JqwikExceptionSupport.throwAsUncheckedException(exception);
			}
		} else {
			if (instances.isEmpty()) {
				String message = String.format("Cannot access value of field %s", field);
				throw new JqwikException(message);
			}
			return readField(field, instances);
		}
	}

	/**
	 * Set a field's value as in ReflectionSupport.setField(..) but potentially use outer instances if the field belongs to an inner class.
	 *
	 * @param field The field to set
	 * @param value The value to set in the field
	 * @param targetInstances The container instances to set the field on, from outermost to innermost
	 */
	public static void setFieldOnContainer(Field field, Object value, List<Object> targetInstances) {
		makeAccessible(field);
		setField(field, value, new ArrayDeque<Object>(targetInstances));
	}

	private static void setField(Field field, Object value, Deque<Object> instances) {
		Object target = instances.removeLast();
		List<Field> declaredFields = Arrays.stream(target.getClass().getDeclaredFields()).collect(toList());
		if (declaredFields.contains(field)) {
			try {
				if (isStatic(field)) {
					field.set(null, value);
				} else {
					field.set(target, value);
				}
			} catch (Exception exception) {
				//noinspection ResultOfMethodCallIgnored
				JqwikExceptionSupport.throwAsUncheckedException(exception);
			}
		} else {
			if (instances.isEmpty()) {
				String message = String.format("Cannot set value of field %s", field);
				throw new JqwikException(message);
			}
			setField(field, value, instances);
		}
	}

	private static List<Class<?>> getDeclaringClasses(Class<?> clazz, HierarchyTraversalMode traversalMode) {
		List<Class<?>> declaringClasses = new ArrayList<>();
		Class<?> nextClass = clazz;
		while (nextClass != null) {
			if (traversalMode == HierarchyTraversalMode.BOTTOM_UP) {
				declaringClasses.add(nextClass);
			} else {
				declaringClasses.add(0, nextClass);
			}
			nextClass = nextClass.getDeclaringClass();
		}
		return declaringClasses;
	}

	/**
	 * Invoke the supplied {@linkplain Method method} as in ReflectionSupport.invokeMethod(..) but potentially use outer
	 * instances if the method belongs to the outer instances of an object.
	 *
	 * @param method    The method to invoke
	 * @param instances The container instances to invoke the method on, from outermost to innermost
	 * @param args      The arguments of the method invocation
	 * @return Result of method invocation if there is one, otherwise null
	 */
	public static Object invokeMethodOnContainer(Method method, List<Object> instances, Object... args) {
		return invokeMethod(method, new ArrayDeque<>(instances), args);
	}

	private static Object invokeMethod(Method method, Deque<Object> instances, Object... args) {
		Object target = instances.removeLast();
		if (method.getDeclaringClass().isAssignableFrom(target.getClass())) {
			return ReflectionSupport.invokeMethod(method, target, args);
		} else {
			if (instances.isEmpty()) {
				String message = String.format("Method [%s] cannot be invoked on target [%s].", method, target);
				throw new IllegalArgumentException(message);
			}
			return invokeMethod(method, instances, args);
		}
	}

	public static Set<Path> getAllClasspathRootDirectories() {
		// TODO: This is quite a hack, since sometimes the classpath is quite different.
		// Especially under Java >=9's module system this will probably no longer work.
		String classpath = System.getProperty("java.class.path");
		return Arrays.stream(classpath.split(File.pathSeparator))
					 .map(Paths::get).filter(Files::isDirectory)
					 .collect(CollectorsSupport.toLinkedHashSet());
	}

	public static List<MethodParameter> getMethodParameters(Executable method, Class<?> containerClass) {
		List<MethodParameter> list = new ArrayList<>();
		Parameter[] parameters = method.getParameters();
		GenericsClassContext containerClassContext = GenericsSupport.contextFor(containerClass);

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			TypeResolution resolution = containerClassContext.resolveParameter(parameter);
			MethodParameter methodParameter = new MethodParameter(parameter, resolution, i);
			list.add(methodParameter);
		}
		return list;
	}

	public static MethodParameter getMethodParameter(Parameter parameter, int index, Class<?> containerClass) {
		GenericsClassContext containerClassContext = GenericsSupport.contextFor(containerClass);
		TypeResolution resolution = containerClassContext.resolveParameter(parameter);
		return new MethodParameter(parameter, resolution, index);
	}

	public static Optional<Method> findGeneratorMethod(
		String generatorToFind,
		Class<?> containerClass,
		Class<? extends Annotation> requiredGeneratorAnnotation,
		Function<Method, String> generatorNameSupplier,
		TypeUsage expectedReturnType
	) {
		List<Method> creators = findMethodsPotentiallyOuter(
			containerClass,
			isGeneratorMethod(expectedReturnType, requiredGeneratorAnnotation),
			HierarchyTraversalMode.BOTTOM_UP
		);
		return creators.stream().filter(generatorMethod -> {
			String generatorName = generatorNameSupplier.apply(generatorMethod);
			if (generatorName.isEmpty()) {
				generatorName = JqwikKotlinSupport.javaOrKotlinName(generatorMethod);
			}
			return generatorName.equals(generatorToFind);
		}).findFirst();
	}

	public static <T> Constructor<T> findConstructor(Class<T> type, Class<?>... parameterTypes) {
		try {
			Constructor<T> ctor = type.getDeclaredConstructor(parameterTypes);
			// Constructor<T> ctor = type.getConstructor(parameterTypes);
			ctor.setAccessible(true);
			return ctor;
		} catch (Throwable t) {
			return JqwikExceptionSupport.throwAsUncheckedException(t);
		}
	}

	private static Predicate<Method> isGeneratorMethod(TypeUsage expectedReturnType, Class<? extends Annotation> requiredAnnotation) {
		return method -> {
			if (!findDeclaredOrInheritedAnnotation(method, requiredAnnotation).isPresent()) {
				return false;
			}
			TypeUsage generatorReturnType = TypeUsage.forType(method.getAnnotatedReturnType().getType());
			return generatorReturnType.canBeAssignedTo(expectedReturnType);
		};
	}

	public static boolean isInnerClass(Class<?> clazz) {
		return clazz.isMemberClass() && !ModifierSupport.isStatic(clazz);
	}

	public static boolean isFunctionalType(Class<?> candidateType) {
		if (!candidateType.isInterface()) {
			return false;
		}
		return countInterfaceMethods(candidateType) == 1;
	}

	private static long countInterfaceMethods(Class<?> candidateType) {
		Method[] methods = candidateType.getMethods();
		return findInterfaceMethods(methods).size();
	}

	private static List<Method> findInterfaceMethods(Method[] methods) {
		return Arrays
				   .stream(methods)
				   .filter(m -> !m.isDefault() && !ModifierSupport.isStatic(m))
				   .collect(Collectors.toList());
	}

	public static Optional<Method> getFunctionMethod(Class<?> candidateType) {
		Method[] methods = candidateType.getMethods();
		List<Method> candidates = findInterfaceMethods(methods);
		if (candidates.size() != 1) {
			return Optional.empty();
		}
		return Optional.of(candidates.get(0));
	}

	public static boolean isEqualsMethod(Method method) {
		try {
			return method.equals(Object.class.getDeclaredMethod("equals", Object.class));
		} catch (NoSuchMethodException shouldNeverHappen) {
			return false;
		}
	}

	public static boolean isToStringMethod(Method method) {
		try {
			return method.equals(Object.class.getDeclaredMethod("toString"));
		} catch (NoSuchMethodException shouldNeverHappen) {
			return false;
		}
	}

	public static boolean isHashCodeMethod(Method method) {
		try {
			return method.equals(Object.class.getDeclaredMethod("hashCode"));
		} catch (NoSuchMethodException shouldNeverHappen) {
			return false;
		}
	}

	public static boolean hasDefaultConstructor(Class<?> aClass) {
		return hasConstructor(aClass);
	}

	public static boolean hasConstructor(Class<?> aClass, Class<?>... parameterTypes) {
		try {
			aClass.getDeclaredConstructor(parameterTypes);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public static boolean isJava9orAbove() {
		try {
			//noinspection JavaReflectionMemberAccess
			Runtime.class.getMethod("version");
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public static boolean isStatic(Class<?> clazz) {
		return Modifier.isStatic(clazz.getModifiers());
	}

	public static boolean isPrivate(Class<?> clazz) {
		return Modifier.isPrivate(clazz.getModifiers());
	}

	public static boolean isNotStatic(Class<?> clazz) {
		return !isStatic(clazz);
	}

	public static boolean isStatic(Member member) {
		return Modifier.isStatic(member.getModifiers());
	}

	public static boolean isNotStatic(Member member) {
		return !isStatic(member);
	}

	public static boolean returnsVoid(Method method) {
		return method.getReturnType().equals(Void.TYPE);
	}

	public static boolean implementsMethod(
		Class<?> aClass,
		String methodName,
		Class<?>[] parameterTypes,
		Class<?> ignoreImplementationClass
	) {
		Optional<Method> optionalMethod = ReflectionSupport.findMethod(aClass, methodName, parameterTypes);
		return optionalMethod.map(method -> !method.getDeclaringClass().equals(ignoreImplementationClass)).orElse(false);
	}

	public static Class<?> extractRawType(Type parameterizedType) {
		if (parameterizedType instanceof Class) {
			return (Class<?>) parameterizedType;
		}
		if (parameterizedType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) parameterizedType).getRawType();
		}
		if (parameterizedType instanceof GenericArrayType) {
			return Object[].class;
		}
		// Now we have a type variable (java.lang.reflect.TypeVariable)
		return Object.class;
	}

}
