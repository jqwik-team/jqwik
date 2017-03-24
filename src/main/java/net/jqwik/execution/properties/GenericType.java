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
}
