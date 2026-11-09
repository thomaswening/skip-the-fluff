package io.github.thomaswening.skipthefluff.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.Test

/**
 * Architecture test for the dependency rule "only the fix engine acts on the page"
 * (https://github.com/thomaswening/skip-the-fluff/blob/main/docs/architecture.md#dependency-rules).
 * Native code never runs JavaScript of its own in the page and never gives the page a native object
 * to call. It searches the text of each production file, comments included, so a comment naming
 * one of these calls fails it too.
 *
 * Phase 2 registers the fix engine and the message bridge; that pull request names the one package
 * allowed to do so here.
 */
class PageAccessTest {
    @Test
    fun `no native code runs JavaScript in the page or exposes an object to it`() {
        Konsist.scopeFromProduction()
            .files
            .assertFalse(additionalMessage = "Only the fix engine acts on the page.") { file ->
                PAGE_ACCESS.any(file.text::contains)
            }
    }

    private companion object {
        val PAGE_ACCESS = listOf(
            "evaluateJavascript", // runs a script in the page and returns its result
            "addJavascriptInterface", // lets the page's scripts call a native object
            "\"javascript:", // loadUrl("javascript:…") runs a script in the page
        )
    }
}
