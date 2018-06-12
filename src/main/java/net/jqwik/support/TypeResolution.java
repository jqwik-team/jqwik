package net.jqwik.support;

import java.lang.reflect.*;

public class TypeResolution {
	private final Type resolvedType;
	private final boolean typeHasChanged;

	TypeResolution(Type resolvedType, boolean typeHasChanged) {
		this.resolvedType = resolvedType;
		this.typeHasChanged = typeHasChanged;
	}

	public Type type() {
		return resolvedType;
	}

	public boolean typeHasChanged() {
		return typeHasChanged;
	}

	public TypeResolution then(TypeResolution other) {
		return new TypeResolution(other.type(), typeHasChanged || other.typeHasChanged);
	}

	@Override
	public String toString() {
		return String.format("TypeResolution(%s:%s)", typeHasChanged, JqwikStringSupport.displayString(resolvedType));
	}
}
