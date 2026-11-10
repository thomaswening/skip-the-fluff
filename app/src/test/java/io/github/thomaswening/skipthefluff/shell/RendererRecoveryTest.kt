package io.github.thomaswening.skipthefluff.shell

import io.github.thomaswening.skipthefluff.navigation.START_URL
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests of how the app recovers once Android has ended the WebView's renderer process
 * ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md),
 * point 4).
 */
class RendererRecoveryTest {
    @Test
    fun `the first loss counts as one`() {
        // Act
        val count = lossesInARow(earlierLossesInARow = 0, millisSincePageStarted = 1_000L)

        // Assert
        assertEquals(1, count)
    }

    @Test
    fun `a loss soon after the page started counts as one more in a row`() {
        // Act
        val count = lossesInARow(earlierLossesInARow = 1, millisSincePageStarted = QUICK_LOSS_MILLIS - 1)

        // Assert
        assertEquals(2, count)
    }

    @Test
    fun `a loss a minute after the page started starts the count again`() {
        // Act
        val count = lossesInARow(earlierLossesInARow = 2, millisSincePageStarted = QUICK_LOSS_MILLIS)

        // Assert
        assertEquals(1, count)
    }

    @Test
    fun `the first loss reloads the page the learner was on`() {
        // Act
        val recovery = recoveryAfterLoss(lossesInARow = 1, lastWebsiteAddress = "https://www.duolingo.com/lesson")

        // Assert
        assertEquals(RendererRecovery.Reload("https://www.duolingo.com/lesson"), recovery)
    }

    @Test
    fun `the second loss in a row loads the start page`() {
        // Act
        val recovery = recoveryAfterLoss(lossesInARow = 2, lastWebsiteAddress = "https://www.duolingo.com/lesson")

        // Assert
        assertEquals(RendererRecovery.Reload(START_URL), recovery)
    }

    @Test
    fun `the third loss in a row gives up`() {
        // Act
        val recovery = recoveryAfterLoss(lossesInARow = 3, lastWebsiteAddress = "https://www.duolingo.com/lesson")

        // Assert
        assertEquals(RendererRecovery.GiveUp, recovery)
    }

    @Test
    fun `a loss before any page of the website was shown loads the start page`() {
        // Act
        val recovery = recoveryAfterLoss(lossesInARow = 1, lastWebsiteAddress = null)

        // Assert
        assertEquals(RendererRecovery.Reload(START_URL), recovery)
    }
}
