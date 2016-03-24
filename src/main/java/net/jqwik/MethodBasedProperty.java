
package net.jqwik;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.jqwik.api.MissingGeneratorConstructor;
import net.jqwik.api.ParameterConstraintViolation;

import org.junit.gen5.commons.util.ReflectionUtils;
import org.junit.gen5.commons.util.StringUtils;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

public class MethodBasedProperty implements ExecutableProperty {

	private static final Logger LOG = Logger.getLogger(MethodBasedProperty.class.getName());

	private final Class<?> testClass;
	private final Method propertyMethod;
	private final int numberOfTrials;
	private final Random random;

	private final Map<Class<?>, Class<? extends Generator>> generatorClasses = new HashMap<>();

	public MethodBasedProperty(Class<?> testClass, Method propertyMethod, int numberOfTrials, long randomSeed) {
		this.testClass = testClass;
		this.propertyMethod = propertyMethod;
		this.numberOfTrials = numberOfTrials;
		this.random = new Random(randomSeed);
		generatorClasses.put(Integer.class, IntegerGenerator.class);
		generatorClasses.put(int.class, IntegerGenerator.class);
	}

	@Override
	public String name() {
		return propertyMethod.getName();
	}

	@Override
	public void evaluate() {
		List<Generator> parameterGenerators = getParameterGenerators();
		long finalNumberOfValues = calculateFinalNumberOfValues(parameterGenerators);
		//		System.out.println("FNOV: " + finalNumberOfValues);
		if (finalNumberOfValues > 0 && finalNumberOfValues <= numberOfTrials) {
			evaluateAllValues(parameterGenerators);
		}
		else {
			evaluateAllNumberOfTrials(parameterGenerators);
		}
	}

	private void evaluateAllValues(List<Generator> parameterGenerators) {
		Stream<Object[]> allParameterCombinations = createAllParameterCombinations(parameterGenerators);
		allParameterCombinations.forEach(parameters -> {
			try {
				boolean propertyFailed = !evaluate(parameters);
				if (propertyFailed) {
					Object[] shrinkedParameters = shrinkParameters(parameters,
							parameterGenerators.toArray(new Generator[0]));
					String message = String.format("Failed with parameters: [%s]",
							parameterDescription(shrinkedParameters));
					AssertionFailedError assertionFailedError = new AssertionFailedError(message);
					throw assertionFailedError;
				}
			}
			catch (ParameterConstraintViolation pcv) {
			}
		});
	}

	private Stream<Object[]> createAllParameterCombinations(List<Generator> parameterGenerators) {
		if (parameterGenerators.isEmpty())
			return Collections.singletonList(new Object[0]).stream();

		//Todo: Only works for one generator
		Generator generator = parameterGenerators.get(0);
		return generator.generateAll().map(param -> new Object[] {param});
	}

	private long calculateFinalNumberOfValues(List<Generator> parameterGenerators) {
		long finalNumberOfValues = 1;
		for (Generator generator : parameterGenerators) {
			long finalNumber = (long) generator.finalNumberOfValues().orElse(0);
			finalNumberOfValues *= finalNumber;
		}

		return finalNumberOfValues;
	}

	private void evaluateAllNumberOfTrials(List<Generator> parameterGenerators) {
		for (int i = 0; i < numberOfTrials; i++) {

			boolean propertyFailed;
			Object[] parameters;

			int maxMisses = numberOfTrials * 10;
			int countMisses = 0;
			while (true) {
				parameters = nextParameters(parameterGenerators.toArray(new Generator[0]));
				try {
					propertyFailed = !evaluate(parameters);
					break;
				}
				catch (ParameterConstraintViolation pcv) {
					countMisses++;
					if (countMisses >= maxMisses)
						throw new TestAbortedException("Too many misses trying to create parameters.");
					continue;
				}
			}

			if (propertyFailed) {
				Object[] shrinkedParameters = shrinkParameters(parameters,
					parameterGenerators.toArray(new Generator[0]));
				String message = String.format("Failed with parameters: [%s]",
					parameterDescription(shrinkedParameters));
				AssertionFailedError assertionFailedError = new AssertionFailedError(message);
				throw assertionFailedError;
			}
		}
	}

	private Object[] shrinkParameters(Object[] parameters, Generator[] parameterGenerators) {
		List<Object> lastFailingSet = Arrays.asList(parameters);

		for (int i = 0; i < parameters.length; i++) {
			final int index = i;
			Object currentValue = parameters[i];
			Generator currentGenerator = parameterGenerators[i];
			Function<Object, Boolean> evaluator = value -> {
				List<Object> params = new ArrayList<>(lastFailingSet);
				params.set(index, value);
				return evaluate(params.toArray());
			};
			Object shrinkedValue = shrinkValue(currentValue, currentGenerator, evaluator);
			lastFailingSet.set(i, shrinkedValue);
		}

		return lastFailingSet.toArray();
	}

	private Object shrinkValue(Object currentValue, Generator currentGenerator, Function<Object, Boolean> evaluator) {
		return new Shrinker(currentGenerator).shrink(currentValue, evaluator);
	}

	private boolean evaluate(Object[] parameters) {
		LOG.warning(() -> String.format("Run method '%s' with parameters: [%s]", methodDescription(),
			parameterDescription(parameters)));
		Object testInstance = null;
		if (!ReflectionUtils.isStatic(propertyMethod))
			testInstance = ReflectionUtils.newInstance(testClass);
		return (boolean) ReflectionUtils.invokeMethod(propertyMethod, testInstance, parameters);
	}

	private String parameterDescription(Object[] parameters) {
		Stream<String> parameterStrings = Arrays.stream(parameters).map(Object::toString);
		return StringUtils.join(parameterStrings, ",");
	}

	private String methodDescription() {
		return propertyMethod.getName();
	}

	private Object[] nextParameters(Generator[] parameterGenerators) {
		return Arrays.stream(parameterGenerators) //
		.map(generator -> generator.generate()) //
		.toArray();
	}

	private List<Generator> getParameterGenerators() {
		return Arrays.stream(propertyMethod.getParameters()) //
		.map(parameter -> {
			Generator generator = createGenerator(parameter).orElseThrow(() -> {
				String message = String.format("Cannot generate values for paramters of type '%s'",
					parameter.getType());
				return new RuntimeException(message);
			});
			return generator;
		}).collect(Collectors.toList());
	}

	private Optional<Generator> createGenerator(Parameter parameter) {
		Class<? extends Generator> generatorClass = generatorClasses.get(parameter.getType());
		if (generatorClass == null)
			return Optional.empty();
		try {
			Generator generator = ReflectionUtils.newInstance(generatorClass, random);
			Arrays.stream(parameter.getDeclaredAnnotations()).forEach(annotation -> {
				Optional<Method> method = ReflectionUtils.findMethod(generatorClass, "configure",
					annotation.annotationType());
				method.ifPresent(m -> {
					ReflectionUtils.invokeMethod(m, generator, annotation);
				});
			});
			return Optional.of(generator);
		}
		catch (Exception e)

		{
			if (e.getClass() == NoSuchMethodException.class)
				throw new MissingGeneratorConstructor(generatorClass);
			throw e;
		}
	}
}
