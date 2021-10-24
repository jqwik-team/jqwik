package net.jqwik.kotlin

import kotlinx.coroutines.delay
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Group
import net.jqwik.api.Property
import net.jqwik.api.constraints.AlphaChars
import net.jqwik.kotlin.api.runBlockingProperty
import net.jqwik.testing.ExpectFailure
import org.assertj.core.api.Assertions.assertThat

@Group
class SuspendedPropertiesTests {

    @Group
    inner class UseRunBlockingProperty {
        @Example
        @ExpectFailure
        fun `fail with suspend assertion`() = runBlockingProperty {
            assertThat(echo("sausage")).isEqualTo("soy")
        }

        @Example
        @ExpectFailure
        fun `fail with suspend predicate`() = runBlockingProperty {
            echo("soy") == "sausage"
        }

        @Property(tries = 10)
        fun `succeed with suspend assertion`(@ForAll string: String) = runBlockingProperty {
            assertThat(echo(string)).isEqualTo(string)
        }

        @Property(tries = 10)
        fun `succeed with suspend predicate`(@ForAll string: String) = runBlockingProperty {
            echo(string) == string
        }

        @Property(tries = 10)
        fun `succeed with suspend null return`(@ForAll string: String) = runBlockingProperty {
            return@runBlockingProperty echo(null)
        }

    }

    @Group
    inner class PropertyWithSuspendModifier {

        @Example
        suspend fun succeedingAssertion() {
            assertThat(echo("sausage")).isEqualTo("sausage")
        }

        @Example
        suspend fun succeedingPredicate(): Boolean {
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
        suspend fun failingAssertionWithParams(
            @ForAll @AlphaChars string1: String,
            @ForAll @AlphaChars string2: String
        ) {
            assertThat(echo(string1)).isEqualTo(echo(string2))
        }
    }

    suspend fun echo(string: String?): String? {
        delay(10)
        return string
    }
}