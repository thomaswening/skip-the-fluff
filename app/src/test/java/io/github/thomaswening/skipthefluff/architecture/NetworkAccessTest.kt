package io.github.thomaswening.skipthefluff.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

/**
 * Architecture test for the dependency rule "one block talks to the network"
 * (https://github.com/thomaswening/skip-the-fluff/blob/main/docs/architecture.md#dependency-rules,
 * [ADR-0006](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0006-structure-and-build.md), point 4). It is the pattern for the tests of the other rules: one class per rule, named
 * after it, reading the production code with Konsist and failing with the rule's name.
 */
class NetworkAccessTest {
    @Test
    fun `only the update package imports java_net or an HTTP library`() {
        Konsist.scopeFromProduction()
            .files
            .filterNot { file -> file.packagee?.name?.startsWith(UPDATE_PACKAGE) == true }
            .assertFalse(additionalMessage = "Only the update block may talk to the network.") { file ->
                file.imports.any { import -> NETWORK_PACKAGES.any(import.name::startsWith) }
            }
    }

    private companion object {
        const val UPDATE_PACKAGE = "io.github.thomaswening.skipthefluff.update"
        val NETWORK_PACKAGES = listOf("java.net.", "javax.net.", "okhttp3.", "io.ktor.client.")
    }
}
