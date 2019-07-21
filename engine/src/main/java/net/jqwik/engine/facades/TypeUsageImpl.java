package net.jqwik.engine.facades;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class TypeUsageImpl implements TypeUsage {

	private static Map<Type, TypeUsageImpl> resolved = new ConcurrentHashMap<>();

	static final String WILDCARD = "?";

	public static TypeUsage forParameter(MethodParameter parameter) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			extractRawType(parameter.getType()),
			parameter.getType(),
			extractTypeVariable(parameter.getType()),
			parameter.findAllAnnotations()
		);
		typeUsage.addTypeArguments(extractTypeArguments(parameter));
		typeUsage.addUpperBounds(extractUpperBounds(parameter.getType()));
		typeUsage.addLowerBounds(extractLowerBounds(parameter.getType()));

		return typeUsage;
	}

	static TypeUsageImpl forParameterizedType(Type parameterizedType) {
		return resolveOrCreate(
			parameterizedType,
			extractRawType(parameterizedType),
			extractTypeVariable(parameterizedType),
			Collections.emptyList(),
			typeUsage -> {
				typeUsage.addTypeArguments(extractPlainTypeArguments(parameterizedType));
				typeUsage.addUpperBounds(extractUpperBounds(parameterizedType));
				typeUsage.addLowerBounds(extractLowerBounds(parameterizedType));
			}
		);
	}

	static TypeUsageImpl forWildcard(WildcardType wildcardType) {
		return resolveOrCreate(
			wildcardType,
			Object.class,
			WILDCARD,
			extractAnnotations(wildcardType),
			typeUsage -> {
				typeUsage.addUpperBounds(extractUpperBounds(wildcardType));
				typeUsage.addLowerBounds(extractLowerBounds(wildcardType));
			}
		);
	}

	private static TypeUsageImpl resolveOrCreate(
		Type type,
		Class rawType,
		String typeVariable,
		List<Annotation> annotations,
		Consumer<TypeUsageImpl> processTypeUsage
	) {
		Optional<TypeUsageImpl> alreadyResolved = alreadyResolvedIn(type);
		if (alreadyResolved.isPresent()) {
			return alreadyResolved.get();
		}

		TypeUsageImpl typeUsage = new TypeUsageImpl(rawType, type, typeVariable, annotations);
		resolved.put(type, typeUsage);
		processTypeUsage.accept(typeUsage);

		return typeUsage;
	}

	private static TypeUsageImpl forAnnotatedType(AnnotatedType annotatedType) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			extractRawType(annotatedType.getType()),
			annotatedType.getType(),
			extractTypeVariable(annotatedType.getType()),
			extractAnnotations(annotatedType)
		);
		typeUsage.addTypeArguments(extractPlainTypeArguments(annotatedType));
		typeUsage.addUpperBounds(extractUpperBounds(annotatedType.getType()));
		typeUsage.addLowerBounds(extractLowerBounds(annotatedType.getType()));
		return typeUsage;
	}

	private static Optional<TypeUsageImpl> alreadyResolvedIn(Type type) {
		return Optional.ofNullable(resolved.get(type));
	}

	private static List<TypeUsage> extractTypeArguments(MethodParameter parameter) {
		if (parameter.isAnnotatedParameterized()) {
			return extractAnnotatedTypeArguments(parameter.getAnnotatedType());
		} else {
			return extractPlainTypeArguments(parameter.getType());
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

	private static List<TypeUsage> extractUpperBounds(Type parameterizedType) {
		if (parameterizedType instanceof TypeVariable) {
			return Arrays.stream(((TypeVariable) parameterizedType).getBounds())
						 .map(TypeUsage::forType)
						 .collect(Collectors.toList());
		}
		if (parameterizedType instanceof WildcardType) {
			return extractUpperBounds((WildcardType) parameterizedType);
		}
		return new ArrayList<>();
	}

	private static List<TypeUsage> extractUpperBounds(WildcardType wildcardType) {
		return Arrays.stream(wildcardType.getUpperBounds())
					 .map(TypeUsage::forType)
					 .collect(Collectors.toList());
	}

	private static List<TypeUsage> extractLowerBounds(Type parameterizedType) {
		if (parameterizedType instanceof WildcardType) {
			return extractLowerBounds((WildcardType) parameterizedType);
		}
		return Collections.emptyList();
	}

	private static List<TypeUsage> extractLowerBounds(WildcardType wildcardType) {
		return Arrays.stream(wildcardType.getLowerBounds())
					 .map(TypeUsage::forType)
					 .collect(Collectors.toList());
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

	private static List<TypeUsage> extractPlainTypeArguments(Object parameterizedType) {
		if (parameterizedType instanceof AnnotatedParameterizedType) {
			return extractAnnotatedTypeArguments((AnnotatedParameterizedType) parameterizedType);
		}
		if (parameterizedType instanceof ParameterizedType) {
			return Arrays.stream(((ParameterizedType) parameterizedType).getActualTypeArguments()) //
						 .map(TypeUsage::forType) //
						 .collect(Collectors.toList());
		}
		// Now it's either not a generic type or it has type variables
		return Collections.emptyList();
	}

	private static List<TypeUsage> extractAnnotatedTypeArguments(AnnotatedParameterizedType annotatedType) {
		return Arrays.stream(annotatedType.getAnnotatedActualTypeArguments()) //
					 .map(TypeUsageImpl::forAnnotatedType) //
					 .collect(Collectors.toList());
	}

	private final Class<?> rawType;
	private final Type type;
	private final String typeVariable;
	private final List<Annotation> annotations;
	private final List<TypeUsage> typeArguments = new ArrayList<>();

	private final List<TypeUsage> upperBounds = new ArrayList<>();
	private final List<TypeUsage> lowerBounds = new ArrayList<>();

	TypeUsageImpl(Class<?> rawType, Type type, String typeVariable, List<Annotation> annotations) {
		if (rawType == null) {
			throw new IllegalArgumentException("rawType must never be null");
		}
		this.rawType = rawType;
		this.type = type;
		this.typeVariable = typeVariable;
		this.annotations = annotations;
	}

	void addTypeArguments(List<TypeUsage> typeArguments) {
		this.typeArguments.addAll(typeArguments);
	}

	void addLowerBounds(List<TypeUsage> lowerBounds) {
		this.lowerBounds.addAll(lowerBounds);
	}

	void addUpperBounds(List<TypeUsage> upperBounds) {
		this.upperBounds.addAll(upperBounds);
	}

	@Override
	public List<TypeUsage> getUpperBounds() {
		return upperBounds;
	}

	@Override
	public List<TypeUsage> getLowerBounds() {
		return lowerBounds;
	}

	@Override
	public Class<?> getRawType() {
		return rawType;
	}

	private boolean hasUpperBoundBeyondObject() {
		if (upperBounds.size() > 1)
			return true;
		return upperBounds.size() == 1 && !upperBounds.get(0).isOfType(Object.class);
	}

	private boolean hasLowerBounds() {
		return lowerBounds.size() > 0;
	}

	@Override
	public boolean isWildcard() {
		return typeVariable != null && typeVariable.equals(WILDCARD);
	}

	@Override
	public boolean isTypeVariable() {
		return typeVariable != null && !isWildcard();
	}

	@Override
	public boolean isTypeVariableOrWildcard() {
		return isWildcard() || isTypeVariable();
	}

	@Override
	public List<TypeUsage> getTypeArguments() {
		return typeArguments;
	}

	@Override
	public TypeUsage getTypeArgument(int position) {
		return getTypeArguments().size() <= position ?
				   TypeUsage.forType(Object.class)
				   : getTypeArguments().get(position);
	}

	@Override
	public boolean isOfType(Class<?> aRawType) {
		if (isTypeVariableOrWildcard())
			return false;
		return rawType == aRawType;
	}

	@Override
	public boolean canBeAssignedTo(TypeUsage targetType) {
		if (targetType.isTypeVariableOrWildcard()) {
			return canBeAssignedToUpperBounds(this, targetType) && canBeAssignedToLowerBounds(this, targetType);
		}
		if (boxedTypeMatches(targetType.getRawType(), this.rawType))
			return true;
		if (boxedTypeMatches(this.rawType, targetType.getRawType()))
			return true;
		if (targetType.getRawType().isAssignableFrom(rawType)) {
			if (allTypeArgumentsCanBeAssigned(this.getTypeArguments(), targetType.getTypeArguments())) {
				return true;
			} else {
				// TODO: This is too loose since it potentially allows not matching types
				// which will lead to class cast exception during property execution
				return findSuperType(targetType.getRawType()).isPresent();
			}
		}
		return false;
	}

	private static boolean canBeAssignedToUpperBounds(TypeUsage sourceType, TypeUsage targetType) {
		if (sourceType.isTypeVariableOrWildcard()) {
			return sourceType.getUpperBounds().stream().allMatch(upperBound -> canBeAssignedToUpperBounds(upperBound, targetType));
		}
		return targetType.getUpperBounds().stream().allMatch(sourceType::canBeAssignedTo);
	}

	private static boolean canBeAssignedToLowerBounds(TypeUsage sourceType, TypeUsage targetType) {
		if (sourceType.isTypeVariableOrWildcard()) {
			return sourceType.getLowerBounds().stream().allMatch(lowerBound -> canBeAssignedToLowerBounds(lowerBound, targetType));
		}
		return targetType.getLowerBounds().stream().allMatch(lowerBound -> lowerBound.canBeAssignedTo(sourceType));
	}

	private boolean allTypeArgumentsCanBeAssigned(
		List<TypeUsage> providedTypeArguments, List<TypeUsage> targetTypeArguments
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
			TypeUsage providedTypeArgument = providedTypeArguments.get(i);
			TypeUsage targetTypeArgument = targetTypeArguments.get(i);
			if (!providedTypeArgument.canBeAssignedTo(targetTypeArgument))
				return false;
		}
		return true;
	}

	@Override
	public boolean isGeneric() {
		return typeArguments.size() > 0;
	}

	@Override
	public boolean isEnum() {
		return getRawType().isEnum();
	}

	@Override
	public boolean isArray() {
		return getRawType().isArray();
	}

	@Override
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
		return annotations.stream()
						  .filter(annotation -> annotation.annotationType().equals(annotationType))
						  .map(annotationType::cast)
						  .findFirst();
	}

	@Override
	public <A extends Annotation> boolean isAnnotated(Class<A> annotationType) {
		return findAnnotation(annotationType).isPresent();
	}

	@Override
	public boolean isAssignableFrom(Class<?> providedClass) {
		return TypeUsage.of(providedClass).canBeAssignedTo(this);
	}

	@Override
	public Optional<TypeUsage> getComponentType() {
		Class<?> componentType = rawType.getComponentType();
		if (componentType != null)
			return Optional.of(TypeUsage.of(componentType));
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

	private Optional<TypeUsageImpl> findSuperType(Class<?> typeToFind) {
		return findSuperTypeIn(typeToFind, this.rawType);
	}

	private Optional<TypeUsageImpl> findSuperTypeIn(Class<?> typeToFind, Class<?> rawType) {
		List<AnnotatedType> supertypes = new ArrayList<>();
		if (rawType.getSuperclass() != null)
			supertypes.add(rawType.getAnnotatedSuperclass());
		supertypes.addAll(Arrays.asList(rawType.getAnnotatedInterfaces()));
		for (AnnotatedType type : supertypes) {
			if (extractRawType(type.getType()).equals(typeToFind))
				return Optional.of(TypeUsageImpl.forAnnotatedType(type));
		}

		for (AnnotatedType type : supertypes) {
			TypeUsageImpl typeUsage = TypeUsageImpl.forAnnotatedType(type);
			Optional<TypeUsageImpl> nestedFound = typeUsage.findSuperType(typeToFind);
			if (nestedFound.isPresent())
				return nestedFound;
		}

		return Optional.empty();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj.getClass() != getClass())
			return false;
		TypeUsageImpl other = (TypeUsageImpl) obj;
		if (!other.getRawType().equals(getRawType()))
			return false;
		if (!other.getType().equals(getType()))
			return false;
		if (!other.getTypeArguments().equals(getTypeArguments()))
			return false;
		if (!other.getAnnotations().equals(getAnnotations()))
			return false;
		if (other.isWildcard() && isWildcard()) {
			if (!(other.lowerBounds.equals(lowerBounds)))
				return false;
			if (!(other.upperBounds.equals(upperBounds)))
				return false;
		}
		if (other.isTypeVariable() && isTypeVariable()) {
			if (!other.typeVariable.equals(typeVariable))
				return false;
			return (other.upperBounds.equals(upperBounds));
		}
		return true;
	}

	@Override
	public boolean isVoid() {
		return rawType.equals(Void.class) || rawType.equals(void.class);
	}

	@Override
	public Optional<TypeUsage> getSuperclass() {
		if (rawType.getSuperclass() == null) {
			return Optional.empty();
		}
		return Optional.of(TypeUsage.forType(rawType.getSuperclass()));
	}

	@Override
	public List<TypeUsage> getInterfaces() {
		return Arrays.stream(getRawType().getInterfaces())
					 .map(TypeUsage::forType)
					 .collect(Collectors.toList());
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean isInterface() {
		return rawType.isInterface();
	}

	@Override
	public int hashCode() {
		return rawType.hashCode();
	}

	@Override
	public String toString() {
		return toString(this, new HashSet<>());
	}

	private static String toString(TypeUsage self, Set<TypeUsage> touchedTypes) {
		if (self instanceof TypeUsageImpl) {
			return ((TypeUsageImpl) self).toString(touchedTypes);
		}
		return self.toString();
	}

	// TODO: Clean up
	private String toString(Set<TypeUsage> touchedTypes) {
		if (touchedTypes.contains(this)) {
			if (isTypeVariableOrWildcard()) {
				return typeVariable;
			}
			return "";
		}
		touchedTypes.add(this);

		String representation = getRawType().getSimpleName();
		if (isGeneric()) {
			String typeArgsRepresentation = typeArguments
												.stream()
												.map(typeUsage -> toString(typeUsage, touchedTypes))
												.collect(Collectors.joining(", "));
			representation = String.format("%s<%s>", representation, typeArgsRepresentation);
		}
		if (isArray()) {
			representation = String.format("%s[]", toString(getComponentType().get(), touchedTypes));
		}
		if (typeVariable != null) {
			representation = typeVariable;
			if (hasUpperBoundBeyondObject()) {
				String boundsRepresentation =
					upperBounds.stream()
							   .map(typeUsage -> toString(typeUsage, touchedTypes))
							   .collect(Collectors.joining(" & "));
				representation += String.format(" extends %s", boundsRepresentation);
			}
			if (hasLowerBounds()) {
				String boundsRepresentation =
					lowerBounds.stream() //
							   .map(typeUsage -> toString(typeUsage, touchedTypes)) //
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
