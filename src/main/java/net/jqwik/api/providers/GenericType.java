package net.jqwik.api.providers;

import net.jqwik.*;
import net.jqwik.support.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericType {

	public static GenericType of(Class<?> type, GenericType... typeParameters) {
		if (typeParameters.length > 0 && typeParameters.length != type.getTypeParameters().length) {
			String typeArgumentsString = JqwikStringSupport.displayString(typeParameters);
			throw new JqwikException(String.format("Type [%s] cannot have type parameters [%s]", type, typeArgumentsString));
		}
		return new GenericType(type, typeParameters);
	}

	public static GenericType forParameter(Parameter parameter) {
		return new GenericType(parameter);
	}

	public static GenericType forType(Type type) {
		return new GenericType(type);
	}

	private final Class<?> rawType;
	private final GenericType[] typeArguments;
	private final String typeVariable;
	private final GenericType[] bounds;

	private GenericType(Class<?> rawType, String typeVariable, GenericType[] bounds, GenericType[] typeArguments) {
		this.rawType = rawType;
		this.typeVariable = typeVariable;
		this.bounds = bounds;
		this.typeArguments = typeArguments;
	}

	private GenericType(Class<?> rawType, GenericType... typeArguments) {
		this(rawType, null, new GenericType[0], typeArguments);
	}

	private GenericType(Parameter parameter) {
		this(parameter.getParameterizedType());
	}

	private GenericType(Type parameterizedType) {
		this(extractRawType(parameterizedType), extractTypeVariable(parameterizedType), extractBounds(parameterizedType),
				extractTypeArguments(parameterizedType));
	}

	private static String extractTypeVariable(Type parameterizedType) {
		if (parameterizedType instanceof TypeVariable) {
			return ((TypeVariable) parameterizedType).getName();
		}
		return null;
	}

	private static GenericType[] extractBounds(Type parameterizedType) {
		if (parameterizedType instanceof TypeVariable) {
			return Arrays.stream(((TypeVariable) parameterizedType).getBounds()) //
					.filter(type -> type != Object.class) //
					.map(GenericType::forType) //
					.toArray(GenericType[]::new);
		}
		return new GenericType[0];
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

	private static GenericType[] extractTypeArguments(Type parameterizedType) {
		if (parameterizedType instanceof ParameterizedType) {
			return Arrays.stream(((ParameterizedType) parameterizedType).getActualTypeArguments()) //
					.map(GenericType::forType) //
					.toArray(GenericType[]::new);
		}
		// Now it's either not a generic type or it has type variables
		return new GenericType[0];
	}

	public Class<?> getRawType() {
		return rawType;
	}

	public boolean hasBounds() {
		return bounds.length > 0;
	}

	public GenericType[] getTypeArguments() {
		return typeArguments;
	}

	public boolean isOfType(Class<?> aRawType) {
		return rawType == aRawType;
	}

	public boolean isGeneric() {
		return typeArguments.length > 0;
	}

	public boolean isEnum() {
		return getRawType().isEnum();
	}

	public boolean isArray() {
		return getRawType().isArray();
	}

	private boolean allTypeArgumentsAreCompatible(GenericType[] targetTypeArguments, GenericType[] providedTypeArguments) {
		if (providedTypeArguments.length == 0) {
			return Arrays.stream(targetTypeArguments)
				.allMatch(targetType -> targetType.isOfType(Object.class) && !targetType.hasBounds());
		}
		if (targetTypeArguments.length != providedTypeArguments.length)
			return false;
		for (int i = 0; i < targetTypeArguments.length; i++) {
			GenericType targetTypeArgument = targetTypeArguments[i];
			GenericType providedTypeArgument = providedTypeArguments[i];
			if (!targetTypeArgument.isCompatibleWith(providedTypeArgument))
				return false;
		}
		return true;
	}

	public boolean isCompatibleWith(Class<?> providedClass) {
		if (isOfType(providedClass))
			return true;
		return boxedTypeMatches(providedClass, this.getRawType());
	}

	public boolean isCompatibleWith(GenericType providedType) {
		if (!this.isCompatibleWith(providedType.getRawType()))
			return false;
		return allTypeArgumentsAreCompatible(typeArguments, providedType.getTypeArguments());
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

	public GenericType getComponentType() {
		Class<?> componentType = rawType.getComponentType();
		if (componentType != null)
			return GenericType.of(componentType);
		return null;
	}

	@Override
	public String toString() {
		String representation = getRawType().getSimpleName();
		if (isGeneric()) {
			String typeArgsRepresentation = Arrays.stream(typeArguments) //
					.map(GenericType::toString) //
					.collect(Collectors.joining(", "));
			representation = String.format("%s<%s>", representation, typeArgsRepresentation);
		}
		if (isArray()) {
			representation = String.format("%s[]", getComponentType().toString());
		}
		if (typeVariable != null) {
			representation = typeVariable;
			if (hasBounds()) {
				String boundsRepresentation = Arrays.stream(bounds) //
													  .map(GenericType::toString) //
													  .collect(Collectors.joining(" & "));
				representation += String.format(" extends %s", boundsRepresentation);
			}
		}
		return representation;
	}

}
