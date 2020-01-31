package com.template

import com.template.flows.Initiator
import com.template.flows.MyClass
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startFlow
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.TestIdentity
import net.corda.testing.driver.DriverDSL
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.driver
import net.corda.testing.node.internal.findCordapp
import org.junit.Test
import java.util.concurrent.Future

class DriverBasedTest {
    private val bankA = TestIdentity(CordaX500Name("BankA", "", "GB"))

    /** Force [MyClass] to be serialized and sent over the wire. */
    @Test
    fun `whitelist discovery test`() = withDriver {
        val (partyAHandle) = startNodes(bankA)
        val flowHandle = partyAHandle.rpc.startFlow(::Initiator)
        flowHandle.returnValue.get()
    }

    // Runs a test inside the Driver DSL, which provides useful functions for starting nodes, etc.
    private fun withDriver(test: DriverDSL.() -> Unit) = driver(
        DriverParameters(isDebug = true, startNodesInProcess = false)
                // It doesn't matter whether I specify the CorDapps explicitly or not, the nodes are still
                // started with a classpath that includes the build directories of the flows and contracts projects.
                // This means that these versions of the [MyWhitelist] will shadow the "real" versions in the jar files
                // copied to the CorDapps folders of the nodes, and these are the ones that will get instantiated by
                // the ServiceLoader.
                .withCordappsForAllNodes(listOf(findCordapp("com.template.flows"), findCordapp("com.template.contracts")))
    ) { test() }

    // Resolves a list of futures to a list of the promised values.
    private fun <T> List<Future<T>>.waitForAll(): List<T> = map { it.getOrThrow() }

    // Starts multiple nodes simultaneously, then waits for them all to be ready.
    private fun DriverDSL.startNodes(vararg identities: TestIdentity) = identities
        .map { startNode(providedName = it.name) }
        .waitForAll()
}
