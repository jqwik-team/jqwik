package net.jqwik.api.constraints;

import org.apiguardian.api.*;

import net.jqwik.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.1")
public enum UseTypeMode {
	CONSTRUCTORS {
		@Override
		public <T> TypeArbitrary<T> modify(TypeArbitrary<T> arbitrary) {
			return arbitrary.useAllConstructors();
		}
	}, FACTORIES {
		@Override
		public <T> TypeArbitrary<T> modify(TypeArbitrary<T> arbitrary) {
			return arbitrary.useAllFactoryMethods();
		}
	}, PUBLIC_CONSTRUCTORS {
		@Override
		public <T> TypeArbitrary<T> modify(TypeArbitrary<T> arbitrary) {
			return arbitrary.usePublicConstructors();
		}
	}, PUBLIC_FACTORIES {
		@Override
		public <T> TypeArbitrary<T> modify(TypeArbitrary<T> arbitrary) {
			return arbitrary.usePublicFactoryMethods();
		}
	};

	@API(status = INTERNAL, since = "1.1")
	public abstract <T> TypeArbitrary<T> modify(TypeArbitrary<T> arbitrary);
}
