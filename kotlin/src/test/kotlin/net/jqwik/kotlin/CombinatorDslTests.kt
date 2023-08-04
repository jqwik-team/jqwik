package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.api.Arbitraries.just
import net.jqwik.kotlin.api.*
import net.jqwik.testing.ShrinkingSupport
import net.jqwik.testing.TestingSupport
import net.jqwik.testing.TestingSupport.assertAtLeastOneGeneratedOf
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.util.*

@PropertyDefaults(tries = 100, shrinking = ShrinkingMode.FULL)
class CombinatorDslTests {

    private val oneToThree: Arbitrary<Int> = Int.any(1..3)

    @Example
    fun `combine arbitraries`() {
        val combined = combine {
            val v1 by just(1)
            val v2 by just(2)

            combineAs {
                v1!! + v2!!
            }
        }

        assertThat(combined.sample()).isEqualTo(3)
    }

    @Example
    fun `combine nullable arbitraries`(@ForAll random: Random) {
        val combined = combine {
            val v1 by just(1).orNull(0.5)
            val v2 by just(2)

            combineAs {
                (v1 ?: 0) + v2!!
            }
        }

        val generator = combined.generator(1000)
        checkAllGenerated(
            generator,
            random
        ) { value -> value == 2 || value == 3 }

        assertAtLeastOneGeneratedOf(generator, random, 2)
        assertAtLeastOneGeneratedOf(generator, random, 3)
    }

    @Example
    fun `filter arbitraries`(@ForAll random: Random) {
        val combined = combine {
            val v1 by oneToThree
            val v2 by oneToThree

            filter { v1 != v2 }

            combineAs {
                Tuple.of(v1, v2)
            }
        }

        TestingSupport.assertAllGenerated(combined.generator(1000), random) { (first, second) ->
            assertThat(first).isNotEqualTo(second)
        }
    }

    @Example
    fun `throw when calling combineAs twice`() {
        assertThatThrownBy {
            combine {
                val value by oneToThree

                combineAs { value }
                combineAs { value }
            }
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Example
    fun `throw when using value outside combineAs`() {
        assertThatThrownBy {
            combine {
                val value by oneToThree

                val usage = value

                combineAs { usage }
            }
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Example
    fun `flatCombine arbitraries`() {
        val combined = combine {
            val v1 by just(1)
            val v2 by just(2)

            flatCombineAs {
                just(v1!! + v2!!)
            }
        }

        assertThat(combined.sample()).isEqualTo(3)
    }

    @Property
    fun `shrink combined arbitrary`(@ForAll random: Random) {
        val combined = combine {
            val i by Arbitraries.integers()
            val s by Arbitraries.strings().alpha().ofMinLength(1)

            combineAs { i.toString() + s }
        }

        val shrunkValue = ShrinkingSupport.falsifyThenShrink(combined, random)
        assertThat(shrunkValue).isIn("0A", "0a")
    }
}
