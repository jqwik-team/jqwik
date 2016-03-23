
package net.jqwik.api;

import net.jqwik.Generator;

public class MissingGeneratorConstructor extends RuntimeException {
	public MissingGeneratorConstructor(Class<? extends Generator> generator) {
		super(String.format("Generator '%s' should have a public constructor accepting a java.util.Random object",
			generator.getName()));
	}
}
