package jqwik.experiments;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

@Spec("An Adder")
@UsePlugIn(Sampling.class)
public class DataDrivenSpec {

	@Fact("can add two numbers")
	@Sampling.UseSample("validValues")
	boolean addingTwoNumbers(Adder anAdder, int number1, int number2, int sum) {
		return anAdder.add(number1, number2) == sum;
	}

	@Fixture
	Adder anAdder() {
		return new Adder();
	}

	@Sampling.Sample()
	List<Object[]> validValues() {
		return Arrays.asList(new Object[][]{
				{1, 1, 2},
				{2, 2, 4},
				{8, 2, 10},
				{4, 5, 9},
				{5, 5, 10}
		});	}
}

class Adder {
	int add(int a, int b) {
		return a + b;
	}
}

class Sampling implements SpecPlugin {

	//Not clear yet how to provide plugin capabilities

	public @interface Sample {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface UseSample {
		String value();
	}
}