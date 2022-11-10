package net.jqwik.docs.arbitraryconfigurator;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

public class NotNullConfigurator extends ArbitraryConfiguratorBase {
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, NotNull notNull) {
		return arbitrary.filter(Objects::nonNull);
	}
}
