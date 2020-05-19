package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public interface ForAllParametersGenerator extends Iterator<List<Shrinkable<Object>>> {

	default ForAllParametersGenerator andThen(Supplier<ForAllParametersGenerator> generatorCreator) {
		ForAllParametersGenerator first = this;
		ForAllParametersGenerator afterSuccessGenerator = generatorCreator.get();
		return new ForAllParametersGenerator() {
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

			@Override
			public int edgeCasesTotal() {
				if (first.hasNext()) {
					return first.edgeCasesTotal();
				}
				return afterSuccessGenerator.edgeCasesTotal();
			}

			@Override
			public int edgeCasesTried() {
				if (first.hasNext()) {
					return first.edgeCasesTried();
				}
				return afterSuccessGenerator.edgeCasesTried();
			}
		};
	}

	default int edgeCasesTotal() {
		return 0;
	}

	default int edgeCasesTried() {
		return 0;
	}

}
