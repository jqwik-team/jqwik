package net;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.core.importer.*;
import com.tngtech.archunit.library.dependencies.*;

import net.jqwik.api.*;

public class ArchitectureTests {

	private static JavaClasses importedClasses = new ClassFileImporter().importPackages("net.jqwik");

	//@Example
	// TODO: Rule is heavily violated. Fixing requires changes in api packages :-(
	void noCyclicDependencies() {

		SliceRule noCyclicDependencies = SlicesRuleDefinition
			.slices()
			.matching("net.jqwik.(**)..")
			.should().beFreeOfCycles();

		noCyclicDependencies.check(importedClasses);
	}

	// TODO: Use @ArchTest instead
	// see https://www.archunit.org/userguide/html/000_Index.html#_junit_4_5_support
	@Example
	void noCyclicDependenciesInApiPackages() {

		SliceRule noCyclicDependencies = SlicesRuleDefinition
											 .slices()
											 .matching("net.jqwik.api.(**)..")
											 .should().beFreeOfCycles();

		noCyclicDependencies.check(importedClasses);
	}

}
