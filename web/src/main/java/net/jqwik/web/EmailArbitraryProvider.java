package net.jqwik.web;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.web.api.*;

public class EmailArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class) && targetType.findAnnotation(Email.class).isPresent();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Optional<Email> optionalEmail = targetType.findAnnotation(Email.class);
		return optionalEmail.map(email -> {
			EmailArbitrary emailArbitrary = Web.emails();
			if (email.quotedLocalPart()) {
				emailArbitrary = emailArbitrary.allowQuotedLocalPart();
			}
			if (email.ipv4Host()) {
				emailArbitrary = emailArbitrary.allowIpv4Host();
			}
			if (email.ipv6Host()) {
				emailArbitrary = emailArbitrary.allowIpv6Host();
			}
			return Collections.<Arbitrary<?>>singleton(emailArbitrary);
		}).orElse(Collections.emptySet());
	}

	@Override
	public int priority() {
		return 5;
	}
}
