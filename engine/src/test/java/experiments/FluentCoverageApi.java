package experiments;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Statistics;

class FluentCoverageApi {

	@Property
	void passwords(
		@ForAll @AlphaChars @NumericChars @StringLength(min = 0, max = 30) String password
	) {

		int pwdLen = password.length();

		Statistics.label("passwdLength").collect(pwdLen);
		Statistics.label("passwdLength").coverage(checker -> {
			checker.check(0).count(x -> x > 10);
			Predicate<List<Integer>> moreThan13 = p -> p.get(0) > 13;
			checker.checkQuery(moreThan13).count(x -> x > 10);
		});

	}

	@Property
	void passwordsFluent(
		@ForAll @AlphaChars @NumericChars @StringLength(min = 0, max = 30) String password
	) {

		int pwdLen = password.length();

		Statistics
			.label("passwdLength")
			.collect(pwdLen);
//			.coverage(checker -> {
//				checker.check(0).count(x -> x > 10);
//				Predicate<List<Integer>> moreThan13 = p -> p.get(0) > 13;
//				checker.checkQuery(moreThan13).count(x -> x > 10);
//			});
	}

	@Property
	void passwordsImprovedQuery(
		@ForAll @AlphaChars @NumericChars @StringLength(min = 0, max = 30) String password
	) {

		int pwdLen = password.length();

		Statistics.label("passwdLength").collect(pwdLen);
		Statistics.label("passwdLength").coverage(checker -> {
			Predicate<Integer> moreThan13 = count -> count > 13;
//			checker.checkQuery(moreThan13).count(x -> x > 10);
		});

	}
}
