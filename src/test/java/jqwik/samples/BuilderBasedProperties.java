
package jqwik.samples;

import static net.jqwik.JqwikPropertyBuilder.property;

import net.jqwik.IntegerGenerator;
import net.jqwik.Property;

class BuilderBasedProperties {

	Property succeedingPropertyWithoutParameter() {
		return property("property without params").state(params -> true);
	}

	Property succeedingPropertyWithOneParameter() {
		return property("property with 1 param") //
		.forAll("number", int.class)//
		.state(param -> param.named("number") != null); //
	}

	Property succeedingPropertyWithTwoParameters() {
		return property("property with 2 params") //
		.forAll("number", int.class)//
		.forAll("another", int.class)//
		.state(param -> {
			int number = ((int) param.named("number"));
			int another = ((int) param.named("another"));
			return number == another || number != another;
		}); //
	}

    Property propertyWithAGeneratorParameter() {
        return property("property with a generator") //
                .forAll("number", IntegerGenerator.any())//
                .state(param -> param.named("number") != null); //
    }


}
