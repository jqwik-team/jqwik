package net.jqwik.engine.support.types;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.providers.*;

class TypeUsageToString {

	public static String toString(TypeUsage self) {
		return toString(self, new LinkedHashSet<>());
	}

	private static String toString(TypeUsage self, Set<TypeUsage> touchedTypes) {
		String representation = self.getRawType().getSimpleName();

		if (touchedTypes.contains(self)) {
			if (self.isTypeVariableOrWildcard()) {
				return self.getTypeVariable();
			}
			return representation;
		}
		touchedTypes.add(self);

		if (self.isGeneric()) {
			representation = String.format("%s<%s>", representation, toStringTypeArguments(self, touchedTypes));
		}
		if (self.isArray()) {
			//noinspection OptionalGetWithoutIsPresent
			representation = String.format("%s[]", toString(self.getComponentType().get(), touchedTypes));
		}
		if (self.isTypeVariableOrWildcard()) {
			representation = toStringTypeVariable(self, touchedTypes);
		} else if (!self.getAnnotations().isEmpty()) {
			representation = String.format("%s %s", toStringAnnotations(self), representation);
		}
		if (self.isNullable()) {
			representation = representation + "?";
		}
		return representation;
	}

	private static String toStringTypeArguments(TypeUsage self, Set<TypeUsage> touchedTypes) {
		return self.getTypeArguments().stream()
							.map(typeUsage -> toString(typeUsage, touchedTypes))
							.collect(Collectors.joining(", "));
	}

	private static String toStringAnnotations(TypeUsage self) {
		return self.getAnnotations().stream()
						  .map(Annotation::toString)
						  .collect(Collectors.joining(" "));
	}

	private static String toStringTypeVariable(TypeUsage self, Set<TypeUsage> touchedTypes) {
		String representation = self.getTypeVariable();
		if (hasUpperBoundBeyondObject(self)) {
			representation += String.format(" extends %s", toStringUpperBound(self, touchedTypes));
		}
		if (hasLowerBounds(self)) {
			representation += String.format(" super %s", toStringLowerBounds(self, touchedTypes));
		}
		return representation;
	}

	private static boolean hasUpperBoundBeyondObject(TypeUsage self) {
		if (self.getUpperBounds().size() > 1)
			return true;
		return self.getUpperBounds().size() == 1 && !self.getUpperBounds().get(0).isOfType(Object.class);
	}

	private static boolean hasLowerBounds(TypeUsage self) {
		return self.getLowerBounds().size() > 0;
	}

	private static String toStringLowerBounds(TypeUsage self, Set<TypeUsage> touchedTypes) {
		return self.getLowerBounds().stream()
						  .map(typeUsage -> toString(typeUsage, touchedTypes))
						  .collect(Collectors.joining(" & "));
	}

	private static String toStringUpperBound(TypeUsage self, Set<TypeUsage> touchedTypes) {
		return self.getUpperBounds().stream()
						  .map(typeUsage -> toString(typeUsage, touchedTypes))
						  .collect(Collectors.joining(" & "));
	}

}
