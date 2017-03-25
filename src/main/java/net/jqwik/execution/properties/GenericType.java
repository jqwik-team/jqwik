package net.jqwik.execution.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class GenericType {

	private final Type parameterizedType;

	public GenericType(Type parameterizedType) {
		this.parameterizedType = parameterizedType;
	}

	public Class<?> getRawType() {
		if (parameterizedType instanceof Class) {
			return (Class) parameterizedType;
		}
		return (Class) ((ParameterizedType) parameterizedType).getRawType();
	}

	public GenericType[] getTypeArguments() {
		if (parameterizedType instanceof Class) {
			return new GenericType[0];
		}
		List<GenericType> typeArgs = Arrays.stream(((ParameterizedType) parameterizedType).getActualTypeArguments())
										   .map(type -> new GenericType(type)).collect(Collectors.toList());
		return typeArgs.toArray(new GenericType[typeArgs.size()]);
	}

	public boolean isGeneric() {
		return (parameterizedType instanceof ParameterizedType);
	}

	public boolean isEnum() {
		return getRawType().isEnum();
	}

	public boolean isAssignableFrom(GenericType providedType) {
		if (boxedTypeMatches(providedType.getRawType(), this.getRawType()))
			return true;
		return this.getRawType().isAssignableFrom(providedType.getRawType());
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


}
