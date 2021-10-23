package net.jqwik.kotlin

import kotlinx.coroutines.delay
import net.jqwik.api.*
import net.jqwik.api.constraints.AlphaChars
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
        fun useSuspendAssertion() = runBlockingAssertion {
            assertThat(echo("sausage")).isEqualTo("soy")
        }

        @Example
        @ExpectFailure
        fun useSuspendPredicate() = runBlockingPredicate {
            echo("soy") == "sausage"
        }

        @Property(tries = 10)
        fun `property with suspend assertion`(@ForAll string: String) = runBlockingAssertion {
            assertThat(echo(string)).isEqualTo(string)
        }

        @Property(tries = 10)
        fun `property with suspend predicate`(@ForAll string: String) = runBlockingPredicate {
            echo(string) == string
        }

    }

    @Group
    inner class PropertyFunctionsWithSuspendModifier {
        @Example
        suspend fun succeedingAssertion() {
            assertThat(echo("sausage")).isEqualTo("sausage")
        }

        @Example
        suspend fun succeedingPredicate() : Boolean {
            return echo("sausage") == "sausage"
        }

        @Example
        @ExpectFailure
        suspend fun failingAssertion() {
            assertThat(echo("sausage")).isEqualTo("soy")
        }

        @Example
        @ExpectFailure
        suspend fun failingPredicate() : Boolean {
            return echo("sausage") == "soy"
        }

        @Property(tries = 10)
        suspend fun succeedingAssertionWithParams(@ForAll @AlphaChars string: String) {
            assertThat(echo(string)).isEqualTo(string)
        }

        @Property(tries = 10)
        @ExpectFailure
        suspend fun failingAssertionWithParams(@ForAll @AlphaChars string: String) {
            assertThat(echo(string)).isEmpty()
        }

    }

    suspend fun echo(string: String): String {
        delay(100)
        return string
    }
}