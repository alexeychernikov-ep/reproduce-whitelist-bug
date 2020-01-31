package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker

data class MyClass(val value: Int)

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<MyClass>() {
    override val progressTracker = ProgressTracker()
    override fun call(): MyClass {
        return MyClass(42)
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
    }
}
