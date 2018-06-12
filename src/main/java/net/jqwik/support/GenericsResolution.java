package net.jqwik.support;

import java.lang.reflect.*;

public class GenericsResolution {
	private final Type resolvedType;
	private final boolean typeHasChanged;

	GenericsResolution(Type resolvedType, boolean typeHasChanged) {
		this.resolvedType = resolvedType;
		this.typeHasChanged = typeHasChanged;
	}

	public Type type() {
		return resolvedType;
	}

	public boolean typeHasChanged() {
		return typeHasChanged;
	}
}
