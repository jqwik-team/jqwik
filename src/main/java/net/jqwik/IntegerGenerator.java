
package net.jqwik;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.jqwik.api.Max;
import net.jqwik.api.Min;

public class IntegerGenerator implements Generator<Integer> {

	private final Random random;

	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE - 1;

	public IntegerGenerator(Random random) {
		this.random = random;
	}

	@Override
	public Integer generate() {
		return random.ints(min, max + 1).findFirst().getAsInt();
	}

	@Override
	public List<Integer> shrink(Integer value) {
		if (value < 0) {
			return Arrays.asList(0, value / 2, value + 1);
		}
		else if (value > 0) {
			return Arrays.asList(0, value / 2, value - 1);
		}
		else
			return Collections.emptyList();
	}

	public void configure(Min min) {
		this.min = (int) min.value();
	}

	public void configure(Max max) {
		this.max = (int) max.value();
	}

}
