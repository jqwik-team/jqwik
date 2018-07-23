package net;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.core.importer.*;
import com.tngtech.archunit.library.dependencies.*;
import net.jqwik.api.*;

public class ArchitectureTests {

	static JavaClasses importedClasses = new ClassFileImporter().importPackages("net.jqwik");

	//@Example
	// TODO: Rule is heavily violated. Fixing requires changes in api packages :-(
	void noCyclicDependencies() {

		SliceRule noCyclicDependencies = SlicesRuleDefinition
			.slices()
			.matching("net.jqwik.(**)..")
			.should().beFreeOfCycles();

		noCyclicDependencies.check(importedClasses);
	}

	@Example
	void noCyclicDependenciesInApiPackages() {

		SliceRule noCyclicDependencies = SlicesRuleDefinition
			.slices()
			.matching("net.jqwik.api.(**)..")
			.should().beFreeOfCycles();

		noCyclicDependencies.check(importedClasses);
	}

}
