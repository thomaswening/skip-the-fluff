package io.github.thomaswening.skipthefluff.shell

import io.github.thomaswening.skipthefluff.navigation.START_URL
import io.github.thomaswening.skipthefluff.navigation.isWebsiteAddress

/**
 * How long after a page started loading a loss of its renderer process counts as one more loss in a
 * row. A page that crashes its process on every load is lost within seconds; Android freeing memory
 * while the learner is in another app ends it minutes later. Decided by Thomas, 10 November 2026.
 */
internal const val QUICK_LOSS_MILLIS = 60_000L

/** The number of losses in a row after which the app tries the start page instead of the last address. */
private const val LOSSES_BEFORE_START_PAGE = 2

/** The number of losses in a row after which the app stops reloading. */
private const val LOSSES_BEFORE_GIVING_UP = 3

/**
 * What the app does once Android has ended the WebView's renderer process
 * ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md),
 * point 4). Either way the dead WebView is gone and the lesson is lost.
 */
internal sealed interface RendererRecovery {
    /** Build a new WebView and load [address] in it, without a message to the learner. */
    data class Reload(val address: String) : RendererRecovery

    /**
     * Build no new WebView and tell the learner the website could not be loaded. The next time the
     * learner returns to the app, it tries again from the start page.
     */
    data object GiveUp : RendererRecovery
}

/**
 * The number of renderer losses in a row, this one included. A loss counts as one more in a row if
 * it came less than [QUICK_LOSS_MILLIS] after the page started loading. Otherwise the count starts
 * again at 1.
 *
 * @param earlierLossesInARow the count returned for the previous loss, or 0 if there was none since the
 *   app started or since it gave up.
 * @param millisSincePageStarted the time from the moment the lost WebView started loading its page to
 *   the loss.
 */
internal fun lossesInARow(earlierLossesInARow: Int, millisSincePageStarted: Long): Int =
    if (millisSincePageStarted < QUICK_LOSS_MILLIS) earlierLossesInARow + 1 else 1

/**
 * How the app recovers from a lost renderer process
 * ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md),
 * point 4). The first loss reloads the page the learner was on. A second loss in a row means that
 * page may itself crash the process, so the app tries the start page. A third means the website
 * cannot be loaded at all, and the app stops instead of reloading forever.
 *
 * @param lossesInARow the count [lossesInARow] returned for this loss.
 * @param lastWebsiteAddress the last address of the website the WebView showed (see [isWebsiteAddress]),
 *   or null if it showed none.
 */
internal fun recoveryAfterLoss(lossesInARow: Int, lastWebsiteAddress: String?): RendererRecovery = when {
    lossesInARow >= LOSSES_BEFORE_GIVING_UP -> RendererRecovery.GiveUp
    lossesInARow >= LOSSES_BEFORE_START_PAGE -> RendererRecovery.Reload(START_URL)
    else -> RendererRecovery.Reload(lastWebsiteAddress ?: START_URL)
}
