package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import static org.apiguardian.api.API.Status.*;

@FunctionalInterface
@API(status = STABLE, since = "1.0")
public interface Falsifier<T> extends Predicate<T> {

	@API(status = INTERNAL)
	default Falsifier<T> withFilter(Predicate<T> filter) {
		return t -> {
			if (!filter.test(t)) {
				throw new TestAbortedException();
			}
			return this.test(t);
		};
	}

	@API(status = INTERNAL)
	default Falsifier<T> withPostFilter(Predicate<T> filter) {
		return t -> {
			try {
				boolean result = this.test(t);
				return result;
			} finally {
				if (!filter.test(t)) {
					throw new TestAbortedException();
				}
			}
		};
	}

	@API(status = INTERNAL)
	default FalsificationResult<T> falsify(Shrinkable<T> candidate) {
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
