package test.modular.api;

import net.jqwik.api.Example;

public class ModularTests {
	@Example
	public boolean jqwikIsRunningOnTheModulePath() {
		return Example.class.getModule().isNamed();
	}

	@Example
	public boolean testsAreRunningOnTheModulePath() {
		return ModularTests.class.getModule().isNamed();
	}
}
