package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

import org.jspecify.annotations.*;

/**
 * Is loaded through reflection in api module
 */
public class EdgeCasesFacadeImpl extends EdgeCases.EdgeCasesFacade {

	@Override
	public <T extends @Nullable Object> EdgeCases<T> fromSuppliers(final List<Supplier<Shrinkable<T>>> suppliers) {
		return EdgeCasesSupport.fromSuppliers(suppliers);
	}

}
