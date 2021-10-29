package test.modular.api;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class ModularTests {
	@Example
	public boolean jqwikIsRunningOnTheModulePath() {
		return Example.class.getModule().isNamed();
	}

	@Example
	public boolean testsAreRunningOnTheModulePath() {
		return ModularTests.class.getModule().isNamed();
	}

	@Property(tries = 10)
	public boolean configuratorsCanBeAccessed(@ForAll @IntRange(max = 1000) int anInt) {
		return anInt >= 0 && anInt <= 1000;
	}


}
