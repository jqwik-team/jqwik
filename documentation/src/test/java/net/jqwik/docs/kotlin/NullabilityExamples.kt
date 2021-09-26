package net.jqwik.docs.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.WithNull

class NullabilityExamples {

    @Property
    fun alsoGenerateNulls(@ForAll nullOrString: String?) {
        println(nullOrString)
    }

    @Property(tries = 100)
    fun generateNullsInList(@ForAll list: List<@WithNull String>) {
        println(list)
    }
}