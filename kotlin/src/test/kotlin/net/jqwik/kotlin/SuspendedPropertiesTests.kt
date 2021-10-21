package net.jqwik.kotlin

import kotlinx.coroutines.delay
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Group
import net.jqwik.api.Property
import net.jqwik.kotlin.api.runBlockingAssertion
import net.jqwik.kotlin.api.runBlockingPredicate
import net.jqwik.testing.ExpectFailure
import org.assertj.core.api.Assertions.assertThat

@Group
class SuspendedPropertiesTests {

    @Group
    inner class RunBlockingWrappers {
        @Example
        @ExpectFailure
        fun useSuspendMethod() = runBlockingAssertion {
            assertThat(echo("sausage")).isEqualTo("soy")
        }

        @Example
        @ExpectFailure
        fun usePredicateSuspendMethod() = runBlockingPredicate {
            echo("soy") == "sausage"
        }

        @Property(tries = 10)
        fun assertionProperty(@ForAll string: String) = runBlockingAssertion {
            assertThat(echo(string)).isEqualTo(string)
        }

    }

    suspend fun echo(string: String): String {
        delay(100)
        return string
    }
}