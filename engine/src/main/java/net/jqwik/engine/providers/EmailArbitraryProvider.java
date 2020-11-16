package net.jqwik.engine.providers;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import java.util.*;

public class EmailArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class) && targetType.findAnnotation(Email.class).isPresent();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider) {
		Email email = targetType.findAnnotation(Email.class).get();
		boolean allowQuotedLocalPart = email.allowQuotedLocalPart();
		boolean allowUnquotedLocalPart = email.allowUnquotedLocalPart();
		boolean allowDomains = email.allowDomains();
		boolean allowIPv4 = email.allowIPv4();
		boolean allowIPv6 = email.allowIPv6();
		return Collections.singleton(Arbitraries.emails());
	}

	@Override
	public int priority() {
		return 5;
	}
}
