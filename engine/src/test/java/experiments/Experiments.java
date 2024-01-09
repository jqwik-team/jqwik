package experiments;

import java.util.*;

import org.junit.jupiter.api.extension.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class Experiments {

	@Property(tries= 10)
	@AddLifecycleHook(ProvideString.class)
	void test(String constant, @ForAll int i) {
		if (i > 1000) fail("too big");
	}
}

class ProvideString implements ResolveParameterHook {

	@Override
	public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext, LifecycleContext lifecycleContext) {
		return Optional.of(ignore -> "constant");
	}
}
