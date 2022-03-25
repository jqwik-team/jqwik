package net.jqwik.api.state;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ActionChainArbitrary<S> extends Arbitrary<ActionChain<S>> {

	ActionChainArbitrary<S> withMaxTransformations(int maxSize);

}
