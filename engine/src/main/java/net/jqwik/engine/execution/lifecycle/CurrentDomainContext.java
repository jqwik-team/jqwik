package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.configurators.*;
import net.jqwik.engine.providers.*;

import org.jspecify.annotations.*;

public class CurrentDomainContext {

	public static final DomainContext GLOBAL_DOMAIN_CONTEXT = new GlobalDomainContext();

	private static final ThreadLocal<DomainContext> currentContext = new ThreadLocal<>();

	public static DomainContext get() {
		if (currentContext.get() == null) {
			return GLOBAL_DOMAIN_CONTEXT;
		}
		return currentContext.get();
	}

	public static <T extends @Nullable Object> T runWithContext(DomainContext context, Supplier<? extends T> runnable) {
		currentContext.set(context);
		try {
			return runnable.get();
		} finally {
			if (currentContext.get() == context) {
				currentContext.remove();
			}
		}
	}

	private static class GlobalDomainContext implements DomainContext {

		@Override
		public Collection<ArbitraryProvider> getArbitraryProviders() {
			return RegisteredArbitraryProviders.getProviders();
		}

		@Override
		public Collection<ArbitraryConfigurator> getArbitraryConfigurators() {
			return RegisteredArbitraryConfigurators.getConfigurators();
		}
	}

}
