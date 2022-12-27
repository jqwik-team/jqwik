package net.jqwik.api.support;

import java.io.*;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;
import org.junit.platform.commons.support.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class LambdaSupport {

	private LambdaSupport() {}

	private static class FieldAccessor {
		final boolean isFunctionalType;
		final MethodHandle handle;

		FieldAccessor(boolean isFunctionalType, MethodHandle handle) {
			this.isFunctionalType = isFunctionalType;
			this.handle = handle;
		}
	}

	/**
	 * Returns field accessor for the given class, or {@code null} if accessors are not available (e.g. no reflective access to the class)
	 */
	private static final ClassValue<List<FieldAccessor>> HANDLES = new ClassValue<List<FieldAccessor>>() {
		@Override
		protected @Nullable List<FieldAccessor> computeValue(Class<?> type) {
			Field[] fields = type.getDeclaredFields();
			List<FieldAccessor> res = new ArrayList<>(fields.length);
			try {
				for (Field field : fields) {
					// Javadoc of LOOKUP.unreflectGetter(..) suggests that this may be necessary in some cases:
					field.setAccessible(true);
					res.add(new FieldAccessor(isFunctionalType(field.getType()), LOOKUP.unreflectGetter(field)));
				}
			} catch (Throwable e) {
				// As of Java 17, field.setAccessible(..) throws IllegalAccessException
				// if the field is private and not opened to jqwik, Predicate.not() will create lambda instances with private fields.
				return null;
			}
			return res;
		}
	};

	/**
	 * This method is used in {@linkplain Object#equals(Object)} implementations of {@linkplain Arbitrary} types
	 * to allow memoization of generators.
	 * <p>
	 * Comparing two lambdas by their implementation class works if they don't access an enclosing object's state.
	 * When in doubt, fail comparison.
	 **/
	public static <T> boolean areEqual(T l1, T l2) {
		if (l1 == l2) return true;
		if (l1.equals(l2)) return true;

		Class<?> l1Class = l1.getClass();
		if (l1Class != l2.getClass()) return false;

		if (l1 instanceof Serializable) {
			try {
				return Arrays.equals(serialize(l1), serialize(l2));
			} catch (IOException e) {
				// ignore
			}
		}

		// Check enclosed state the hard way
		List<FieldAccessor> handles = HANDLES.get(l1Class);
		if (handles == null) {
			return false;
		}
		for (FieldAccessor handle : handles) {
			if (!fieldIsEqualIn(handle, l1, l2)) {
				return false;
			}
		}
		return true;
	}

	private static <T> byte[] serialize(T l1) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
		outputStream.writeObject(l1);
		return byteArrayOutputStream.toByteArray();
	}

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	private static boolean fieldIsEqualIn(FieldAccessor field, Object left, Object right) {
		try {
			// If field is a functional type use LambdaSupport.areEqual().
			// TODO: Could there be circular references among functional types?
			MethodHandle handle = field.handle;
			if (field.isFunctionalType) {
				return areEqual(handle.invoke(left), handle.invoke(right));
			}
			return handle.invoke(left).equals(handle.invoke(right));
		} catch (Throwable e) {
			// As of Java 17, field.setAccessible(..) throws IllegalAccessException
			// if the field is private and not opened to jqwik, Predicate.not() will create lambda instances with private fields.
			return false;
		}
	}

	// TODO: This duplicates JqwikReflectionSupport.isFunctionalType() because module dependencies
	private static boolean isFunctionalType(Class<?> candidateType) {
		if (!candidateType.isInterface()) {
			return false;
		}
		return countInterfaceMethods(candidateType) == 1;
	}

	private static long countInterfaceMethods(Class<?> candidateType) {
		Method[] methods = candidateType.getMethods();
		return findInterfaceMethods(methods).size();
	}

	private static List<Method> findInterfaceMethods(Method[] methods) {
		return Arrays
				   .stream(methods)
				   .filter(m -> !m.isDefault() && !ModifierSupport.isStatic(m))
				   .collect(Collectors.toList());
	}

}
