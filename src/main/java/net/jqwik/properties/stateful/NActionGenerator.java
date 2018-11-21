package net.jqwik.properties.stateful;

import net.jqwik.api.stateful.*;

public interface NActionGenerator<M> {

	Action<M> next(M model);
}
