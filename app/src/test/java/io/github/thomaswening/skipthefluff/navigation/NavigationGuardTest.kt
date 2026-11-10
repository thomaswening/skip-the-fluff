package io.github.thomaswening.skipthefluff.navigation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests of which navigations open in the default browser
 * ([REQ-015](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-015)).
 * The sign-in hosts are those the website's buttons opened on 10 November 2026, e.g.
 * https://accounts.google.com/o/oauth2/v2/auth?client_id=…, https://www.facebook.com/v7.0/dialog/oauth?app_id=…
 * and https://appleid.apple.com/auth/authorize?client_id=….
 */
class NavigationGuardTest {
    @Test
    fun `the website's own domain stays in the app`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "duolingo.com", isMainFrame = true)

        // Assert
        assertFalse(opensInBrowser)
    }

    @Test
    fun `the website's www subdomain stays in the app`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "www.duolingo.com", isMainFrame = true)

        // Assert
        assertFalse(opensInBrowser)
    }

    @Test
    fun `another subdomain of the website stays in the app`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "support.duolingo.com", isMainFrame = true)

        // Assert
        assertFalse(opensInBrowser)
    }

    @Test
    fun `the website's host in capitals stays in the app`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "WWW.Duolingo.com", isMainFrame = true)

        // Assert
        assertFalse(opensInBrowser)
    }

    @Test
    fun `a host that ends in the website's name without a dot opens in the browser`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "evilduolingo.com", isMainFrame = true)

        // Assert
        assertTrue(opensInBrowser)
    }

    @Test
    fun `a host that starts with the website's name opens in the browser`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "duolingo.com.evil.example", isMainFrame = true)

        // Assert
        assertTrue(opensInBrowser)
    }

    @Test
    fun `another host opens in the browser`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "en.wikipedia.org", isMainFrame = true)

        // Assert
        assertTrue(opensInBrowser)
    }

    @Test
    fun `the sign-in with Google opens in the browser`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "accounts.google.com", isMainFrame = true)

        // Assert
        assertTrue(opensInBrowser)
    }

    @Test
    fun `the sign-in with Facebook opens in the browser`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "www.facebook.com", isMainFrame = true)

        // Assert
        assertTrue(opensInBrowser)
    }

    @Test
    fun `the sign-in with Apple opens in the browser`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "appleid.apple.com", isMainFrame = true)

        // Assert
        assertTrue(opensInBrowser)
    }

    @Test
    fun `a frame from another host inside the website stays in the app`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = "accounts.google.com", isMainFrame = false)

        // Assert
        assertFalse(opensInBrowser)
    }

    @Test
    fun `a page without a host stays in the app`() {
        // Act
        val opensInBrowser = opensInDefaultBrowser(host = null, isMainFrame = true)

        // Assert
        assertFalse(opensInBrowser)
    }
}
