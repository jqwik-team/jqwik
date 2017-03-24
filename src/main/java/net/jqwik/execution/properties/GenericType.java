package net.jqwik.execution.properties;

import java.lang.reflect.*;

public class GenericType {

	private final Type parameterizedType;

	public GenericType(Type parameterizedType) {

		this.parameterizedType = parameterizedType;
	}

	public Class getRawType() {
		if (parameterizedType instanceof Class) {
			return (Class) parameterizedType;
		}
		return (Class) ((ParameterizedType) parameterizedType).getRawType();
	}

	public Type[] getTypeArguments() {
		if (parameterizedType instanceof Class) {
			return new Type[0];
		}
		return ((ParameterizedType) parameterizedType).getActualTypeArguments();
	}

	public boolean isGeneric() {
		return (parameterizedType instanceof ParameterizedType);
	}

	public boolean isEnum() {
		return getRawType().isEnum();
	}
}
