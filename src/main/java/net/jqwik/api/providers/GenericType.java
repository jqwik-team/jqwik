package net.jqwik.api.providers;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 * An instance of {@code GenericType} describes the information available for parameter or return types.
 * The class is supposed to relieve its users from all the intricacies of the Java reflection API.
 * Doing that it will resolve meta annotations, repeated annotations as well as annotated type parameters.
 * <p>
 * {@code GenericType} provides access to:
 * <ul>
 * <li>the native type of an object</li>
 * <li>the component type (if it's an array)</li>
 * <li>the type parameters (again as instances of {@code GenericType})</li>
 * <li>the annotations (if the object is derived from a parameter)</li>
 * <li>methods to test for compatibility of types that do also handle compatibility
 * between raw types and boxed type</li>
 * </ul>
 * <p>
 * Within the public API {@code GenericType} is used in two places:
 * <ul>
 * <li>@see {@link ArbitraryProvider}</li>
 * <li>@see {@link Arbitraries#defaultFor(GenericType)}</li>
 * </ul>
 */
public class GenericType {

	private static final String WILDCARD = "?";

	public static GenericType of(Class<?> type, GenericType... typeParameters) {
		if (typeParameters.length > 0 && typeParameters.length != type.getTypeParameters().length) {
			String typeArgumentsString = JqwikStringSupport.displayString(typeParameters);
			throw new JqwikException(String.format("Type [%s] cannot have type parameters [%s]", type, typeArgumentsString));
		}
		return new GenericType(type, typeParameters);
	}

	public static GenericType forParameter(MethodParameter parameter) {
		return new GenericType(parameter);
	}

	public static GenericType forType(Type type) {
		if (type instanceof WildcardType) {
			return GenericType.forWildcard((WildcardType) type);
		}
		return new GenericType(type);
	}

	private static GenericType forWildcard(WildcardType wildcardType) {
		return new GenericType(
			Object.class,
			WILDCARD,
			extractUpperBounds(wildcardType),
			extractLowerBounds(wildcardType),
			Collections.emptyList(),
			extractAnnotations(wildcardType)
		);
	}

	private final Class<?> rawType;
	private final List<Annotation> annotations;
	private final List<GenericType> typeArguments;
	private final String typeVariable;
	private final GenericType[] upperBounds;
	private final GenericType[] lowerBounds;

	private GenericType(Class<?> rawType, GenericType... typeArguments) {
		this(
			rawType,
			null,
			new GenericType[0],
			new GenericType[0],
			Arrays.asList(typeArguments),
			Collections.emptyList()
		);
	}

	private GenericType(AnnotatedType annotatedType) {
		this(
			extractRawType(annotatedType.getType()),
			extractTypeVariable(annotatedType.getType()),
			extractUpperBounds(annotatedType.getType()),
			extractLowerBounds(annotatedType.getType()),
			extractPlainTypeArguments(annotatedType),
			extractAnnotations(annotatedType)
		);
	}

	private GenericType(MethodParameter parameter) {
		this(
			parameter.getType(),
			extractTypeVariable(parameter.getParameterizedType()),
			extractUpperBounds(parameter.getParameterizedType()),
			extractLowerBounds(parameter.getParameterizedType()),
			extractTypeArguments(parameter),
			parameter.findAllAnnotations()
		);
	}

	private GenericType(Type parameterizedType) {
		this(
			extractRawType(parameterizedType),
			extractTypeVariable(parameterizedType),
			extractUpperBounds(parameterizedType),
			extractLowerBounds(parameterizedType),
			extractPlainTypeArguments(parameterizedType),
			Collections.emptyList()
		);
	}

	private GenericType(
		Class<?> rawType,
		String typeVariable,
		GenericType[] upperBounds,
		GenericType[] lowerBounds,
		List<GenericType> typeArguments,
		List<Annotation> annotations
	) {
		this.rawType = rawType;
		this.typeVariable = typeVariable;
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
		this.typeArguments = typeArguments;
		this.annotations = annotations;
	}

	private static List<GenericType> extractTypeArguments(MethodParameter parameter) {
		if (parameter.isAnnotatedParameterized()) {
			return extractAnnotatedTypeArguments(parameter.getAnnotatedType());
		} else {
			return extractPlainTypeArguments(parameter.getParameterizedType());
		}
	}

	private static List<Annotation> extractAnnotations(Object parameterizedType) {
		if (parameterizedType instanceof AnnotatedElement)
			return JqwikAnnotationSupport.findAllAnnotations((AnnotatedElement) parameterizedType);
		return Collections.emptyList();
	}

	private static String extractTypeVariable(Type parameterizedType) {
		if (parameterizedType instanceof WildcardType) {
			return WILDCARD;
		}
		if (parameterizedType instanceof TypeVariable) {
			return ((TypeVariable) parameterizedType).getName();
		}
		return null;
	}

	private static GenericType[] extractUpperBounds(Type parameterizedType) {
		if (parameterizedType instanceof TypeVariable) {
			return Arrays.stream(((TypeVariable) parameterizedType).getBounds()) //
						 .map(GenericType::forType) //
						 .toArray(GenericType[]::new);
		}
		if (parameterizedType instanceof WildcardType) {
			return extractUpperBounds((WildcardType) parameterizedType);
		}
		return new GenericType[0];
	}

	private static GenericType[] extractUpperBounds(WildcardType wildcardType) {
		return Arrays.stream(wildcardType.getUpperBounds())
					 .map(GenericType::forType)
					 .toArray(GenericType[]::new);
	}

	private static GenericType[] extractLowerBounds(Type parameterizedType) {
		if (parameterizedType instanceof WildcardType) {
			return extractLowerBounds((WildcardType) parameterizedType);
		}
		return new GenericType[0];
	}

	private static GenericType[] extractLowerBounds(WildcardType wildcardType) {
		return Arrays.stream(wildcardType.getLowerBounds())
					 .map(GenericType::forType)
					 .toArray(GenericType[]::new);
	}

	private static Class<?> extractRawType(Type parameterizedType) {
		if (parameterizedType instanceof Class) {
			return (Class) parameterizedType;
		}
		if (parameterizedType instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) parameterizedType).getRawType();
		}
		// Now we have a type variable (java.lang.reflect.TypeVariable)
		return Object.class;
	}

	private static List<GenericType> extractPlainTypeArguments(Object parameterizedType) {
		if (parameterizedType instanceof AnnotatedParameterizedType) {
			return extractAnnotatedTypeArguments((AnnotatedParameterizedType) parameterizedType);
		}
		if (parameterizedType instanceof ParameterizedType) {
			return Arrays.stream(((ParameterizedType) parameterizedType).getActualTypeArguments()) //
						 .map(GenericType::forType) //
						 .collect(Collectors.toList());
		}
		// Now it's either not a generic type or it has type variables
		return Collections.emptyList();
	}

	private static List<GenericType> extractAnnotatedTypeArguments(AnnotatedParameterizedType annotatedType) {
		return Arrays.stream(annotatedType.getAnnotatedActualTypeArguments()) //
					 .map(GenericType::new) //
					 .collect(Collectors.toList());
	}

	/**
	 * Return the raw type which is usually the class or interface you see in a parameters or return values
	 * specification.
	 * <p>
	 * A raw type always exists.
	 */
	public Class<?> getRawType() {
		return rawType;
	}

	/**
	 * Return true if a type parameter has upper bounds.
	 */
	public boolean hasUpperBounds() {
		if (upperBounds.length > 1)
			return true;
		return upperBounds.length == 1 && !upperBounds[0].isOfType(Object.class);
	}

	/**
	 * Return true if a type parameter has upper bounds.
	 */
	public boolean hasLowerBounds() {
		return lowerBounds.length > 0;
	}

	/**
	 * Return true if a generic type is a wildcard.
	 */
	public boolean isWildcard() {
		return typeVariable != null && typeVariable.equals(WILDCARD);
	}

	/**
	 * Return true if a generic type is a wildcard.
	 */
	public boolean isTypeVariable() {
		return typeVariable != null && !isWildcard();
	}

	/**
	 * Return true if a generic type is a type variable or a wildcard.
	 */
	public boolean isTypeVariableOrWildcard() {
		return isWildcard() || isTypeVariable();
	}

	/**
	 * Return the type arguments of a generic type in the order of there appearance in a type's declaration.
	 */
	public List<GenericType> getTypeArguments() {
		return typeArguments;
	}

	/**
	 * Check if an instance is of a specific raw type
	 *
	 * Most of the time this is what you want to do when checking for applicability of a
	 * {@linkplain ArbitraryProvider}.
	 */
	public boolean isOfType(Class<?> aRawType) {
		if (isTypeVariableOrWildcard())
			return false;
		return rawType == aRawType;
	}

	/**
	 * Check if an instance can be assigned to another {@code GenericType} instance.
	 */
	public boolean canBeAssignedTo(GenericType targetType) {
		if (targetType.isTypeVariableOrWildcard()) {
			return canBeAssignedToUpperBounds(targetType) && canBeAssignedToLowerBounds(targetType);
		}
		if (boxedTypeMatches(targetType.rawType, this.rawType))
			return true;
		if (boxedTypeMatches(this.rawType, targetType.rawType))
			return true;
		if (targetType.getRawType().isAssignableFrom(rawType)) {
			if (allTypeArgumentsCanBeAssigned(this.getTypeArguments(), targetType.getTypeArguments())) {
				return true;
			} else {
				// TODO: This is too loose since it potentially allows not matching types
				// which will lead to class cast exception during property execution
				return findSuperType(targetType.rawType).isPresent();
			}
		}
		return false;
	}

	private boolean canBeAssignedToUpperBounds(GenericType targetType) {
		if (isTypeVariableOrWildcard()) {
			return Arrays.stream(upperBounds).allMatch(upperBound -> upperBound.canBeAssignedToUpperBounds(targetType));
		}
		return Arrays.stream(targetType.upperBounds).allMatch(this::canBeAssignedTo);
	}

	private boolean canBeAssignedToLowerBounds(GenericType targetType) {
		if (isTypeVariableOrWildcard()) {
			return Arrays.stream(lowerBounds).allMatch(lowerBound -> lowerBound.canBeAssignedToLowerBounds(targetType));
		}
		return Arrays.stream(targetType.lowerBounds).allMatch(lowerBound -> lowerBound.canBeAssignedTo(this));
	}

	private boolean allTypeArgumentsCanBeAssigned(
		List<GenericType> providedTypeArguments, List<GenericType> targetTypeArguments
	) {
		if (providedTypeArguments.size() == 0) {
			return true;
		}
		if (targetTypeArguments.size() == 0) {
			return true;
		}
		if (targetTypeArguments.size() != providedTypeArguments.size())
			return false;
		for (int i = 0; i < targetTypeArguments.size(); i++) {
			GenericType providedTypeArgument = providedTypeArguments.get(i);
			GenericType targetTypeArgument = targetTypeArguments.get(i);
			if (!providedTypeArgument.canBeAssignedTo(targetTypeArgument))
				return false;
		}
		return true;
	}

	/**
	 * Return true if a type has any type arguments itself.
	 */
	public boolean isGeneric() {
		return typeArguments.size() > 0;
	}

	/**
	 * Return true if a type is an {@code enum} type.
	 */
	public boolean isEnum() {
		return getRawType().isEnum();
	}

	/**
	 * Return true if a type is an array type.
	 */
	public boolean isArray() {
		return getRawType().isArray();
	}

	/**
	 * Return all annotations of a parameter (or an annotated type argument).
	 *
	 * This list already contains all meta annotations, repeated annotations and annotations
	 * from annotated type arguments. Thus, it does much more than the usual Java reflection API.
	 */
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	/**
	 * Return an {@code Optional} of the first instance of a specific {@code annotationType} if there is one.
	 */
	public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType) {
		return annotations.stream()
						  .filter(annotation -> annotation.annotationType().equals(annotationType))
						  .map(annotationType::cast)
						  .findFirst();
	}

	/**
	 * Check if a given {@code providedClass} is assignable from this generic type.
	 */
	public boolean isAssignableFrom(Class<?> providedClass) {
		return GenericType.of(providedClass).canBeAssignedTo(this);
	}

	/**
	 * Return an {@code Optional} of an array's component type - if it is an array.
	 */
	public Optional<GenericType> getComponentType() {
		Class<?> componentType = rawType.getComponentType();
		if (componentType != null)
			return Optional.of(GenericType.of(componentType));
		return Optional.empty();
	}

	private boolean boxedTypeMatches(Class<?> providedType, Class<?> targetType) {
		if (providedType.equals(Long.class) && targetType.equals(long.class))
			return true;
		if (providedType.equals(Integer.class) && targetType.equals(int.class))
			return true;
		if (providedType.equals(Short.class) && targetType.equals(short.class))
			return true;
		if (providedType.equals(Byte.class) && targetType.equals(byte.class))
			return true;
		if (providedType.equals(Character.class) && targetType.equals(char.class))
			return true;
		if (providedType.equals(Double.class) && targetType.equals(double.class))
			return true;
		if (providedType.equals(Float.class) && targetType.equals(float.class))
			return true;
		return providedType.equals(Boolean.class) && targetType.equals(boolean.class);
	}

	public Optional<GenericType> findSuperType(Class<?> typeToFind) {
		return findSuperTypeIn(typeToFind, this.rawType);
	}

	private Optional<GenericType> findSuperTypeIn(Class<?> typeToFind, Class<?> rawType) {
		List<AnnotatedType> supertypes = new ArrayList<>();
		if (rawType.getSuperclass() != null)
			supertypes.add(rawType.getAnnotatedSuperclass());
		supertypes.addAll(Arrays.asList(rawType.getAnnotatedInterfaces()));
		for (AnnotatedType type : supertypes) {
			if (extractRawType(type.getType()).equals(typeToFind))
				return Optional.of(new GenericType(type));
		}

		for (AnnotatedType type : supertypes) {
			GenericType genericType = new GenericType(type);
			Optional<GenericType> nestedFound = genericType.findSuperType(typeToFind);
			if (nestedFound.isPresent())
				return nestedFound;
		}

		return Optional.empty();
	}

	@Override
	public String toString() {
		String representation = getRawType().getSimpleName();
		if (isGeneric()) {
			String typeArgsRepresentation = typeArguments.stream() //
														 .map(GenericType::toString) //
														 .collect(Collectors.joining(", "));
			representation = String.format("%s<%s>", representation, typeArgsRepresentation);
		}
		if (isArray()) {
			representation = String.format("%s[]", getComponentType().get().toString());
		}
		if (typeVariable != null) {
			representation = typeVariable;
			if (hasUpperBounds()) {
				String boundsRepresentation =
					Arrays.stream(upperBounds) //
						  .map(GenericType::toString) //
						  .collect(Collectors.joining(" & "));
				representation += String.format(" extends %s", boundsRepresentation);
			}
			if (hasLowerBounds()) {
				String boundsRepresentation =
					Arrays.stream(lowerBounds) //
						  .map(GenericType::toString) //
						  .collect(Collectors.joining(" & "));
				representation += String.format(" super %s", boundsRepresentation);
			}
		}
		if (!annotations.isEmpty()) {
			String annotationRepresentation = annotations.stream()
														 .map(Annotation::toString)
														 .collect(Collectors.joining(" "));
			representation = annotationRepresentation + " " + representation;
		}
		return representation;
	}

}
