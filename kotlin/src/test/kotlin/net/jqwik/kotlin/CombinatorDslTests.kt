package net.jqwik.kotlin

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Group
import net.jqwik.kotlin.api.combine
import net.jqwik.kotlin.api.orNull
import net.jqwik.testing.TestingSupport.assertAtLeastOneGeneratedOf
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions.assertThat
import java.util.*

@Group
class CombinatorDslTests {
    @Example
    fun `combine arbitraries`() {
        val combined = combine {
            val v1 by just(1)
            val v2 by just(2)

            createAs {
                v1 + v2
            }
        }

        assertThat(combined.sample()).isEqualTo(3)
    }

    @Example
    fun `combine nullable arbitraries`(@ForAll random: Random) {
        val combined = combine {
            val v1 by just(1).orNull(0.5)
            val v2 by just(2)

            createAs {
                (v1 ?: 0) + v2
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
}
