package net.jqwik.kotlin.api

import net.jqwik.api.Tuple

operator fun <A> Tuple.Tuple1<A>.component1(): A = this.get1()

operator fun <A, B> Tuple.Tuple2<A, B>.component2(): B = this.get2()

operator fun <A, B, C> Tuple.Tuple3<A, B, C>.component3(): C = this.get3()

operator fun <A, B, C, D> Tuple.Tuple4<A, B, C, D>.component4(): D = this.get4()

operator fun <A, B, C, D, E> Tuple.Tuple5<A, B, C, D, E>.component5(): E = this.get5()

operator fun <A, B, C, D, E, F> Tuple.Tuple6<A, B, C, D, E, F>.component6(): F = this.get6()

operator fun <A, B, C, D, E, F, G> Tuple.Tuple7<A, B, C, D, E, F, G>.component7(): G = this.get7()

operator fun <A, B, C, D, E, F, G, H> Tuple.Tuple8<A, B, C, D, E, F, G, H>.component8(): H = this.get8()
