package net.jqwik.api;

import org.opentest4j.*;

import java.util.function.*;

@FunctionalInterface
public interface Falsifier<T> extends Predicate<T> {

	default Falsifier<T> withFilter(Predicate<T> filter) {
		return t -> {
			if (!filter.test(t)) {
				throw new TestAbortedException();
			}
			return this.test(t);
		};
	}

	default FalsificationResult<T> falsify(NShrinkable<T> candidate) {
		try {
			boolean falsified = !test(candidate.value());
			if (falsified) {
				return FalsificationResult.falsified(candidate, null);
			} else {
				return FalsificationResult.notFalsified(candidate);
			}
		} catch (TestAbortedException tae) {
			return FalsificationResult.filtered(candidate);
		} catch (Throwable throwable) {
			return FalsificationResult.falsified(candidate, throwable);
		}
	}

}
