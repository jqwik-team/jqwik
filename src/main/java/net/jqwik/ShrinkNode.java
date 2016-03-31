/*
 The MIT License

 Copyright (c) 2010-2016 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.jqwik;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.pholser.junit.quickcheck.internal.generator.PropertyParameterGenerationContext;

final class ShrinkNode {
	private final Method method;
	private final Class<?> testClass;
	private final List<PropertyParameterGenerationContext> params;
	private final Object[] args;
	private final int argIndex;
	private final int depth;

	private ShrinkNode(Method method, Class<?> testClass, List<PropertyParameterGenerationContext> params,
			Object[] args, int argIndex, int depth) {

		this.method = method;
		this.testClass = testClass;
		this.params = params;
		this.args = args;
		this.argIndex = argIndex;
		this.depth = depth;
	}

	static ShrinkNode root(Method method, Class<?> testClass, List<PropertyParameterGenerationContext> params,
			Object[] args) {

		return new ShrinkNode(method, testClass, params, args, 0, 0);
	}

	List<ShrinkNode> shrinks() {
		if (argIndex >= args.length)
			return Collections.emptyList();

		PropertyParameterGenerationContext param = params.get(argIndex);
		return param.shrink(args[argIndex]).stream().map(this::shrinkNodeFor).collect(Collectors.toList());
	}

	boolean verifyProperty() throws Throwable {
		boolean[] result = new boolean[1];

		property(result).verify();

		return result[0];
	}

	ShrinkNode advanceToNextArg() {
		return new ShrinkNode(method, testClass, params, args, argIndex + 1, depth);
	}

	PropertyVerificationFailure fail(PropertyVerificationFailure originalFailure) {
		PropertyVerificationFailure minimumFailure = new PropertyVerificationFailure(originalFailure.getDisplayName(),
			args, originalFailure);
		minimumFailure.setStackTrace(originalFailure.getStackTrace());
		throw minimumFailure;
	}

	boolean mightBePast(ShrinkNode other) {
		return argIndex >= other.argIndex && depth >= other.depth;
	}

	boolean deeperThan(int max) {
		return depth > max;
	}

	private PropertyVerifier property(boolean[] result) {
		return new PropertyVerifier(testClass, method, args, s -> result[0] = true, v -> result[0] = true,
			e -> result[0] = false);
	}

	private ShrinkNode shrinkNodeFor(Object shrunk) {
		Object[] shrunkArgs = new Object[args.length];
		System.arraycopy(args, 0, shrunkArgs, 0, args.length);
		shrunkArgs[argIndex] = shrunk;

		return new ShrinkNode(method, testClass, params, shrunkArgs, argIndex, depth + 1);
	}
}
