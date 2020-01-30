package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public interface ShrinkablesGenerator extends Iterator<List<Shrinkable<Object>>> {

	default ShrinkablesGenerator andThen(Supplier<ShrinkablesGenerator> generatorCreator) {
		ShrinkablesGenerator first = this;
		ShrinkablesGenerator afterSuccessGenerator = generatorCreator.get();
		return new ShrinkablesGenerator() {
			@Override
			public boolean hasNext() {
				if (first.hasNext()) {
					return true;
				}
				return afterSuccessGenerator.hasNext();
			}

			@Override
			public List<Shrinkable<Object>> next() {
				if (first.hasNext()) {
					return first.next();
				}
				return afterSuccessGenerator.next();
			}
		};
	}
}
