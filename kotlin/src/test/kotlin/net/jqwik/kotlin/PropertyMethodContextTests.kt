package net.jqwik.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Reporter
import net.jqwik.api.footnotes.EnableFootnotes
import net.jqwik.api.footnotes.Footnotes
import net.jqwik.testing.CheckReporting
import org.mockito.Mockito

class PropertyMethodContextTests {

    //TODO: Enable after plugin update. Does not compile due to bug in Intellij's Kotlin plugin
    //@AddLifecycleHook(CheckPublishing.class)
    @Property(tries = 5)
    fun Reporter.testReporter(@ForAll aNumber: Int) {
        publishValue("aNumber", aNumber.toString())
    }

    @EnableFootnotes
    @Property(tries = 5)
    fun Footnotes.testFootnotes(@ForAll aNumber: Int) {
        addFootnote(aNumber.toString())
    }
}

class CheckPublishing : CheckReporting() {
    override fun check(mockReporter: Reporter) {
        Mockito.verify(mockReporter).publishValue(Mockito.eq("aNumber"), Mockito.any())
    }
}