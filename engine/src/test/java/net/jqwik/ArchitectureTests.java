package net.jqwik;

import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.library.dependencies.*;

@AnalyzeClasses(packages = "net.jqwik")
public class ArchitectureTests {

	//@ArchTest
	//TODO: Does not work yet
	public static final ArchRule noCyclicDependenciesInEnginePackages = SlicesRuleDefinition
																			.slices()
																			.matching("net.jqwik.engine.(**)..")
																			.should().beFreeOfCycles();
	@ArchTest
	public static final ArchRule noCyclicDependenciesInApiPackages = SlicesRuleDefinition
																		 .slices()
																		 .matching("net.jqwik.api.(**)..")
																		 .should().beFreeOfCycles();

}
