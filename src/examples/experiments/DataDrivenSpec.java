package experiments;

import experiments.Sampling.*;

@Spec("An Adder")
@UsePlugIn(Sampling.class)
public class DataDrivenSpec {

	@Fact("can add two numbers")
	@Sampling.Use("validValues")
	boolean addingTwoNumbers(Adder anAdder, @Param int number1, @Param int number2, @Param int sum) {
		return anAdder.add(number1, number2) == sum;
	}

	@Fixture
	Adder anAdder() {
		return new Adder();
	}

	@Sampling.Sample()
	List<Tuple3<Integer, Integer, Integer>> validValues() {
		return Tuple3.list(
				Tuple3.of(1, 2, 3),
				Tuple3.of(4, 5, 9)
		);
	}
}

class Adder {
	int add(int a, int b) {
		return a + b;
	}
}

class Tuple3<T1, T2, T3> {

	static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 v1, T2 v2, T3 v3) {
		return new Tuple3(v1, v2, v3);
	}

	static <T1, T2, T3> List<Tuple3<T1, T2, T3>> list(Tuple3... tuples) {
		return Arrays.asList(tuples);
	}

	static <T1, T2, T3> Stream<Tuple3<T1, T2, T3> > stream(Tuple3... tuples) {
		return Arrays.stream(tuples);
	}

	Tuple3(T1 v1, T2 v2, T3 v3) {

	}
}

class Sampling implements SpecPlugin {

	// Not clear yet how to provide plugin capabilities

	public @interface Sample {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Use {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Param {
	}
}