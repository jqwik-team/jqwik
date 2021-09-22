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

	private static final Map<TypeVariable<?>, TypeUsageImpl> resolved = new ConcurrentHashMap<>();

	static final String WILDCARD = "?";

	public static TypeUsage forResolution(TypeResolution typeResolution) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			extractRawType(typeResolution.type()),
			typeResolution.type(),
			typeResolution.annotatedType(),
			extractTypeVariable(typeResolution.type()),
			extractAnnotations(typeResolution.annotatedType()),
			null
		);
		typeUsage.addTypeArguments(extractTypeArguments(typeResolution));
		typeUsage.addUpperBounds(extractUpperBounds(typeResolution.annotatedType()));
		typeUsage.addLowerBounds(extractLowerBounds(typeResolution.annotatedType()));

		return typeUsage;
	}

	public static TypeUsage forParameter(MethodParameter parameter) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			extractRawType(parameter.getType()),
			parameter.getType(),
			parameter.getAnnotatedType(),
			extractTypeVariable(parameter.getType()),
			parameter.findAllAnnotations(),
			parameter.getRawParameter()
		);
		typeUsage.addTypeArguments(extractTypeArguments(parameter));
		typeUsage.addUpperBounds(extractUpperBounds(parameter));
		typeUsage.addLowerBounds(extractLowerBounds(parameter));

		return typeUsage;
	}

	static TypeUsageImpl forNonWildcardType(Type type) {
		return resolveVariableOrCreate(
			extractRawType(type),
			type,
			extractTypeVariable(type),
			Collections.emptyList(),
			typeUsage -> {
				typeUsage.addTypeArguments(extractPlainTypeArguments(type));
				typeUsage.addUpperBounds(extractUpperBounds(type));
				typeUsage.addLowerBounds(extractLowerBounds(type));
			}
		);
	}

	static TypeUsageImpl forWildcard(WildcardType wildcardType) {
		return resolveVariableOrCreate(
			Object.class, wildcardType,
			WILDCARD,
			extractAnnotations(wildcardType),
			typeUsage -> {
				typeUsage.addUpperBounds(extractUpperBoundsForWildcard(wildcardType));
				typeUsage.addLowerBounds(extractLowerBoundsForWildcard(wildcardType));
			}
		);
	}

	private static TypeUsageImpl forAnnotatedType(AnnotatedType annotatedType) {
		return resolveVariableOrCreate(
			extractRawType(annotatedType.getType()),
			annotatedType.getType(),
			annotatedType,
			extractTypeVariable(annotatedType.getType()),
			extractAnnotations(annotatedType),
			typeUsage -> {
				typeUsage.addTypeArguments(extractPlainTypeArguments(annotatedType));
				typeUsage.addUpperBounds(extractUpperBounds(annotatedType));
				typeUsage.addLowerBounds(extractLowerBounds(annotatedType));
			}
		);
	}

	private static TypeUsageImpl resolveVariableOrCreate(
		Class<?> rawType,
		Type type,
		String typeVariable,
		List<Annotation> annotations,
		Consumer<TypeUsageImpl> processTypeUsage
	) {
		AnnotatedType annotatedType = type instanceof AnnotatedType ? (AnnotatedType) type : null;
		return resolveVariableOrCreate(rawType, type, annotatedType, typeVariable, annotations, processTypeUsage);
	}

	private static TypeUsageImpl resolveVariableOrCreate(
		Class<?> rawType,
		Type type,
		AnnotatedType annotatedType,
		String typeVariable,
		List<Annotation> annotations,
		Consumer<TypeUsageImpl> processTypeUsage
	) {
		if (type instanceof TypeVariable) {
			Optional<TypeUsageImpl> alreadyResolved = alreadyResolvedIn((TypeVariable<?>) type);
			if (alreadyResolved.isPresent()) {
				return alreadyResolved.get();
			}
		}

		TypeUsageImpl typeUsage = new TypeUsageImpl(rawType, type, annotatedType, typeVariable, annotations, null);
		if (type instanceof TypeVariable) {
			resolved.put((TypeVariable<?>) type, typeUsage);
		}
		processTypeUsage.accept(typeUsage);

		return typeUsage;
	}

	private static Optional<TypeUsageImpl> alreadyResolvedIn(TypeVariable<?> typeVariable) {
		return Optional.ofNullable(resolved.get(typeVariable));
	}

	private static List<TypeUsage> extractTypeArguments(MethodParameter parameter) {
		if (parameter.getAnnotatedType() instanceof AnnotatedParameterizedType) {
			return extractAnnotatedTypeArguments((AnnotatedParameterizedType) parameter.getAnnotatedType());
		} else {
			return extractPlainTypeArguments(parameter.getType());
		}
	}

	private static List<TypeUsage> extractTypeArguments(TypeResolution resolution) {
		if (resolution.annotatedType() instanceof AnnotatedParameterizedType) {
			return extractAnnotatedTypeArguments((AnnotatedParameterizedType) resolution.annotatedType());
		} else {
			return extractPlainTypeArguments(resolution.type());
		}
	}

	private static List<TypeUsage> extractPlainTypeArguments(Object parameterizedType) {
		if (parameterizedType instanceof AnnotatedParameterizedType) {
			return extractAnnotatedTypeArguments((AnnotatedParameterizedType) parameterizedType);
		}
		if (parameterizedType instanceof ParameterizedType) {
			return toTypeUsages(((ParameterizedType) parameterizedType).getActualTypeArguments());
		}
		// Now it's either not a generic type or it has type variables
		return Collections.emptyList();
	}

	private static List<TypeUsage> extractAnnotatedTypeArguments(AnnotatedParameterizedType annotatedType) {
		AnnotatedType[] annotatedActualTypeArguments = annotatedType.getAnnotatedActualTypeArguments();
		return toTypeUsages(annotatedActualTypeArguments);
	}

	private static List<TypeUsage> toTypeUsages(AnnotatedType[] annotatedActualTypeArguments) {
		return Arrays.stream(annotatedActualTypeArguments)
					 .filter(Objects::nonNull) // for some strange reason there can be null entries
					 .map(TypeUsageImpl::forAnnotatedType)
					 .collect(Collectors.toList());
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
			return ((TypeVariable<?>) parameterizedType).getName();
		}
		return null;
	}

	private static List<TypeUsage> extractUpperBounds(AnnotatedType annotatedType) {
		List<TypeUsage> upperBounds = Collections.emptyList();
		if (annotatedType instanceof AnnotatedWildcardType) {
			AnnotatedType[] annotatedUpperBounds = ((AnnotatedWildcardType) annotatedType).getAnnotatedUpperBounds();
			upperBounds = toTypeUsages(annotatedUpperBounds);
		}
		if (annotatedType instanceof AnnotatedTypeVariable) {
			AnnotatedType[] annotatedUpperBounds = ((AnnotatedTypeVariable) annotatedType).getAnnotatedBounds();
			upperBounds = toTypeUsages(annotatedUpperBounds);
		}
		return upperBounds.isEmpty() ? Collections.singletonList(TypeUsage.of(Object.class)) : upperBounds;
	}

	private static List<TypeUsage> extractUpperBounds(MethodParameter parameter) {
		return extractUpperBounds(parameter.getAnnotatedType());
	}

	private static List<TypeUsage> extractUpperBounds(Type parameterizedType) {
		if (parameterizedType instanceof TypeVariable) {
			return extractUpperBoundsForTypeVariable((TypeVariable<?>) parameterizedType);
		}
		if (parameterizedType instanceof WildcardType) {
			return extractUpperBoundsForWildcard((WildcardType) parameterizedType);
		}
		return Collections.emptyList();
	}

	private static List<TypeUsage> extractUpperBoundsForTypeVariable(TypeVariable<?> typeVariable) {
		Type[] upperBounds = typeVariable.getBounds();
		return toTypeUsages(upperBounds);
	}

	private static List<TypeUsage> extractUpperBoundsForWildcard(WildcardType wildcardType) {
		return toTypeUsages(wildcardType.getUpperBounds());
	}

	private static List<TypeUsage> toTypeUsages(Type[] upperBounds) {
		return Arrays.stream(upperBounds)
					 .filter(Objects::nonNull) // for some strange reason there can be null entries
					 .map(TypeUsage::forType)
					 .collect(Collectors.toList());
	}

	private static List<TypeUsage> extractLowerBounds(MethodParameter parameter) {
		return extractLowerBounds(parameter.getAnnotatedType());
	}

	private static List<TypeUsage> extractLowerBounds(AnnotatedType annotatedType) {
		if (annotatedType instanceof AnnotatedWildcardType) {
			AnnotatedType[] annotatedUpperBounds = ((AnnotatedWildcardType) annotatedType).getAnnotatedLowerBounds();
			return toTypeUsages(annotatedUpperBounds);
		}
		return Collections.emptyList();
	}

	private static List<TypeUsage> extractLowerBounds(Type parameterizedType) {
		if (parameterizedType instanceof WildcardType) {
			return extractLowerBoundsForWildcard((WildcardType) parameterizedType);
		}
		return Collections.emptyList();
	}

	private static List<TypeUsage> extractLowerBoundsForWildcard(WildcardType wildcardType) {
		return toTypeUsages(wildcardType.getLowerBounds());
	}

	private static Class<?> extractRawType(Type parameterizedType) {
		if (parameterizedType instanceof Class) {
			return (Class<?>) parameterizedType;
		}
		if (parameterizedType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) parameterizedType).getRawType();
		}
		// Now we have a type variable (java.lang.reflect.TypeVariable)
		return Object.class;
	}

	private final Class<?> rawType;
	private final Type type;
	private final AnnotatedType annotatedType;
	private final String typeVariable;
	private final List<Annotation> annotations;
	private final List<TypeUsage> typeArguments = new ArrayList<>();

	private final List<TypeUsage> upperBounds = new ArrayList<>();
	private final List<TypeUsage> lowerBounds = new ArrayList<>();

	private final Parameter fromParameter;

	TypeUsageImpl(
		Class<?> rawType,
		Type type,
		AnnotatedType annotatedType,
		String typeVariable,
		List<Annotation> annotations,
		Parameter fromParameter
	) {
		if (rawType == null) {
			throw new IllegalArgumentException("rawType must never be null");
		}
		this.rawType = rawType;
		this.type = type;
		this.annotatedType = annotatedType;
		this.typeVariable = typeVariable;
		this.annotations = new ArrayList<>(annotations);
		this.fromParameter = fromParameter;
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
		if (isSingleUpperBoundVariableType()) {
			return getUpperBounds().get(0).getTypeArguments();
		}
		return typeArguments;
	}

	private boolean isSingleUpperBoundVariableType() {
		return isTypeVariableOrWildcard() && getUpperBounds().size() == 1 && getLowerBounds().isEmpty();
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
		if (primitiveTypeToObject(this.getRawType(), targetType.getRawType()))
			return true;
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

	private boolean primitiveTypeToObject(Class<?> primitiveType, Class<?> objectType) {
		return primitiveType.isPrimitive() && objectType.equals(Object.class);
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
		if (isSingleUpperBoundVariableType()) {
			return getAnnotationsStream().collect(Collectors.toList());
		}
		return Collections.unmodifiableList(annotations);
	}

	private Stream<Annotation> getAnnotationsStream() {
		if (isSingleUpperBoundVariableType()) {
			return Stream.concat(annotations.stream(), getUpperBounds().get(0).getAnnotations().stream());
		}
		return annotations.stream();
	}

	@Override
	public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
		return getAnnotationsStream()
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
		Class<?> componentRawType = rawType.getComponentType();
		if (componentRawType != null) {
			TypeUsageImpl componentType = (TypeUsageImpl) TypeUsage.of(componentRawType);
			componentType.annotations.addAll(this.annotations);
			return Optional.of(componentType);
		}
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
		if (!other.typeArguments.equals(typeArguments))
			return false;
		if (!other.getAnnotations().equals(getAnnotations()))
			return false;
		if (other.isWildcard() != isWildcard()) {
			return false;
		}
		if (other.isTypeVariable() != isTypeVariable()) {
			return false;
		}
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
		return Optional.of(TypeUsage.forType(rawType.getGenericSuperclass()));
	}

	@Override
	public List<TypeUsage> getInterfaces() {
		return toTypeUsages(getRawType().getGenericInterfaces());
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public AnnotatedType getAnnotatedType() {
		return annotatedType;
	}

	@Override
	public Optional<Parameter> getParameter() {
		return Optional.ofNullable(fromParameter);
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

	private String toString(Set<TypeUsage> touchedTypes) {
		String representation = getRawType().getSimpleName();

		if (touchedTypes.contains(this)) {
			if (isTypeVariableOrWildcard()) {
				return typeVariable;
			}
			return representation;
		}
		touchedTypes.add(this);

		if (isGeneric()) {
			representation = String.format("%s<%s>", representation, toStringTypeArguments(touchedTypes));
		}
		if (isArray()) {
			//noinspection OptionalGetWithoutIsPresent
			representation = String.format("%s[]", toString(getComponentType().get(), touchedTypes));
		}
		if (isTypeVariableOrWildcard()) {
			representation = toStringTypeVariable(touchedTypes);
		}
		if (!annotations.isEmpty()) {
			representation = String.format("%s %s", toStringAnnotations(), representation);
		}
		return representation;
	}

	private String toStringTypeArguments(Set<TypeUsage> touchedTypes) {
		return typeArguments.stream()
							.map(typeUsage -> toString(typeUsage, touchedTypes))
							.collect(Collectors.joining(", "));
	}

	private String toStringAnnotations() {
		return annotations.stream()
						  .map(Annotation::toString)
						  .collect(Collectors.joining(" "));
	}

	private String toStringTypeVariable(Set<TypeUsage> touchedTypes) {
		String representation = typeVariable;
		if (hasUpperBoundBeyondObject()) {
			representation += String.format(" extends %s", toStringUpperBound(touchedTypes));
		}
		if (hasLowerBounds()) {
			representation += String.format(" super %s", toStringLowerBounds(touchedTypes));
		}
		return representation;
	}

	private String toStringLowerBounds(Set<TypeUsage> touchedTypes) {
		return lowerBounds.stream()
						  .map(typeUsage -> toString(typeUsage, touchedTypes))
						  .collect(Collectors.joining(" & "));
	}

	private String toStringUpperBound(Set<TypeUsage> touchedTypes) {
		return upperBounds.stream()
						  .map(typeUsage -> toString(typeUsage, touchedTypes))
						  .collect(Collectors.joining(" & "));
	}

}
