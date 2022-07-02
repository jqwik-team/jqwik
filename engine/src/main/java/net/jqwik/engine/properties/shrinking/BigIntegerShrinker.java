package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class BigIntegerShrinker {

	private final BigInteger shrinkingTarget;

	public BigIntegerShrinker(BigInteger shrinkingTarget) {
		this.shrinkingTarget = shrinkingTarget;
	}

	public Stream<BigInteger> shrink(BigInteger value) {
		Set<BigInteger> candidates = new LinkedHashSet<>();
		BigInteger lower = shrinkingTarget.min(value);
		BigInteger higher = shrinkingTarget.max(value);
		addFibbonaci(candidates, lower, BigInteger.valueOf(0), BigInteger.valueOf(1), higher);
		subFibbonaci(candidates, higher, BigInteger.valueOf(0), BigInteger.valueOf(1), lower);
		candidates.add(shrinkingTarget);
		candidates.remove(value);
		return candidates.stream();
	}

	private void subFibbonaci(
		Set<BigInteger> candidates, BigInteger target, BigInteger butLast, BigInteger last, BigInteger border
	) {
		while (true) {
			BigInteger step = butLast.add(last);
			BigInteger candidate = target.subtract(step);
			if (candidate.compareTo(border) <= 0) {
				break;
			}
			candidates.add(candidate);
			butLast = last;
			last = step;
		}
	}

	private void addFibbonaci(
		Set<BigInteger> candidates, BigInteger target, BigInteger butLast, BigInteger last, BigInteger border
	) {
		while (true) {
			BigInteger step = butLast.add(last);
			BigInteger candidate = target.add(step);
			if (candidate.compareTo(border) >= 0) {
				break;
			}
			candidates.add(candidate);
			butLast = last;
			last = step;
		}
	}

}
