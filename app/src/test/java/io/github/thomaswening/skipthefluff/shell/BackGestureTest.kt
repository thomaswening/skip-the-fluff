package io.github.thomaswening.skipthefluff.shell

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests of where the back gesture leads, outside lessons
 * ([REQ-037](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-037)).
 */
class BackGestureTest {
    @Test
    fun `back goes back in the website on a page with history`() {
        // Act
        val goesBack = goesBackInWebsite(path = "/profile", canGoBack = true)

        // Assert
        assertTrue(goesBack)
    }

    @Test
    fun `back closes the app on the learning path although it has history`() {
        // Act
        val goesBack = goesBackInWebsite(path = "/learn", canGoBack = true)

        // Assert
        assertFalse(goesBack)
    }

    @Test
    fun `back closes the app on the learning path written with a trailing slash`() {
        // Act
        val goesBack = goesBackInWebsite(path = "/learn/", canGoBack = true)

        // Assert
        assertFalse(goesBack)
    }

    @Test
    fun `back goes back in the website on a page below the learning path`() {
        // Act
        val goesBack = goesBackInWebsite(path = "/learn/units", canGoBack = true)

        // Assert
        assertTrue(goesBack)
    }

    @Test
    fun `back closes the app on the first page`() {
        // Act
        val goesBack = goesBackInWebsite(path = "/", canGoBack = false)

        // Assert
        assertFalse(goesBack)
    }
}
