package io.github.thomaswening.skipthefluff.shell

/** The path of the learning path, the page the website shows after sign-in. */
private const val LEARNING_PATH = "/learn"

/**
 * Whether Android's back gesture goes back one page in the website (true) or closes the app (false),
 * as [REQ-037](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-037)
 * asks: back in the website's history, except on the learning path, which closes the app.
 * Asking before a lesson ends follows with the lesson state in phase 2.
 *
 * @param path the path of the page shown, e.g. `/learn` or `/lesson`, or null if it has none.
 * @param canGoBack whether the WebView has an earlier page in its history.
 */
internal fun goesBackInWebsite(path: String?, canGoBack: Boolean): Boolean =
    canGoBack && !isLearningPath(path)

private fun isLearningPath(path: String?): Boolean =
    path?.trimEnd('/') == LEARNING_PATH
