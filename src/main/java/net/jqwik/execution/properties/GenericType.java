package net.jqwik.execution.properties;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericType {

	private final Type parameterizedType;

	public GenericType(Type parameterizedType) {

		this.parameterizedType = parameterizedType;
	}

	public Type getRawType() {
		if (parameterizedType instanceof  Class) {
			return  parameterizedType;
		}
		return ((ParameterizedType) parameterizedType).getRawType();
	}

	public Type[] getTypeArguments() {
		if (parameterizedType instanceof  Class) {
			return  new Type[0];
		}
		return ((ParameterizedType) parameterizedType).getActualTypeArguments();
	}

	public boolean isGeneric() {
		return (parameterizedType instanceof ParameterizedType);
	}

}
