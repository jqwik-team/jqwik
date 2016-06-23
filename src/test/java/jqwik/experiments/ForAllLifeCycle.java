package jqwik.experiments;

public interface ForAllLifeCycle {

	default void beforeAll() {}

	default void afterAll() {}

}
