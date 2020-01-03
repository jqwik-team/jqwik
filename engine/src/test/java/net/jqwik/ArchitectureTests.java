package net.jqwik;

import com.tngtech.archunit.core.importer.ImportOption.*;
import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.library.dependencies.*;

@AnalyzeClasses(packages = "net.jqwik", importOptions = {DoNotIncludeTests.class, DoNotIncludeJars.class})
public class ArchitectureTests {

	//@ArchTest
	//TODO: Does not work yet
	public static final ArchRule noCyclicDependenciesInEnginePackages = SlicesRuleDefinition
																			.slices()
																			.matching("net.jqwik.engine.(**)")
																			.should().notDependOnEachOther();

	//@ArchTest
	//TODO: Does not work yet
	public static final ArchRule noCyclicDependenciesInEngineClasses = SlicesRuleDefinition
																		   .slices()
																		   .matching("net.jqwik.engine.(**)..")
																		   .should().beFreeOfCycles();

	//@ArchTest
	//TODO: Does not work yet
	public static final ArchRule noCyclicDependenciesInApiPackages = SlicesRuleDefinition
																		 .slices()
																		 .matching("net.jqwik.api.(**)")
																		 .should().notDependOnEachOther();
	@ArchTest
	public static final ArchRule noCyclicDependenciesInApiClasses = SlicesRuleDefinition
																		.slices()
																		.matching("net.jqwik.api.(**)..")
																		.should().beFreeOfCycles();

}
