package net.jqwik.api.domains;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

@Domain(IntsAre42.class)
public class ContextWithDomainAnnotation extends ParentDomainContext implements DomainContext {

	@Override
	public Collection<ArbitraryProvider> getArbitraryProviders() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ArbitraryConfigurator> getArbitraryConfigurators() {
		return Collections.emptyList();
	}
}

@Domain(StringsAreHello.class)
class ParentDomainContext {

}

class IntsAre42 extends DomainContextBase {
	@Provide
	Arbitrary<Integer> ints() {
		return Arbitraries.integers().map(ignore -> 42);
	}
}

class StringsAreHello extends DomainContextBase {
	@Provide
	Arbitrary<String> strings() {
		return Arbitraries.integers().map(ignore -> "hello");
	}
}