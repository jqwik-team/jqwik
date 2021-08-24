package net.jqwik.web;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.web.api.*;

public class WebDomainArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class) && targetType.findAnnotation(WebDomain.class).isPresent();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.<Arbitrary<?>>singleton(Web.webDomains());
	}

	@Override
	public int priority() {
		return 5;
	}
}
