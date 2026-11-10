package io.github.thomaswening.skipthefluff.screens

import io.github.thomaswening.skipthefluff.navigation.START_URL
import io.github.thomaswening.skipthefluff.navigation.isWebsiteAddress

/**
 * The shortest time the reload button shows its spinner. A reload without a network fails within
 * milliseconds, so without it the learner would see no sign that the tap did anything. Decided by
 * Thomas, 10 November 2026.
 */
internal const val MIN_SPINNER_MILLIS = 1_000L

/**
 * The address of a page the WebView loads, with the parts the shell reads from it.
 *
 * @property scheme the address's scheme, such as `https`, or null if it has none.
 * @property host the address's host, such as `www.duolingo.com`, or null if it has none.
 * @property url the whole address.
 */
internal data class PageAddress(val scheme: String?, val host: String?, val url: String)

/**
 * The state of the no-connection screen ([NoConnectionScreen]): hidden, or shown in place of
 * Android's raw error page when a page of the website cannot be loaded
 * ([REQ-044](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-044)).
 * The screen never reloads by itself: only the learner's tap on its button does.
 *
 * The WebView reports each load as started, then failed if it failed, then finished. Each report
 * gives a new state, as `with` gives a new record in C#.
 */
internal sealed interface NoConnectionState {
    /** The website is shown, or is loading. */
    data object Hidden : NoConnectionState

    /**
     * The screen is shown.
     *
     * @property reloadUrl the page the button loads.
     * @property isReloading whether that page is loading, so that the button waits instead of starting
     *   a second load.
     */
    data class Shown(val reloadUrl: String, val isReloading: Boolean) : NoConnectionState

    /**
     * The state once a page starts loading: after the learner's tap on the button, or any other load,
     * such as one the back gesture starts. The screen stays until that load has finished without an error.
     */
    fun afterLoadStarted(): NoConnectionState = when (this) {
        Hidden -> this
        is Shown -> copy(isReloading = true)
    }

    /**
     * The state after a load failed, whatever the error: no network, no answer from duolingo.com, a
     * broken secure connection. Each of them would otherwise show Android's raw error page. A failed
     * image or script inside the page changes nothing: the page around it is still shown, and the
     * website handles its own failed requests. Neither does an HTTP error such as 503, because
     * duolingo.com answered with a page of its own. Decided by Thomas, 10 November 2026.
     *
     * @param address the address that failed. The button reloads it if it is a page of the website,
     *   otherwise the start page.
     * @param isMainFrame whether the failed load was the page itself rather than a part of it.
     */
    fun afterLoadFailed(address: PageAddress, isMainFrame: Boolean): NoConnectionState {
        if (!isMainFrame) return this
        val reloadUrl = if (isWebsiteAddress(address.scheme, address.host)) address.url else START_URL
        return Shown(reloadUrl, isReloading = false)
    }

    /**
     * The state once a page has finished loading. The WebView reports the end of a failed load too,
     * after the error, which has already stopped the reloading. So only a load that is still reloading
     * here succeeded, and the website is shown again.
     */
    fun afterLoadFinished(): NoConnectionState = when (this) {
        Hidden -> this
        is Shown -> if (isReloading) Hidden else this
    }
}

/**
 * How much longer the spinner stays after a reload has ended, so that it shows for at least
 * [MIN_SPINNER_MILLIS] in all.
 *
 * @param spinnerMillis how long the spinner has shown so far.
 */
internal fun remainingSpinnerMillis(spinnerMillis: Long): Long = (MIN_SPINNER_MILLIS - spinnerMillis).coerceAtLeast(0L)
