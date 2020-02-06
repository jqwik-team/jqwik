package net.jqwik.api;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@API(status = DEPRECATED, since = "1.2.4")
@Deprecated
public
interface LegacyFalsifier<T> extends Falsifier<T> {
	boolean test(T t);

	@Override
	default TryExecutionResult executeTry(T parameters) {
		try {
			boolean result = this.test(parameters);
			return result ? TryExecutionResult.satisfied() : TryExecutionResult.falsified(null);
		} catch (TestAbortedException tea) {
			return TryExecutionResult.invalid();
		} catch (AssertionError | Exception e) {
			return TryExecutionResult.falsified(e);
		} catch (Throwable throwable) {
			throwAs(throwable);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	static <T extends Throwable> void throwAs(Throwable t) throws T {
		throw (T) t;
	}

}
