package examples.bugs;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

// See https://github.com/jlink/jqwik/issues/446
class ListOfConsumersBug {
	@Property
	void listOfConsumers(@ForAll @Size(min = 1) List<Consumer<Integer>> integerConsumers){

	}

	@Example
	void listOfConsumersExample(){
		Arbitrary<Consumer<Integer>> returning = Functions.function(Consumer.class).returning(Arbitraries.nothing());
		ListArbitrary<Consumer<Integer>> consumers = returning.list().ofMinSize(1);

		consumers.sample();
	}

	@Property
	void listOfSuppliers(@ForAll @Size(min = 1) List<Supplier<Integer>> integerConsumers){

	}
}
