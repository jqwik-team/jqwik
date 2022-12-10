package net.jqwik.api.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public abstract class TestingSupportFacade {

	public static final TestingSupportFacade implementation;

	static {
		implementation = FacadeLoader.load(TestingSupportFacade.class);
	}

	public abstract <T> Shrinkable<T> generateUntil(RandomGenerator<T> generator, JqwikRandom random, Function<T, Boolean> condition);

	public abstract String singleLineReport(Object any);

	public abstract List<String> multiLineReport(Object any);


}
