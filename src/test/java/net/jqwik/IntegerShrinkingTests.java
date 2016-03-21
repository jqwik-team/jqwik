package net.jqwik;

import org.junit.gen5.api.Test;

import java.util.Random;

import static org.junit.gen5.api.Assertions.assertEquals;

class IntegerShrinkingTests {

	IntegerGenerator generator = new IntegerGenerator(new Random());
	Shrinker<Integer> shrinker = new Shrinker(generator);


	@Test
	void shrinkToZero() throws Exception {
		assertEquals(0, (int) shrinker.shrink(1, number -> number < 0));
		assertEquals(0, (int) shrinker.shrink(10, number -> number < 0));
		assertEquals(0, (int) shrinker.shrink(Integer.MAX_VALUE, number -> number < 0));

		assertEquals(0, (int) shrinker.shrink(-1, number -> number > 0));
		assertEquals(0, (int) shrinker.shrink(-10, number -> number > 0));
		assertEquals(0, (int) shrinker.shrink(Integer.MIN_VALUE, number -> number > 0));
	}

	@Test
	void shrinksToLowestPossiblePositiveValue() {
		assertEquals(1, (int) shrinker.shrink(2, number -> number <= 0));
		assertEquals(1, (int) shrinker.shrink(10, number -> number <= 0));

		assertEquals(43, (int) shrinker.shrink(43, number -> number <= 42));
		assertEquals(43, (int) shrinker.shrink(44, number -> number <= 42));
		assertEquals(43, (int) shrinker.shrink(90, number -> number <= 42));
		assertEquals(43, (int) shrinker.shrink(Integer.MAX_VALUE, number -> number <= 42));
	}

	@Test
	void shrinksToHighestPossibleNegativeValue() {
		assertEquals(-1, (int) shrinker.shrink(-2, number -> number >= 0));
		assertEquals(-1, (int) shrinker.shrink(-10, number -> number >= 0));

		assertEquals(-43, (int) shrinker.shrink(-43, number -> number >= -42));
		assertEquals(-43, (int) shrinker.shrink(-44, number -> number >= -42));
		assertEquals(-43, (int) shrinker.shrink(-90, number -> number >= -42));
		assertEquals(-43, (int) shrinker.shrink(Integer.MIN_VALUE, number -> number >= -42));
	}
}
