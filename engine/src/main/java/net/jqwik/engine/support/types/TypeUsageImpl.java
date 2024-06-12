package net.jqwik.engine.support.types;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class TypeUsageImpl implements TypeUsage, Cloneable {

	private static final Map<TypeVariable<?>, TypeUsageImpl> resolvedTypeVariables = new ConcurrentHashMap<>();

	public static final String WILDCARD = "?";

	public static TypeUsage forParameterizedClass(Tuple2<Class<?>, TypeUsage[]> parameterizedClass) {
		Class<?> type = parameterizedClass.get1();
		TypeUsage[] typeParameters = parameterizedClass.get2();

		if (typeParameters.length > 0 && typeParameters.length != type.getTypeParameters().length) {
			String typeArgumentsString = JqwikStringSupport.displayString(typeParameters);
			throw new JqwikException(String.format("Type [%s] cannot have type parameters [%s]", type, typeArgumentsString));
		}
		TypeUsageImpl typeUsage = new TypeUsageImpl(type, type, null, null, Collections.emptyList());
		typeUsage.addTypeArguments(Arrays.asList(typeParameters));
		return typeUsage;
	}

	public static TypeUsage wildcardOf(TypeUsage upperBound) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			Object.class, Object.class, null, TypeUsageImpl.WILDCARD, Collections.emptyList()
		);
		typeUsage.addUpperBounds(Arrays.asList(upperBound));
		return typeUsage;
	}

	public static TypeUsageImpl forType(Type type) {
		if (type instanceof WildcardType) {
			return TypeUsageImpl.wildcardOf((WildcardType) type);
		}
		return TypeUsageImpl.forNonWildcardType(type);
	}

	public static TypeUsage forResolution(TypeResolution typeResolution) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			extractRawType(typeResolution.type()),
			typeResolution.type(),
			typeResolution.annotatedType(),
			extractTypeVariable(typeResolution.type()),
			extractAnnotations(typeResolution.annotatedType())
		);
		typeUsage.addTypeArguments(extractTypeArguments(typeResolution));
		typeUsage.addUpperBounds(extractUpperBounds(typeResolution.annotatedType()));
		typeUsage.addLowerBounds(extractLowerBounds(typeResolution.annotatedType()));

		return typeUsage;
	}

	public static TypeUsage forParameter(MethodParameter parameter) {
		return forParameter(parameter, RegisteredTypeUsageEnhancers.getEnhancers());
	}

	// Only used in tests
	public static TypeUsage forParameter(MethodParameter parameter, List<TypeUsage.Enhancer> enhancerPipeline) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			extractRawType(parameter.getType()),
			parameter.getType(),
			parameter.getAnnotatedType(),
			extractTypeVariable(parameter.getType()),
			parameter.findAllAnnotations()
		);

		typeUsage.addTypeArguments(extractTypeArguments(parameter));
		typeUsage.addUpperBounds(extractUpperBounds(parameter));
		typeUsage.addLowerBounds(extractLowerBounds(parameter));

		return forParameterThroughEnhancerPipeline(parameter, enhancerPipeline, typeUsage);
	}

	private static TypeUsage forParameterThroughEnhancerPipeline(
		MethodParameter parameter,
		List<Enhancer> enhancerPipeline,
		TypeUsageImpl typeUsage
	) {
		TypeUsage enhanced = typeUsage;
		for (Enhancer enhancer : enhancerPipeline) {
			enhanced = enhancer.forParameter(enhanced, parameter.getRawParameter());
		}
		return enhanced;
	}

	public static TypeUsageImpl forNonWildcardType(Type type) {
		// This allows optimized handling of type Object
		if (type.equals(Object.class)) {
			return (TypeUsageImpl) TypeUsage.OBJECT_TYPE;
		}
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

	public static TypeUsageImpl wildcardOf(WildcardType wildcardType) {
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

		TypeUsageImpl typeUsage = new TypeUsageImpl(rawType, type, annotatedType, typeVariable, annotations);
		if (type instanceof TypeVariable) {
			resolvedTypeVariables.put((TypeVariable<?>) type, typeUsage);
		}
		processTypeUsage.accept(typeUsage);

		return typeUsage;
	}

	private static Optional<TypeUsageImpl> alreadyResolvedIn(TypeVariable<?> typeVariable) {
		return Optional.ofNullable(resolvedTypeVariables.get(typeVariable));
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
		return upperBounds.isEmpty() ? Collections.singletonList(OBJECT_TYPE) : upperBounds;
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
					 .map(TypeUsageImpl::forType)
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

	private final Class<?> rawType;
	private final Type type;
	private final AnnotatedType annotatedType;
	private final String typeVariable;
	private List<Annotation> annotations;
	private final List<TypeUsage> typeArguments = new ArrayList<>();

	private final List<TypeUsage> upperBounds = new ArrayList<>();
	private final List<TypeUsage> lowerBounds = new ArrayList<>();
	private HashMap<String, Object> metaInfo = new LinkedHashMap<>();
	private boolean isNullable = false;

	// Lazy initialization
	private volatile TypeUsage superclass = null;

	// Lazy initialization
	private volatile List<TypeUsage> interfaces = null;


	public TypeUsageImpl(
		Class<?> rawType,
		Type type,
		AnnotatedType annotatedType,
		String typeVariable,
		List<Annotation> annotations
	) {
		if (rawType == null) {
			throw new IllegalArgumentException("rawType must never be null");
		}
		this.rawType = rawType;
		this.type = type;
		this.annotatedType = annotatedType;
		this.typeVariable = typeVariable;
		this.annotations = new ArrayList<>(annotations);
	}

	public void addTypeArguments(List<TypeUsage> typeArguments) {
		this.typeArguments.addAll(typeArguments);
	}

	public void addLowerBounds(List<TypeUsage> lowerBounds) {
		this.lowerBounds.addAll(lowerBounds);
	}

	public void addUpperBounds(List<TypeUsage> upperBounds) {
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
		return getTypeArguments().size() <= position ? OBJECT_TYPE : getTypeArguments().get(position);
	}

	@Override
	public boolean isOfType(Class<?> aRawType) {
		if (isTypeVariableOrWildcard())
			return false;
		return rawType == aRawType;
	}

	@Override
	public boolean canBeAssignedTo(TypeUsage targetType) {
		if (this.equals(targetType)) return true;

		if (targetType == OBJECT_TYPE) return true;

		if (targetType.isSuperWildcard() && this.isExtendsConstraint()) {
			return false;
		}
		if (targetType.isTypeVariableOrWildcard()) {
			return canBeAssignedToUpperBounds(this, targetType) &&
					   canBeAssignedToLowerBounds(this, targetType);
		}
		if (primitiveTypeToObject(this.getRawType(), targetType.getRawType()))
			return true;
		if (boxedTypeMatchesEitherWay(this, targetType)) return true;
		if (targetType.getRawType().isAssignableFrom(rawType)) {
			if (this.isParameterizedRaw() || targetType.isParameterizedRaw()) {
				// A raw parameterized type can always be on either side of assignment
				return true;
			}
			if (targetType.getTypeArgument(0).isExtendsConstraint()) {
				// Recursive types will break off here.
				// Sadly this will only work for direct recursion.
				// Indirect recursion will still lead to a stack overflow.
				boolean isRecursive = targetType.getTypeArgument(0).getUpperBounds().stream()
												.anyMatch(upperBound -> upperBound.isOfType(targetType.getRawType()));
				if (isRecursive) {
					return true;
				}
			}
			if (allTypeArgumentsCanBeAssigned(this.getTypeArguments(), targetType.getTypeArguments())) {
				return true;
			} else return anySupertypeCanBeAssignedTo(targetType);
		}
		return false;
	}

	private static boolean boxedTypeMatchesEitherWay(TypeUsage sourceType, TypeUsage targetType) {
		if (boxedTypeMatches(targetType.getRawType(), sourceType.getRawType()))
			return true;
		if (boxedTypeMatches(sourceType.getRawType(), targetType.getRawType()))
			return true;
		return false;
	}

	private boolean anySupertypeCanBeAssignedTo(TypeUsage targetType) {
		// An interface cannot be assigned to a class except to Object
		if (isInterface(this) && !isInterface(targetType) && !targetType.isOfType(Object.class)) {
			return false;
		}
		for (TypeUsage supertype : getSuperTypes()) {
			if (supertype.canBeAssignedTo(targetType)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isInterface(TypeUsage targetType) {
		return targetType.getRawType().isInterface();
	}

	private boolean primitiveTypeToObject(Class<?> primitiveType, Class<?> objectType) {
		return primitiveType.isPrimitive() && objectType.equals(Object.class);
	}

	private static boolean canBeAssignedToUpperBounds(TypeUsage sourceType, TypeUsage targetType) {
		if (sourceType.isTypeVariableOrWildcard()) {
			return sourceType.getUpperBounds().stream()
							 .allMatch(upperBoundSource -> canBeAssignedToUpperBounds(upperBoundSource, targetType));
		}
		return targetType.getUpperBounds().stream().allMatch(upperBoundTarget -> sourceType.canBeAssignedTo(upperBoundTarget));
	}

	private static boolean canBeAssignedToLowerBounds(TypeUsage sourceType, TypeUsage targetType) {
		if (sourceType.isTypeVariableOrWildcard()) {
			return sourceType.getLowerBounds().stream()
							 .allMatch(lowerBoundSource -> canBeAssignedToLowerBounds(lowerBoundSource, targetType));
		}
		return targetType.getLowerBounds().stream().allMatch(lowerBoundTarget -> lowerBoundTarget.canBeAssignedTo(sourceType));
	}

	private boolean allTypeArgumentsCanBeAssigned(
		List<TypeUsage> providedTypeArguments, List<TypeUsage> targetTypeArguments
	) {
		if (targetTypeArguments.size() != providedTypeArguments.size())
			return false;

		for (int i = 0; i < targetTypeArguments.size(); i++) {
			TypeUsage providedTypeArgument = providedTypeArguments.get(i);
			TypeUsage targetTypeArgument = targetTypeArguments.get(i);

			if (boxedTypeMatchesEitherWay(providedTypeArgument, targetTypeArgument)) {
				// Primitive types can be assigned to their boxed type and vice versa
				// This cannot happen in real Java (since primitive types cannot be used in generics),
				// but it is possible in some jqwik scenarios
				return true;
			}

			boolean sameRawType = targetTypeArgument.getRawType().equals(providedTypeArgument.getRawType());
			if (!(sameRawType || targetTypeArgument.isTypeVariableOrWildcard())) {
				// Co- or contra-variance is only allowed for type variables and wildcards.
				// Therefore, stop here if the raw types are not equal and it is not a type variable or wildcard.
				return false;
			}

			// Unbound type variables can only be assigned to themselves or type-equal type usages.
			if (targetTypeArgument.isTypeVariable() && !providedTypeArgument.hasSameTypeAs(targetTypeArgument)) {
				return false;
			}

			if (!providedTypeArgument.canBeAssignedTo(targetTypeArgument)) return false;
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
		if (!isArray()) {
			return Optional.empty();
		}
		return Optional.of(createComponentType());
	}

	private TypeUsage createComponentType() {
		if (type instanceof GenericArrayType) {
			return createGenericArrayComponentType((GenericArrayType) type);
		} else {
			return createSimpleArrayComponentType();
		}
	}

	private TypeUsage createGenericArrayComponentType(GenericArrayType genericArrayType) {
		return TypeUsage.forType(genericArrayType.getGenericComponentType());
	}

	private TypeUsageImpl createSimpleArrayComponentType() {
		Class<?> componentRawType = rawType.getComponentType();
		return (TypeUsageImpl) TypeUsage.of(componentRawType);
	}

	private static boolean boxedTypeMatches(Class<?> providedType, Class<?> targetType) {
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

	private TypeUsageImpl cloneWith(Consumer<TypeUsageImpl> updater) {
		try {
			TypeUsageImpl clone = (TypeUsageImpl) this.clone();
			updater.accept(clone);
			return clone;
		} catch (CloneNotSupportedException shouldNeverHappen) {
			return JqwikExceptionSupport.throwAsUncheckedException(shouldNeverHappen);
		}
	}

	@Override
	public boolean hasSameTypeAs(TypeUsage otherUsage) {
		if (this == otherUsage) {
			return true;
		}

		if (!(otherUsage instanceof TypeUsageImpl)) {
			return false;
		}

		TypeUsageImpl other = (TypeUsageImpl) otherUsage;

		// The rest is mostly a copy of the equals method but
		// - without annotation checking
		// - without comparing name of type variable
		// and with comparing component types using hasSameTypeAs

		if (!other.getRawType().equals(getRawType()))
			return false;
		if (!(haveSameTypes(other.typeArguments, typeArguments)))
			return false;
		if (other.isWildcard() != isWildcard()) {
			return false;
		}
		if (other.isTypeVariable() != isTypeVariable()) {
			return false;
		}
		if (other.isWildcard() && isWildcard()) {
			if (!(haveSameTypes(other.lowerBounds, lowerBounds)))
				return false;
			if (!(haveSameTypes(other.upperBounds, upperBounds)))
				return false;
		}
		if (other.isTypeVariable() && isTypeVariable()) {
			return haveSameTypes(other.upperBounds, upperBounds);
		}
		return other.isNullable() == isNullable();
	}

	private static boolean haveSameTypes(List<TypeUsage> left, List<TypeUsage> right) {
		if (left.size() != right.size()) {
			return false;
		}
		for (int i = 0; i < left.size(); i++) {
			if (!left.get(i).hasSameTypeAs(right.get(i))) {
				return false;
			}
		}
		return true;
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
		return other.isNullable() == isNullable();
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
		// Double checked locking for thread safety
		if (superclass == null) {
			synchronized (this) {
				if (superclass == null) {
					superclass = createSuperclass();
				}
			}
		}
		return Optional.of(superclass);
	}

	private TypeUsageImpl createSuperclass() {
		return resolveSupertype(rawType.getGenericSuperclass());
	}

	private TypeUsageImpl resolveSupertype(Type supertype) {
		// TODO: The right thing to do would be to resolve the supertype using a GenericsClassContext.
		//       However, this would require to have the annotated, parameterized type of the supertype
		//       available. This is not the case for the supertype of a raw type.
		//       To achieve this, the original resolution of the current type usage would have to be stored.
		TypeUsageImpl resolvedSupertype = TypeUsageImpl.forType(supertype);
		return resolvedSupertype.replaceTypeVariablesFromSubtype(this);
	}

	private TypeUsageImpl replaceTypeVariablesFromSubtype(TypeUsageImpl subtype) {
		Map<String, TypeUsage> typeVariables = extractTypeVariables(subtype);
		replaceTypeVariablesIn(typeVariables, this);
		return this;
	}

	private static void replaceTypeVariablesIn(Map<String, TypeUsage> typeVariables, TypeUsageImpl replacementTarget) {
		for (int i = 0; i < replacementTarget.typeArguments.size(); i++) {
			TypeUsage typeArgument = replacementTarget.typeArguments.get(i);
			if (typeArgument.isTypeVariable() && typeVariables.containsKey(typeArgument.getTypeVariable())) {
				replacementTarget.typeArguments.set(i, typeVariables.get(typeArgument.getTypeVariable()));
			} else {
				replaceTypeVariablesIn(typeVariables, (TypeUsageImpl) typeArgument);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private static Map<String, TypeUsage> extractTypeVariables(TypeUsage subtype) {
		List<TypeUsage> typeArgumentsFromSubtype = subtype.getTypeArguments();
		Map<String, TypeUsage> namedTypeArguments = new HashMap<>();
		for (int pos = 0; pos < typeArgumentsFromSubtype.size(); pos++) {
			TypeUsage typeArgumentFromSubtype = typeArgumentsFromSubtype.get(pos);
			if (!typeArgumentFromSubtype.isTypeVariable()) {
				// The parameter name is taken from the raw type, e.g. List<String> -> E = String because List<E>
				TypeVariable[] rawTypeArguments = subtype.getRawType().getTypeParameters();
				if (rawTypeArguments.length >= pos) {
					namedTypeArguments.put(rawTypeArguments[pos].getName(), typeArgumentFromSubtype);
				}
			}
		}
		return namedTypeArguments;
	}

	@Override
	public List<TypeUsage> getInterfaces() {
		// Double checked locking for thread safety
		if (interfaces == null) {
			synchronized(this) {
				if (interfaces == null) {
					interfaces = createInterfaces();
				}
			}
		}
		return interfaces;
	}

	private List<TypeUsage> createInterfaces() {
		return Arrays.stream(getRawType().getGenericInterfaces())
					 .filter(Objects::nonNull) // for some strange reason there can be null entries
					 .map(type -> resolveSupertype(type))
					 // .map(superType -> superType.replaceTypeVariablesFromSubtype(this))
					 .collect(Collectors.toList());
	}

	@Override
	public List<TypeUsage> getSuperTypes() {
		List<TypeUsage> supertypes = new ArrayList<>();
		getSuperclass().ifPresent(e -> supertypes.add(e));
		supertypes.addAll(getInterfaces());
		return supertypes;
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
	public boolean isNullable() {
		return isNullable;
	}

	@Override
	public boolean isParameterizedRaw() {
		return typeArguments.isEmpty() && getRawType().getTypeParameters().length > 0;
	}

	@Override
	public boolean isSuperWildcard() {
		return isWildcard() && !lowerBounds.isEmpty();
	}

	@Override
	public boolean isExtendsConstraint() {
		return isTypeVariableOrWildcard() && upperBounds.stream().anyMatch(b -> !b.getRawType().equals(Object.class));
	}

	@Override
	public TypeUsage asNullable() {
		if (this.isNullable()) {
			return this;
		}
		return cloneWith(t -> t.isNullable = true);
	}

	@Override
	public TypeUsage asNotNullable() {
		if (!this.isNullable()) {
			return this;
		}
		return cloneWith(t -> t.isNullable = false);
	}

	@Override
	public String getTypeVariable() {
		return typeVariable;
	}

	@Override
	public <A extends Annotation> TypeUsage withAnnotation(A annotation) {
		return cloneWith(t -> {
			t.annotations = new ArrayList<>(annotations);
			t.annotations.add(annotation);
			JqwikAnnotationSupport.allMetaAnnotations(annotation).stream()
								  .filter(candidate -> !t.annotations.contains(candidate))
								  .forEach(metaAnnotation -> t.annotations.add(metaAnnotation));
		});
	}

	@Override
	public Optional<Object> getMetaInfo(String key) {
		return Optional.ofNullable(metaInfo.get(key));
	}

	@Override
	public TypeUsage withMetaInfo(String key, Object value) {
		return cloneWith(t -> {
			t.metaInfo = new LinkedHashMap<>(metaInfo);
			t.metaInfo.put(key, value);
		});
	}

	@Override
	public int hashCode() {
		return rawType.hashCode();
	}

	@Override
	public String toString() {
		return TypeUsageToString.toString(this);
	}

}
