package net.jqwik.api.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public abstract class TestingSupportFacade {

	public static final TestingSupportFacade implementation;

	static {
		implementation = FacadeLoader.load(TestingSupportFacade.class);
	}

	public abstract <T extends @Nullable Object> Shrinkable<T> generateUntil(RandomGenerator<T> generator, Random random, Function<? super T, Boolean> condition);

	public abstract String singleLineReport(Object any);

	public abstract List<String> multiLineReport(Object any);


}
