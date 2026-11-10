package io.github.thomaswening.skipthefluff.permissions

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests of how the app answers the website's requests for a device
 * ([REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038)).
 */
class PermissionBridgeTest {
    @Test
    fun `the microphone is passed on when the learner has already allowed it`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = true, microphoneAllowed = true)

        // Assert
        assertEquals(DeviceAnswer.GRANT_MICROPHONE, answer)
    }

    @Test
    fun `the learner is asked when the website wants the microphone for the first time`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = true, microphoneAllowed = false)

        // Assert
        assertEquals(DeviceAnswer.ASK_LEARNER, answer)
    }

    @Test
    fun `a request without the microphone is refused although the microphone is allowed`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = false, microphoneAllowed = true)

        // Assert
        assertEquals(DeviceAnswer.DENY, answer)
    }

    @Test
    fun `a request without the microphone is refused without asking the learner`() {
        // Act
        val answer = answerDeviceRequest(wantsMicrophone = false, microphoneAllowed = false)

        // Assert
        assertEquals(DeviceAnswer.DENY, answer)
    }

    @Test
    fun `the microphone is passed on when the learner allows it in Android's dialog`() {
        // Act
        val answer = answerAfterLearner(learnerAllowed = true)

        // Assert
        assertEquals(DeviceAnswer.GRANT_MICROPHONE, answer)
    }

    @Test
    fun `the website is refused when the learner refuses the microphone`() {
        // Act
        val answer = answerAfterLearner(learnerAllowed = false)

        // Assert
        assertEquals(DeviceAnswer.DENY, answer)
    }
}
