package net.jqwik.api.facades;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public abstract class TestingSupportFacade {
	public static final TestingSupportFacade implementation;

	static {
		implementation = FacadeLoader.load(TestingSupportFacade.class);
	}

	public abstract <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random, Falsifier<T> falsifier);
}
