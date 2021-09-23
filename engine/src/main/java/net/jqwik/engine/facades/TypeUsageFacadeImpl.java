package net.jqwik.engine.facades;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

/**
 * Is loaded through reflection in api module
 */
public class TypeUsageFacadeImpl extends TypeUsage.TypeUsageFacade {

	@Override
	public TypeUsage of(Class<?> type, TypeUsage... typeParameters) {
		if (typeParameters.length > 0 && typeParameters.length != type.getTypeParameters().length) {
			String typeArgumentsString = JqwikStringSupport.displayString(typeParameters);
			throw new JqwikException(String.format("Type [%s] cannot have type parameters [%s]", type, typeArgumentsString));
		}
		TypeUsageImpl typeUsage = new TypeUsageImpl(type, type, null, null, Collections.emptyList());
		typeUsage.addTypeArguments(Arrays.asList(typeParameters));
		return typeUsage;
	}

	@Override
	public TypeUsage wildcard(TypeUsage upperBound) {
		TypeUsageImpl typeUsage = new TypeUsageImpl(
			Object.class, Object.class, null, TypeUsageImpl.WILDCARD, Collections.emptyList()
		);
		typeUsage.addUpperBounds(Arrays.asList(upperBound));
		return typeUsage;
	}

	@Override
	public TypeUsage forType(Type type) {
		if (type instanceof WildcardType) {
			return TypeUsageImpl.forWildcard((WildcardType) type);
		}
		return TypeUsageImpl.forNonWildcardType(type);
	}

}
