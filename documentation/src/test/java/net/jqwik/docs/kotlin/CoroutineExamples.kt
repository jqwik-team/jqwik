package net.jqwik.docs.kotlin

import kotlinx.coroutines.delay
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.kotlin.api.runBlockingProperty
import org.assertj.core.api.Assertions.assertThat
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineExamples {

    suspend fun echo(string: String): String {
        delay(100)
        return string
    }

    @Property(tries = 10)
    fun useRunBlocking(@ForAll s: String) = runBlockingProperty {
        assertThat(echo(s)).isEqualTo(s)
    }

    @Property(tries = 10)
    fun useRunBlockingWithContext(@ForAll s: String) = runBlockingProperty(EmptyCoroutineContext) {
        assertThat(echo(s)).isEqualTo(s)
    }

    @Property(tries = 10)
    suspend fun useSuspend(@ForAll s: String) {
        assertThat(echo(s)).isEqualTo(s)
    }
}