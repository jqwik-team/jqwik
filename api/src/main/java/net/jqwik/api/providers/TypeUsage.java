package net.jqwik.api.providers;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

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
@API(status = MAINTAINED, since = "1.0")
public interface TypeUsage {

	@API(status = INTERNAL)
	TypeUsage OBJECT_TYPE = TypeUsage.of(Object.class);

	@API(status = INTERNAL)
	abstract class TypeUsageFacade {
		private static final TypeUsageFacade implementation;

		static {
			implementation = FacadeLoader.load(TypeUsageFacade.class);
		}

		public abstract TypeUsage of(Class<?> type, TypeUsage... typeParameters);

		public abstract TypeUsage wildcardOf(TypeUsage upperBound);

		public abstract TypeUsage forType(Type type);
	}

	/**
	 * Enhancers can manipulate the perceived type of parameters.
	 * They must be registered through Java Service Registration.
	 *
	 * <p>
	 * Currently used in Kotlin module to add nullability information.
	 * </p>
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	interface Enhancer {
		default TypeUsage forParameter(TypeUsage original, Parameter parameter) {
			return original;
		}
	}

	static TypeUsage of(Class<?> type, TypeUsage... typeParameters) {
		return TypeUsageFacade.implementation.of(type, typeParameters);
	}

	static TypeUsage wildcard(TypeUsage upperBound) {
		return TypeUsageFacade.implementation.wildcardOf(upperBound);
	}

	static TypeUsage forType(Type type) {
		return TypeUsageFacade.implementation.forType(type);
	}

	/**
	 * Return the raw type which is usually the class or interface you see in a parameters or return values
	 * specification.
	 * <p>
	 * A raw type always exists.
	 */
	Class<?> getRawType();

	/**
	 * Return upper bounds if a generic type is a wildcard or type variable.
	 * {@code TypeUsage.of(Object.class)} is always included.
	 */
	List<TypeUsage> getUpperBounds();

	/**
	 * Return lower bounds if a generic type is a wildcard.
	 */
	List<TypeUsage> getLowerBounds();

	/**
	 * Return true if a generic type is a wildcard.
	 */
	boolean isWildcard();

	/**
	 * Return true if a generic type is a wildcard.
	 */
	boolean isTypeVariable();

	/**
	 * Return true if a generic type is a type variable or a wildcard.
	 */
	boolean isTypeVariableOrWildcard();

	/**
	 * Return the type arguments of a generic type in the order of there appearance in a type's declaration.
	 */
	List<TypeUsage> getTypeArguments();

	/**
	 * Return the type argument at a specific position.
	 */
	TypeUsage getTypeArgument(int position);

	/**
	 * Check if an instance is of a specific raw type
	 * <p>
	 * Most of the time this is what you want to do when checking for applicability of a
	 * {@linkplain ArbitraryProvider}.
	 */
	boolean isOfType(Class<?> aRawType);

	/**
	 * Check if an instance can be assigned to another {@code TypeUsage} instance.
	 */
	boolean canBeAssignedTo(TypeUsage targetType);

	/**
	 * Return true if a type has any type arguments itself.
	 */
	boolean isGeneric();

	/**
	 * Return true if a type is an {@code enum} type.
	 */
	boolean isEnum();

	/**
	 * Return true if a type is an array type.
	 */
	boolean isArray();

	/**
	 * Return all annotations of a parameter (or an annotated type argument).
	 * <p>
	 * This list already contains all meta annotations, repeated annotations and annotations
	 * from annotated type arguments. Thus, it does much more than the usual Java reflection API.
	 */
	List<Annotation> getAnnotations();

	/**
	 * Return an {@code Optional} of the first instance of a specific {@code annotationType}
	 * if there is one (directly or indirectly through meta-annotations).
	 */
	<A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType);

	/**
	 * Return true if the current instance is annotated (directly or indirectly through meta-annotations)
	 * with a specific {@code annotationType}.
	 */
	<A extends Annotation> boolean isAnnotated(Class<A> annotationType);

	/**
	 * Check if a given {@code providedClass} is assignable from this generic type.
	 */
	boolean isAssignableFrom(Class<?> providedClass);

	/**
	 * Return an {@code Optional} of an array's component type - if it is an array.
	 */
	Optional<TypeUsage> getComponentType();

	/**
	 * Return true if type is Void.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.0")
	boolean isVoid();

	/**
	 * Return superclass of this type. With all type variables resolved.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.0")
	Optional<TypeUsage> getSuperclass();

	/**
	 * List of superclass and interfaces. With all type variables resolved.
	 */
	@API(status = EXPERIMENTAL, since = "1.8.0")
	List<TypeUsage> getSuperTypes();

	/**
	 * Return interfaces of this type. With all type variables resolved.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.0")
	List<TypeUsage> getInterfaces();

	@API(status = INTERNAL, since = "1.2.0")
	Type getType();

	@API(status = INTERNAL, since = "1.2.0")
	AnnotatedType getAnnotatedType();

	/**
	 * Return optional true if this type is nullable.
	 *
	 * <p>
	 * Plain Java types are never nullable.
	 * Nullability must be set explicitly using {@linkplain #asNullable()}.
	 * This is usually done in registered {@linkplain TypeUsage.Enhancer enhancers}.
	 * </p>
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	boolean isNullable();

	/**
	 * Return true if it is a parameterized type with no parameters, e.g. {@code List}.
	 */
	@API(status = EXPERIMENTAL, since = "1.8.0")
	boolean isParameterizedRaw();

	/**
	 * Return true if it is a wildcard with super constraint, e.g. {@code ? super String}.
	 */
	@API(status = EXPERIMENTAL, since = "1.8.0")
	boolean isSuperWildcard();

	/**
	 * Return true if it is a wildcard with extends constraint, e.g. {@code ? extends String}.
	 */
	@API(status = EXPERIMENTAL, since = "1.8.0")
	boolean isExtendsConstraint();

	/**
	 * Return type usage object with just nullablity set to true
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	TypeUsage asNullable();

	/**
	 * Return type usage object with just nullablity set to false
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	TypeUsage asNotNullable();

	@API(status = EXPERIMENTAL, since = "1.6.0")
	String getTypeVariable();

	/**
	 * Return type usage object with additional annotation
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	<A extends Annotation> TypeUsage withAnnotation(A annotation);

	/**
	 * Get meta info for a certain key
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	Optional<Object> getMetaInfo(String key);

	/**
	 * Return type usage object with additional meta info
	 */
	@API(status = EXPERIMENTAL, since = "1.6.0")
	TypeUsage withMetaInfo(String key, Object value);


	/**
	 * Check for type equality, ie do not consider annotations.
	 */
	@API(status = EXPERIMENTAL, since = "1.9.0")
	boolean hasSameTypeAs(TypeUsage other);
}
