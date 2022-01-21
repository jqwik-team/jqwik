package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.configurators.*;
import net.jqwik.engine.providers.*;

public class CurrentDomainContext {

	public static final DomainContext GLOBAL_DOMAIN_CONTEXT = new GlobalDomainContext();

	private static final ThreadLocal<DomainContext> currentContext = new ThreadLocal<>();

	public static DomainContext get() {
		if (currentContext.get() == null) {
			return GLOBAL_DOMAIN_CONTEXT;
		}
		return currentContext.get();
	}

	public static <T> T runWithContext(DomainContext context, Supplier<T> runnable) {
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
		public List<ArbitraryProvider> getArbitraryProviders() {
			return RegisteredArbitraryProviders.getProviders();
		}

		@Override
		public List<ArbitraryConfigurator> getArbitraryConfigurators() {
			return RegisteredArbitraryConfigurators.getConfigurators();
		}
	}

}
