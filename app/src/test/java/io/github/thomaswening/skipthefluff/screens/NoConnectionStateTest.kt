package io.github.thomaswening.skipthefluff.screens

import io.github.thomaswening.skipthefluff.navigation.START_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests of when the no-connection screen is shown, what its button reloads and how long its spinner shows
 * ([REQ-044](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-044)).
 * The WebView reports a load that fails in this order: the load starts, the error, the load finishes.
 */
class NoConnectionStateTest {
    @Test
    fun `a failed page load shows the screen`() {
        // Act
        val state = NoConnectionState.Hidden.afterLoadFailed(LESSON, isMainFrame = true)

        // Assert
        assertEquals(NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = false), state)
    }

    @Test
    fun `a failed image or script inside the page does not show the screen`() {
        // Act
        val state = NoConnectionState.Hidden.afterLoadFailed(LESSON, isMainFrame = false)

        // Assert
        assertEquals(NoConnectionState.Hidden, state)
    }

    @Test
    fun `a failed address outside the website reloads the start page`() {
        // Act
        val state = NoConnectionState.Hidden.afterLoadFailed(BLANK_PAGE, isMainFrame = true)

        // Assert
        assertEquals(NoConnectionState.Shown(reloadUrl = START_URL, isReloading = false), state)
    }

    @Test
    fun `a load that starts while the screen is shown is a reload`() {
        // Arrange
        val shown = NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = false)

        // Act
        val state = shown.afterLoadStarted()

        // Assert
        assertEquals(NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = true), state)
    }

    @Test
    fun `a load that starts while the website is shown keeps the screen hidden`() {
        // Act
        val state = NoConnectionState.Hidden.afterLoadStarted()

        // Assert
        assertEquals(NoConnectionState.Hidden, state)
    }

    @Test
    fun `a reload that fails again offers the button again`() {
        // Arrange
        val reloading = NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = true)

        // Act
        val state = reloading.afterLoadFailed(LESSON, isMainFrame = true)

        // Assert
        assertEquals(NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = false), state)
    }

    @Test
    fun `a reload that finishes without an error hides the screen`() {
        // Arrange
        val reloading = NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = true)

        // Act
        val state = reloading.afterLoadFinished()

        // Assert
        assertEquals(NoConnectionState.Hidden, state)
    }

    @Test
    fun `the end of the load that failed keeps the screen shown`() {
        // Arrange
        val failed = NoConnectionState.Shown(reloadUrl = LESSON.url, isReloading = false)

        // Act
        val state = failed.afterLoadFinished()

        // Assert
        assertEquals(failed, state)
    }

    @Test
    fun `a reload that failed at once keeps the spinner for the rest of the minimum`() {
        // Act
        val remaining = remainingSpinnerMillis(spinnerMillis = 200L)

        // Assert
        assertEquals(MIN_SPINNER_MILLIS - 200L, remaining)
    }

    @Test
    fun `a reload that took the minimum hides the spinner at once`() {
        // Act
        val remaining = remainingSpinnerMillis(spinnerMillis = MIN_SPINNER_MILLIS)

        // Assert
        assertEquals(0L, remaining)
    }

    @Test
    fun `a reload that took longer than the minimum hides the spinner at once`() {
        // Act
        val remaining = remainingSpinnerMillis(spinnerMillis = MIN_SPINNER_MILLIS + 5_000L)

        // Assert
        assertEquals(0L, remaining)
    }

    private companion object {
        val LESSON = PageAddress(scheme = "https", host = "www.duolingo.com", url = "https://www.duolingo.com/lesson")
        val BLANK_PAGE = PageAddress(scheme = "about", host = null, url = "about:blank")
    }
}
