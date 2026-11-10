package io.github.thomaswening.skipthefluff.screens

import io.github.thomaswening.skipthefluff.navigation.START_URL
import io.github.thomaswening.skipthefluff.navigation.isWebsiteAddress

/**
 * Whether the app shows its no-connection screen instead of the website
 * ([REQ-044](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-044)).
 * The screen replaces Android's raw error page when a page of the website cannot be loaded. It never
 * reloads by itself: only the learner's tap on its button does.
 */
internal sealed interface NoConnection {
    /** The website is shown, or is loading. */
    data object Hidden : NoConnection

    /**
     * The screen is shown.
     *
     * @property reloadAddress the page the button loads.
     * @property isReloading whether that page is loading, so that the button waits instead of starting
     *   a second load.
     */
    data class Shown(val reloadAddress: String, val isReloading: Boolean) : NoConnection
}

/**
 * The address of a page that could not be loaded, with the parts the shell reads from it.
 *
 * @property scheme the address's scheme, such as `https`, or null if it has none.
 * @property host the address's host, such as `www.duolingo.com`, or null if it has none.
 * @property address the whole address.
 */
internal data class FailedAddress(val scheme: String?, val host: String?, val address: String)

/**
 * The screen after the WebView reported that a load failed, whatever the error: no network, no answer
 * from duolingo.com, a broken secure connection. Each of them would otherwise show Android's raw error
 * page. A failed image or script inside the page leaves the screen as it was. The page around it is
 * still shown, and the website handles its own failed requests. So does an HTTP error such as 503:
 * duolingo.com answered with a page of its own. Decided by Thomas, 10 November 2026.
 *
 * @param current the screen before the failure.
 * @param isForMainFrame whether the failed load was the page itself rather than a part of it.
 * @param failedAddress the address that failed. The button reloads it if it is a page of the website,
 *   otherwise the start page.
 */
internal fun afterLoadFailed(current: NoConnection, isForMainFrame: Boolean, failedAddress: FailedAddress): NoConnection {
    if (!isForMainFrame) return current
    val reloadAddress = if (isWebsiteAddress(failedAddress.scheme, failedAddress.host)) failedAddress.address else START_URL
    return NoConnection.Shown(reloadAddress, isReloading = false)
}

/**
 * The screen once a page starts loading: after the learner's tap on the button, or any other load,
 * such as one the back gesture starts. The screen stays until that load has finished without an error.
 */
internal fun afterLoadStarted(current: NoConnection): NoConnection = when (current) {
    NoConnection.Hidden -> current
    is NoConnection.Shown -> current.copy(isReloading = true)
}

/**
 * The screen once a page has finished loading. The WebView reports the end of a failed load too, after
 * the error, which has already set the screen to not reloading. So only a load that is still reloading
 * here succeeded, and the website is shown again.
 */
internal fun afterLoadFinished(current: NoConnection): NoConnection = when (current) {
    NoConnection.Hidden -> current
    is NoConnection.Shown -> if (current.isReloading) NoConnection.Hidden else current
}
