package experiments

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.domains.Domain
import net.jqwik.api.domains.DomainContextBase
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.anyForSubtypeOf
import net.jqwik.kotlin.api.anyForType
import net.jqwik.kotlin.api.combine

@Domain(MyDomain::class)
@Domain(AnotherDomain::class)
class Test {

    @Property(tries = 200)
    fun test(@ForAll model: Model) {
        //println(model)
        //print('.')
    }

    @Property(tries = 100000)
    fun testPerformance(@ForAll model: Model) {
    }
}

class MyDomain : DomainContextBase() {
    @Provide
    fun model(): Arbitrary<Model> {
        return anyForSubtypeOf<Model>()
    }

    @Provide
    fun attribute() = combine {
        //println("## MyDomain.attribute()")
        val value by Double.any()
        combineAs {
            Attr(value)
        }
    }
}

class AnotherDomain : DomainContextBase() {
    @Provide
    fun attribute() = combine {
        //println("## AnotherDomain.attribute()")
        val value by Double.any()
        combineAs {
            Attr(value)
        }
    }
}

sealed interface Model

data class Model1(val attr1: Attr) : Model
data class Model2(val attr1: Attr) : Model

data class Attr(val value: Double)