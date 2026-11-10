package io.github.thomaswening.skipthefluff.permissions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests of how the app answers the website's requests for a device
 * ([REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038)) and
 * when it tells the learner where to switch the microphone on
 * ([REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)).
 */
class PermissionBridgeTest {
    @Test
    fun `the microphone is passed on when the learner allowed it in an earlier dialog`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = true, appHoldsMicrophonePermission = true)

        // Assert
        assertEquals(DeviceAnswer.GRANT_MICROPHONE, answer)
    }

    @Test
    fun `the learner is asked when the website wants the microphone for the first time`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = true, appHoldsMicrophonePermission = false)

        // Assert
        assertEquals(DeviceAnswer.ASK_LEARNER, answer)
    }

    @Test
    fun `a request without the microphone is refused although the microphone is allowed`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = false, appHoldsMicrophonePermission = true)

        // Assert
        assertEquals(DeviceAnswer.DENY, answer)
    }

    @Test
    fun `a request without the microphone is refused without asking the learner`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = false, appHoldsMicrophonePermission = false)

        // Assert
        assertEquals(DeviceAnswer.DENY, answer)
    }

    @Test
    fun `the microphone is passed on when the learner allows it in Android's dialog`() {
        // Act
        val answer = answerAfterDialog(learnerAllowedInDialog = true)

        // Assert
        assertEquals(DeviceAnswer.GRANT_MICROPHONE, answer)
    }

    @Test
    fun `the website is refused when the learner refuses the microphone`() {
        // Act
        val answer = answerAfterDialog(learnerAllowedInDialog = false)

        // Assert
        assertEquals(DeviceAnswer.DENY, answer)
    }

    @Test
    fun `the learner is told where to switch the microphone on when Android will not ask again`() {
        // Act
        val tells = tellsWhereToSwitchMicrophoneOn(learnerAllowedInDialog = false, androidShowsDialogAgain = false, toldSinceAppStart = false)

        // Assert
        assertTrue(tells)
    }

    @Test
    fun `the learner is not told after the first refusal because Android asks again`() {
        // Act
        val tells = tellsWhereToSwitchMicrophoneOn(learnerAllowedInDialog = false, androidShowsDialogAgain = true, toldSinceAppStart = false)

        // Assert
        assertFalse(tells)
    }

    @Test
    fun `the learner is told only once per start of the app`() {
        // Act
        val tells = tellsWhereToSwitchMicrophoneOn(learnerAllowedInDialog = false, androidShowsDialogAgain = false, toldSinceAppStart = true)

        // Assert
        assertFalse(tells)
    }

    @Test
    fun `the learner is not told when they allow the microphone`() {
        // Act
        val tells = tellsWhereToSwitchMicrophoneOn(learnerAllowedInDialog = true, androidShowsDialogAgain = false, toldSinceAppStart = false)

        // Assert
        assertFalse(tells)
    }
}
