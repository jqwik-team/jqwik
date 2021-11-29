package net.jqwik.engine.facades;

import java.lang.reflect.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.types.*;

/**
 * Is loaded through reflection in api module
 */
public class TypeUsageFacadeImpl extends TypeUsage.TypeUsageFacade {

	@Override
	public TypeUsage of(Class<?> type, TypeUsage... typeParameters) {
		return TypeUsageImpl.forParameterizedClass(Tuple.of(type, typeParameters));
	}

	@Override
	public TypeUsage wildcardOf(TypeUsage upperBound) {
		return TypeUsageImpl.wildcardOf(upperBound);
	}

	@Override
	public TypeUsage forType(Type type) {
		return TypeUsageImpl.forType(type);
	}

}
