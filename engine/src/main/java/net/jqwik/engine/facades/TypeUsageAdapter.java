package net.jqwik.engine.facades;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.providers.*;

public class TypeUsageAdapter implements TypeUsage {
	private final TypeUsageImpl delegate;

	public TypeUsageAdapter(TypeUsageImpl delegate) {this.delegate = delegate;}

	@Override
	public Class<?> getRawType() {
		return delegate.getRawType();
	}

	@Override
	public List<TypeUsage> getUpperBounds() {
		return delegate.getUpperBounds();
	}

	@Override
	public List<TypeUsage> getLowerBounds() {
		return delegate.getLowerBounds();
	}

	@Override
	public boolean isWildcard() {
		return delegate.isWildcard();
	}

	@Override
	public boolean isTypeVariable() {
		return delegate.isTypeVariable();
	}

	@Override
	public boolean isTypeVariableOrWildcard() {
		return delegate.isTypeVariableOrWildcard();
	}

	@Override
	public List<TypeUsage> getTypeArguments() {
		return delegate.getTypeArguments();
	}

	@Override
	public TypeUsage getTypeArgument(int position) {
		return delegate.getTypeArgument(position);
	}

	@Override
	public boolean isOfType(Class<?> aRawType) {
		return delegate.isOfType(aRawType);
	}

	@Override
	public boolean canBeAssignedTo(TypeUsage targetType) {
		return delegate.canBeAssignedTo(targetType);
	}

	@Override
	public boolean isGeneric() {
		return delegate.isGeneric();
	}

	@Override
	public boolean isEnum() {
		return delegate.isEnum();
	}

	@Override
	public boolean isArray() {
		return delegate.isArray();
	}

	@Override
	public List<Annotation> getAnnotations() {
		return delegate.getAnnotations();
	}

	@Override
	public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
		return delegate.findAnnotation(annotationType);
	}

	@Override
	public <A extends Annotation> boolean isAnnotated(Class<A> annotationType) {
		return delegate.isAnnotated(annotationType);
	}

	@Override
	public boolean isAssignableFrom(Class<?> providedClass) {
		return delegate.isAssignableFrom(providedClass);
	}

	@Override
	public Optional<TypeUsage> getComponentType() {
		return delegate.getComponentType();
	}

	@Override
	public boolean isVoid() {
		return false;
	}

	@Override
	public Optional<TypeUsage> getSuperclass() {
		return delegate.getSuperclass();
	}

	@Override
	public List<TypeUsage> getInterfaces() {
		return delegate.getInterfaces();
	}

	@Override
	public Type getType() {
		return delegate.getType();
	}

	@Override
	public AnnotatedType getAnnotatedType() {
		return delegate.getAnnotatedType();
	}

	@Override
	public TypeUsage asNullable() {
		return delegate.asNullable();
	}

	@Override
	public TypeUsage asNotNullable() {
		return delegate.asNotNullable();
	}

	@Override
	public String getTypeVariable() {
		return delegate.getTypeVariable();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TypeUsageAdapter that = (TypeUsageAdapter) o;

		return delegate.equals(that.delegate);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		return TypeUsageToString.toString(this);
	}
}
