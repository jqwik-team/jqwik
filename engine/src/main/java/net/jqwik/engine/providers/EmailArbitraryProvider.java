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
			emailArbitrary = emailArbitrary.quotedLocalParts();
		}
		if(email.unquotedLocalPart()){
			emailArbitrary = emailArbitrary.unquotedLocalParts();
		}
		if(email.domains()){
			emailArbitrary = emailArbitrary.domains();
		}
		if(email.ipv4Addresses()){
			emailArbitrary = emailArbitrary.ipv4Addresses();
		}
		if(email.ipv6Addresses()){
			emailArbitrary = emailArbitrary.ipv6Addresses();
		}
		return Collections.singleton(emailArbitrary);
	}

	@Override
	public int priority() {
		return 5;
	}
}
