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
 *   microphone together gets the microphone only, which Chromium treats as a refusal of the whole request
 *   (seen on the emulator, 10 November 2026). The website asks for the microphone alone.
 * @param appHoldsMicrophonePermission whether Android records that the learner gave the app its microphone
 *   permission in an earlier dialog. Android keeps that choice, and the learner can change it in Android's
 *   app settings. The app stores nothing and reads it again for each request.
 */
fun answerDeviceRequest(wantsMicrophone: Boolean, appHoldsMicrophonePermission: Boolean): DeviceAnswer = when {
    !wantsMicrophone -> DeviceAnswer.DENY
    appHoldsMicrophonePermission -> DeviceAnswer.GRANT_MICROPHONE
    else -> DeviceAnswer.ASK_LEARNER
}

/**
 * How the app answers the website once the learner has tapped an answer in Android's microphone dialog,
 * which [answerDeviceRequest] asked for. Android stores the answer itself, so the next request goes
 * through [answerDeviceRequest] again without a dialog.
 *
 * A refusal ends here and never opens the dialog again. The website then offers its own option to skip
 * speaking exercises for now, which the app never uses on the learner's behalf
 * ([REQ-012](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-012)).
 *
 * @param learnerAllowedInDialog whether the learner allowed the microphone in the dialog just shown.
 */
fun answerAfterDialog(learnerAllowedInDialog: Boolean): DeviceAnswer =
    if (learnerAllowedInDialog) DeviceAnswer.GRANT_MICROPHONE else DeviceAnswer.DENY

/**
 * Whether the app tells the learner, after Android's microphone dialog, that the microphone is off and
 * where in Android's settings to switch it on
 * ([REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)).
 * It does so only when Android will not show its dialog again, because then only the app's page in
 * Android's settings can switch the microphone on.
 *
 * @param learnerAllowedInDialog whether the learner allowed the microphone in the dialog just shown, or
 *   Android allowed it without a dialog.
 * @param androidShowsDialogAgain Android's `shouldShowRequestPermissionRationale`, read right after the
 *   answer. It is true after the first refusal only: the next request shows the dialog again, where the
 *   learner can still change their mind. It is false after the second refusal and after a refusal that
 *   Android made by itself without a dialog. It is also false when the learner closed the very first
 *   dialog without an answer. The message then comes one dialog early but is still true.
 * @param toldSinceAppStart whether the app has told the learner so since the app started. The message
 *   shows at most once per start, so that it does not repeat in every speaking exercise.
 */
fun tellsWhereToSwitchMicrophoneOn(
    learnerAllowedInDialog: Boolean,
    androidShowsDialogAgain: Boolean,
    toldSinceAppStart: Boolean,
): Boolean = !learnerAllowedInDialog && !androidShowsDialogAgain && !toldSinceAppStart
