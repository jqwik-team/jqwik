package net.jqwik.api.stateful;

import net.jqwik.api.*;

import java.util.*;

public interface StateMachine<M> {

	List<Arbitrary<Action<M>>> actions();

	M createModel();

}
