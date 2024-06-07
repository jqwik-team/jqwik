package net.jqwik.engine.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import org.jspecify.annotations.*;

interface ActionGenerator<M extends @Nullable Object> {

	Action<M> next(M model);

	List<Shrinkable<Action<M>>> generated();
}
