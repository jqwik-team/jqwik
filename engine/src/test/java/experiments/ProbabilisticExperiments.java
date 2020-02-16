package experiments;

import org.assertj.core.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

class ProbabilisticExperiments {

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	void squareRoot(@ForAll @DoubleRange(min = 0) double aDouble) {
		double sqrt = Math.sqrt(aDouble);
		double square = sqrt * sqrt;
		ProbabilisticAssertions.assertMinimumPercentage(50, () -> {
			Assertions.assertThat(square).isEqualTo(aDouble);
		});
	}
}

class ProbabilisticAssertions {

	public static void assertMinimumPercentage(double targetPercentage, Runnable assertions) {
		Store<Integer> countAll = Store.getOrCreate(Tuple.of(assertions.getClass(), "countAll"), Lifespan.PROPERTY, () -> 0);
		Store<Integer> countSuccess = Store.getOrCreate(Tuple.of(assertions.getClass(), "countSuccess"), Lifespan.PROPERTY, () -> 0);
		try {
			assertions.run();
			countAll.update(i -> i + 1);
			countSuccess.update(i -> i + 1);
		} catch (AssertionError failure) {
			countAll.update(i -> i + 1);
		}

//		PropertyLifecycle.onSuccess(countSuccess, () -> {
//			double percentage = countSuccess.get() * 100.0 / countAll.get();
//			if (percentage < targetPercentage) {
//				String message = String.format("[%s] is below minimum percentage [%s]", percentage, targetPercentage);
//				throw new AssertionFailedError(message);
//			}
//		});
	}
}