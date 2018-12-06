package net.jqwik.engine.facades;

import java.lang.reflect.*;

import net.jqwik.api.providers.*;

/**
 * Is loaded through reflection in {@linkplain TypeUsage}
 */

public class TypeUsageFacadeImpl implements TypeUsage.TypeUsageFacade {

	@Override
	public TypeUsage of(Class<?> type, TypeUsage... typeParameters) {
		return null;
	}

	@Override
	public TypeUsage wildcard(TypeUsage upperBound) {
		return null;
	}

	@Override
	public TypeUsage forType(Type type) {
		return null;
	}
}
