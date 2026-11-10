package io.github.thomaswening.skipthefluff.navigation

/** The website's own domain. Its subdomains, such as www and support, belong to the website too. */
private const val WEBSITE_DOMAIN = "duolingo.com"

/** The page the app opens: the website's start page, which shows the learning path once signed in. */
internal const val START_URL = "https://www.duolingo.com/"

/**
 * Whether a navigation leaves the website and opens in the phone's default browser
 * ([REQ-015](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-015)).
 * That is the case for a page on any host other than duolingo.com and its subdomains. The website's
 * sign-in with Google, Facebook or Apple starts on such a host (accounts.google.com, www.facebook.com,
 * appleid.apple.com), so it opens in the browser too.
 *
 * @param host the host of the page the navigation leads to, as the URL names it. A URL without a host,
 *   such as about:blank, stays in the app as before.
 * @param isMainFrame whether the navigation replaces the whole page. A frame inside the page, such as a
 *   sign-in button the website embeds from Google, is a resource of the website and is never moved out.
 */
fun opensInDefaultBrowser(host: String?, isMainFrame: Boolean): Boolean {
    if (host == null || !isMainFrame) return false
    return !belongsToWebsite(host)
}

/**
 * Whether a host is duolingo.com or one of its subdomains, in any letter case. Compares whole labels,
 * so "evilduolingo.com" and "duolingo.com.evil.example" are other hosts.
 */
internal fun belongsToWebsite(host: String): Boolean {
    val lowercaseHost = host.lowercase()
    return lowercaseHost == WEBSITE_DOMAIN || lowercaseHost.endsWith(".$WEBSITE_DOMAIN")
}
