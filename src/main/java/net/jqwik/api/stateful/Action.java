package net.jqwik.api.stateful;

public interface Action<M> {

	default boolean precondition(M model) {
		return true;
	}

	M run(M model);
}
