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
		Optional<Email> optionalEmail = targetType.findAnnotation(Email.class);
		return optionalEmail.map(email -> {
			checkValidEmailConfiguration(email);
			EmailArbitrary emailArbitrary = Arbitraries.emails();
			if (email.quotedLocalPart()) {
				emailArbitrary = emailArbitrary.quotedLocalPart();
			}
			if (email.unquotedLocalPart()) {
				emailArbitrary = emailArbitrary.unquotedLocalPart();
			}
			if (email.domainHost()) {
				emailArbitrary = emailArbitrary.domainHost();
			}
			if (email.ipv4Host()) {
				emailArbitrary = emailArbitrary.ipv4Host();
			}
			if (email.ipv6Host()) {
				emailArbitrary = emailArbitrary.ipv6Host();
			}
			return Collections.<Arbitrary<?>>singleton(emailArbitrary);
		}).orElse(Collections.emptySet());
	}

	public void checkValidEmailConfiguration(Email email) {
		if (!email.quotedLocalPart() && !email.unquotedLocalPart()) {
			String message = "Email addresses require a quoted or unquoted local part.";
			throw new JqwikException(message);
		}
		if (!email.domainHost() && !email.ipv4Host() && !email.ipv6Host()) {
			String message = "Email addresses require some kind of host.";
			throw new JqwikException(message);
		}
	}

	@Override
	public int priority() {
		return 5;
	}
}
