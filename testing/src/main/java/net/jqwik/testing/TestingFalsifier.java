package net.jqwik.testing;

import java.util.function.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public interface TestingFalsifier<T> extends Falsifier<T>, Predicate<T> {

	static <T> TestingFalsifier<T> alwaysFalsify() {
		return ignore -> false;
	}

	static <T> TestingFalsifier<T> falsifier(Predicate<T> predicate) {
		return predicate::test;
	}

	@Override
	default TryExecutionResult execute(T parameters) {
		try {
			boolean result = this.test(parameters);
			return result ? TryExecutionResult.satisfied() : TryExecutionResult.falsified(null);
		} catch (TestAbortedException tea) {
			return TryExecutionResult.invalid();
		} catch (AssertionError | Exception e) {
			return TryExecutionResult.falsified(e);
		}
	}

}
