package net.jqwik.engine.providers;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
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
		EmailArbitrary emailArbitrary = (EmailArbitrary) Arbitraries.emails();
		if(email.allowQuotedLocalPart()){
			emailArbitrary = emailArbitrary.quotedLocalParts();
		}
		if(email.allowUnquotedLocalPart()){
			emailArbitrary = emailArbitrary.unquotedLocalParts();
		}
		if(email.allowDomains()){
			emailArbitrary = emailArbitrary.domains();
		}
		if(email.allowIPv4()){
			emailArbitrary = emailArbitrary.ipv4Addresses();
		}
		if(email.allowIPv6()){
			emailArbitrary = emailArbitrary.ipv6Addresses();
		}
		return Collections.singleton(emailArbitrary);
	}

	@Override
	public int priority() {
		return 5;
	}
}
