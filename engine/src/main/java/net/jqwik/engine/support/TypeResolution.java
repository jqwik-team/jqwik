package net.jqwik.engine.support;

import java.lang.reflect.*;

public class TypeResolution {
	private final Type resolvedType;
	private final AnnotatedType annotatedType;
	private final boolean typeHasChanged;

	TypeResolution(Type resolvedType, AnnotatedType annotatedType, boolean typeHasChanged) {
		this.resolvedType = resolvedType;
		this.annotatedType = annotatedType;
		this.typeHasChanged = typeHasChanged;
	}

	public boolean isVariable() {
		return type() instanceof TypeVariable;
	}

	public Type type() {
		return resolvedType;
	}

	public boolean typeHasChanged() {
		return typeHasChanged;
	}

	public AnnotatedType annotatedType() {
		return annotatedType;
	}

	@Override
	public String toString() {
		return String.format("TypeResolution(%s:%s)", typeHasChanged, JqwikStringSupport.displayString(resolvedType));
	}

	TypeResolution unchanged() {
		return new TypeResolution(resolvedType, annotatedType, false);
	}
}
