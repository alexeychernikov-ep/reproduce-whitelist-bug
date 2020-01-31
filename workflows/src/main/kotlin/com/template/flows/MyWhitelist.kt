package com.template.flows

import net.corda.core.internal.location
import net.corda.core.serialization.SerializationWhitelist
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(MyWhitelist::class.java)

@Suppress("unused")
class MyWhitelist : SerializationWhitelist {
    init {
        val cp = MyWhitelist::class.java.location
        log.info("Whitelist instantiated.\n\tlocation=$cp\n\tstacktrace:\n\t\t${Thread.currentThread().stackTrace.joinToString("\n\t\t")}")
    }
    override val whitelist: List<Class<*>>
        get() = listOf(MyClass::class.java)
}
