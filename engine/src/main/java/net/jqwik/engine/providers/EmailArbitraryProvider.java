package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class EmailArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class) && targetType.findAnnotation(Email.class).isPresent();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider) {
		Email email = targetType.findAnnotation(Email.class).get();
		EmailArbitrary emailArbitrary = Arbitraries.emails();
		if(email.quotedLocalPart()){
			emailArbitrary = emailArbitrary.quotedLocalPart();
		}
		if(email.unquotedLocalPart()){
			emailArbitrary = emailArbitrary.unquotedLocalPart();
		}
		if(email.domain()){
			emailArbitrary = emailArbitrary.domain();
		}
		if(email.ipv4Address()){
			emailArbitrary = emailArbitrary.ipv4Address();
		}
		if(email.ipv6Address()){
			emailArbitrary = emailArbitrary.ipv6Address();
		}
		return Collections.singleton(emailArbitrary);
	}

	@Override
	public int priority() {
		return 5;
	}
}
