package net.jqwik.docs.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Group
import net.jqwik.api.Property

class GroupingExamples {

    @Property
    fun plainProperty(@ForAll anInt: Int) {
    }

    @Group
    inner class OuterGroup {

        @Property
        fun propertyInOuterGroup(@ForAll anInt: Int) {
        }

        @Group
        inner class InnerGroup {
            @Property
            fun propertyInInnerGroup(@ForAll anInt: Int) {
            }
        }

    }

    @Group
    class WithoutInnerItWillNotBePickedUp {
        @Property
        fun propertyWillNotBeRun(@ForAll anInt: Int) {
        }
    }

}