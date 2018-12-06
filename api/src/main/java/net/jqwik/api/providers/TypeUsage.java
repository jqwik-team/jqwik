package net.jqwik.api.providers;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

/**
 * An instance of {@code TypeUsage} describes the information available for parameter or return types.
 * The class is supposed to relieve its users from all the intricacies of the Java reflection API.
 * Doing that it will resolve meta annotations, repeated annotations as well as annotated type parameters.
 * <p>
 * {@code TypeUsage} provides access to:
 * <ul>
 * <li>the native type of an object</li>
 * <li>the component type (if it's an array)</li>
 * <li>the type parameters (again as instances of {@code TypeUsage})</li>
 * <li>the annotations (if the object is derived from a parameter)</li>
 * <li>methods to test for compatibility of types that do also handle compatibility
 * between raw types and boxed type</li>
 * </ul>
 * <p>
 * Within the public API {@code TypeUsage} is used in two places:
 * <ul>
 * <li>@see {@link ArbitraryProvider}</li>
 * <li>@see {@link Arbitraries#defaultFor(Class, Class[])}</li>
 * </ul>
 */
public abstract class TypeUsage {

	private static TypeUsageFacade facade;

	static  {
		try {
			facade = (TypeUsageFacade) Class.forName("net.jqwik.engine.facades.TypeUsageFacadeImpl").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface TypeUsageFacade {
		TypeUsage of(Class<?> type, TypeUsage... typeParameters);
		TypeUsage wildcard(TypeUsage upperBound);
		TypeUsage forType(Type type);
	}

	public static TypeUsage of(Class<?> type, TypeUsage... typeParameters) {
		return facade.of(type, typeParameters);
	}

	public static TypeUsage wildcard(TypeUsage upperBound) {
		return facade.wildcard(upperBound);
	}

	public static TypeUsage forType(Type type) {
		return facade.forType(type);
	}

	/**
	 * Return the raw type which is usually the class or interface you see in a parameters or return values
	 * specification.
	 * <p>
	 * A raw type always exists.
	 */
	public abstract Class<?> getRawType();

	/**
	 * Return upper bounds if a generic type is a wildcard.
	 */
	public abstract List<TypeUsage> getUpperBounds();

	/**
	 * Return lower bounds if a generic type is a wildcard.
	 */
	public abstract List<TypeUsage> getLowerBounds();

	/**
	 * Return true if a generic type is a wildcard.
	 */
	public abstract boolean isWildcard();

	/**
	 * Return true if a generic type is a wildcard.
	 */
	public abstract boolean isTypeVariable();

	/**
	 * Return true if a generic type is a type variable or a wildcard.
	 */
	public abstract boolean isTypeVariableOrWildcard();

	/**
	 * Return the type arguments of a generic type in the order of there appearance in a type's declaration.
	 */
	public abstract List<TypeUsage> getTypeArguments();

	/**
	 * Check if an instance is of a specific raw type
	 * <p>
	 * Most of the time this is what you want to do when checking for applicability of a
	 * {@linkplain ArbitraryProvider}.
	 */
	public abstract boolean isOfType(Class<?> aRawType);

	/**
	 * Check if an instance can be assigned to another {@code TypeUsage} instance.
	 */
	public abstract boolean canBeAssignedTo(TypeUsage targetType);

	/**
	 * Return true if a type has any type arguments itself.
	 */
	public abstract boolean isGeneric();

	/**
	 * Return true if a type is an {@code enum} type.
	 */
	public abstract boolean isEnum();

	/**
	 * Return true if a type is an array type.
	 */
	public abstract boolean isArray();

	/**
	 * Return all annotations of a parameter (or an annotated type argument).
	 * <p>
	 * This list already contains all meta annotations, repeated annotations and annotations
	 * from annotated type arguments. Thus, it does much more than the usual Java reflection API.
	 */
	public abstract List<Annotation> getAnnotations();

	/**
	 * Return an {@code Optional} of the first instance of a specific {@code annotationType}
	 * if there is one (directly or indirectly through meta-annotations).
	 */
	public abstract <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType);

	/**
	 * Return true if the current instance is annotated (directly or indirectly through meta-annotations)
	 * with a specific {@code annotationType}.
	 */
	public abstract <A extends Annotation> boolean isAnnotated(Class<A> annotationType);

	/**
	 * Check if a given {@code providedClass} is assignable from this generic type.
	 */
	public abstract boolean isAssignableFrom(Class<?> providedClass);

	/**
	 * Return an {@code Optional} of an array's component type - if it is an array.
	 */
	public abstract Optional<TypeUsage> getComponentType();

}
