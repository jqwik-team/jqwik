package net.jqwik.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericType {

	private final Class<?> rawType;
	private final GenericType[] typeArguments;

	public GenericType(Class<?> rawType, GenericType... typeArguments) {
		this.rawType = rawType;
		this.typeArguments = typeArguments;
	}

	public GenericType(Parameter parameter) {
		this(parameter.getParameterizedType());
	}

	public GenericType(Type parameterizedType) {
		this(extractRawType(parameterizedType), extractTypeArguments(parameterizedType));
	}

	private static Class<?> extractRawType(Type parameterizedType) {
		if (parameterizedType instanceof Class) {
			return (Class) parameterizedType;
		}
		return (Class) ((ParameterizedType) parameterizedType).getRawType();
	}

	private static GenericType[] extractTypeArguments(Type parameterizedType) {
		if (parameterizedType instanceof Class) {
			return new GenericType[0];
		}
		List<GenericType> typeArgs = Arrays.stream(((ParameterizedType) parameterizedType).getActualTypeArguments())
				.map(type -> new GenericType(type)).collect(Collectors.toList());
		return typeArgs.toArray(new GenericType[typeArgs.size()]);
	}

	public Class<?> getRawType() {
		return rawType;
	}

	public GenericType[] getTypeArguments() {
		return typeArguments;
	}

	public boolean isGeneric() {
		return typeArguments.length > 0;
	}

	public boolean isEnum() {
		return getRawType().isEnum();
	}

	public boolean isAssignableFrom(GenericType providedType) {
		if (boxedTypeMatches(providedType.getRawType(), this.getRawType()))
			return true;
		if (!this.getRawType().isAssignableFrom(providedType.getRawType()))
			return false;
		return allTypeArgumentsAreAssignable(typeArguments, providedType.getTypeArguments());
	}

	private boolean allTypeArgumentsAreAssignable(GenericType[] targetTypeArguments, GenericType[] providedTypeArguments) {
		if (targetTypeArguments.length != providedTypeArguments.length)
			return false;
		for (int i = 0; i < targetTypeArguments.length; i++) {
			GenericType targetTypeArgument = targetTypeArguments[i];
			GenericType providedTypeArgument = providedTypeArguments[i];
			if (!targetTypeArgument.isAssignableFrom(providedTypeArgument))
				return false;
		}
		return true;
	}

	public boolean isAssignableFrom(Class providedClass) {
		if (this.getRawType().isAssignableFrom(providedClass))
			return true;
		return boxedTypeMatches(providedClass, this.getRawType());
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

	@Override
	public String toString() {
		String representation = getRawType().getSimpleName();
		if (isGeneric()) {
			String typeArgsRepresentation = Arrays.stream(typeArguments).map(genericType -> genericType.toString())
					.collect(Collectors.joining(", "));
			representation = String.format("%s<%s>", representation, typeArgsRepresentation);
		}
		return representation;
	}
}
