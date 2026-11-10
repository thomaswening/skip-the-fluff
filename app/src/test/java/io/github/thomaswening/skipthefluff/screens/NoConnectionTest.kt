package io.github.thomaswening.skipthefluff.screens

import io.github.thomaswening.skipthefluff.navigation.START_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests of when the no-connection screen is shown and what its button reloads
 * ([REQ-044](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-044)).
 * The WebView reports a load that fails in this order: the load starts, the error, the load finishes.
 */
class NoConnectionTest {
    @Test
    fun `a failed page load shows the screen`() {
        // Act
        val screen = afterLoadFailed(NoConnection.Hidden, isForMainFrame = true, failedAddress = LESSON)

        // Assert
        assertEquals(NoConnection.Shown(reloadAddress = LESSON.address, isReloading = false), screen)
    }

    @Test
    fun `a failed image or script inside the page does not show the screen`() {
        // Act
        val screen = afterLoadFailed(NoConnection.Hidden, isForMainFrame = false, failedAddress = LESSON)

        // Assert
        assertEquals(NoConnection.Hidden, screen)
    }

    @Test
    fun `a failed address outside the website reloads the start page`() {
        // Act
        val screen = afterLoadFailed(NoConnection.Hidden, isForMainFrame = true, failedAddress = BLANK_PAGE)

        // Assert
        assertEquals(NoConnection.Shown(reloadAddress = START_URL, isReloading = false), screen)
    }

    @Test
    fun `a load that starts while the screen is shown is a reload`() {
        // Arrange
        val shown = NoConnection.Shown(reloadAddress = LESSON.address, isReloading = false)

        // Act
        val screen = afterLoadStarted(shown)

        // Assert
        assertEquals(NoConnection.Shown(reloadAddress = LESSON.address, isReloading = true), screen)
    }

    @Test
    fun `a load that starts while the website is shown keeps the screen hidden`() {
        // Act
        val screen = afterLoadStarted(NoConnection.Hidden)

        // Assert
        assertEquals(NoConnection.Hidden, screen)
    }

    @Test
    fun `a reload that fails again offers the button again`() {
        // Arrange
        val reloading = NoConnection.Shown(reloadAddress = LESSON.address, isReloading = true)

        // Act
        val screen = afterLoadFailed(reloading, isForMainFrame = true, failedAddress = LESSON)

        // Assert
        assertEquals(NoConnection.Shown(reloadAddress = LESSON.address, isReloading = false), screen)
    }

    @Test
    fun `a reload that finishes without an error hides the screen`() {
        // Arrange
        val reloading = NoConnection.Shown(reloadAddress = LESSON.address, isReloading = true)

        // Act
        val screen = afterLoadFinished(reloading)

        // Assert
        assertEquals(NoConnection.Hidden, screen)
    }

    @Test
    fun `the end of the load that failed keeps the screen shown`() {
        // Arrange
        val failed = NoConnection.Shown(reloadAddress = LESSON.address, isReloading = false)

        // Act
        val screen = afterLoadFinished(failed)

        // Assert
        assertEquals(failed, screen)
    }

    private companion object {
        val LESSON = FailedAddress(scheme = "https", host = "www.duolingo.com", address = "https://www.duolingo.com/lesson")
        val BLANK_PAGE = FailedAddress(scheme = "about", host = null, address = "about:blank")
    }
}
