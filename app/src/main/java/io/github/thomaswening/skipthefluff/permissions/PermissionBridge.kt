package io.github.thomaswening.skipthefluff.permissions

/**
 * The app's answer to the website when it asks for a device
 * ([REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038)).
 */
enum class DeviceAnswer {
    /** Pass the microphone, and only the microphone, to the website. */
    GRANT_MICROPHONE,

    /** Show Android's microphone dialog first and answer the website with the learner's choice. */
    ASK_LEARNER,

    /** Refuse the website's request. A speaking exercise then offers the website's own skip option. */
    DENY,
}

/**
 * How the app answers the website's request for a device, such as `getUserMedia({ audio: true })` in a
 * speaking exercise. The microphone is the only device the website needs, so anything else is refused
 * ([REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038)).
 *
 * @param wantsMicrophone whether the request includes the microphone. A request for the camera and the
 *   microphone together still gets only the microphone.
 * @param microphoneAllowed whether the learner has already given the app Android's microphone permission.
 */
fun answerDeviceRequest(wantsMicrophone: Boolean, microphoneAllowed: Boolean): DeviceAnswer = when {
    !wantsMicrophone -> DeviceAnswer.DENY
    microphoneAllowed -> DeviceAnswer.GRANT_MICROPHONE
    else -> DeviceAnswer.ASK_LEARNER
}

/**
 * How the app answers the website once the learner has answered Android's microphone dialog.
 * A refusal is passed on as it is: the website then offers its own option to skip speaking exercises for
 * now, which the app never uses on the learner's behalf
 * ([REQ-012](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-012)).
 *
 * @param learnerAllowed whether the learner allowed the microphone in Android's dialog.
 */
fun answerAfterLearner(learnerAllowed: Boolean): DeviceAnswer =
    if (learnerAllowed) DeviceAnswer.GRANT_MICROPHONE else DeviceAnswer.DENY
