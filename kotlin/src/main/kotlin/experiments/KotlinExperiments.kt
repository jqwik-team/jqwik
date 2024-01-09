package experiments

import net.jqwik.api.*
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.combine

class KotlinExperiments {

    @Property(tries = 1000, generation = GenerationMode.RANDOMIZED)
    fun `filter arbitraries`(@ForAll("combinesOneToThree") tuple: Tuple.Tuple2<Int, Int>) {
        println(tuple)
    }

    @Provide
    fun combinesOneToThree() = combine {
        val v1 by Int.any()
        val v2 by Int.any()

        filter { v1 == v2 }

        combineAs {
            Tuple.of(v1, v2)
        }
    }

}