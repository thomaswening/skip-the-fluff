package io.github.thomaswening.skipthefluff.shell

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.thomaswening.skipthefluff.R
import io.github.thomaswening.skipthefluff.navigation.opensInDefaultBrowser

/** The page the app opens: the website's start page, which shows the learning path once signed in. */
private const val START_URL = "https://www.duolingo.com/"

/**
 * The app's one window. It shows the website full screen in one WebView that lives as long as the
 * window ([ADR-0001](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0001-android-webview.md)).
 * The manifest declares every configuration change, so Android never rebuilds this window and the
 * WebView, with the lesson in it, survives a rotation or a dark-mode switch
 * ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md), point 1).
 */
class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    // Enabled while the back gesture goes back in the website; disabled, Android closes the app.
    private val backInWebsite = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = webView.goBack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The website is drawn behind the system bars, and the padding in WebsiteScreen leaves room for them.
        enableEdgeToEdge()
        webView = createWebView()
        onBackPressedDispatcher.addCallback(this, backInWebsite)
        webView.loadUrl(START_URL)
        setContent { WebsiteScreen(webView) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Light or dark status bar icons follow a new dark-mode setting. Compose redraws the rest itself.
        enableEdgeToEdge()
    }

    override fun onPause() {
        super.onPause()
        // Stops media and animations. The page's JavaScript, and with it the lesson, keeps running
        // ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md), point 3). The cookies are written to disk now, in case Android ends the app.
        webView.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    private fun createWebView(): WebView {
        val view = WebView(this)
        // Fill the parent, not "as tall as the content", which AndroidView would otherwise assume. With
        // the latter, the WebView reports the CSS unit vh as 0, and the website's lesson layout,
        // sized in vh, slides under its own header (found on the reference device, 10 November 2026).
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // The website is a JavaScript app that keeps its state in the page's storage.
        view.settings.javaScriptEnabled = true
        view.settings.domStorageEnabled = true
        // [REQ-016](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-016):
        // the sign-in cookie keeps the learner signed in across restarts.
        CookieManager.getInstance().setAcceptCookie(true)
        // The renderer process stays as important as the app when the app is not visible ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md), point 2).
        view.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        view.webViewClient = WebsiteClient()
        return view
    }

    /**
     * Keeps the WebView on the website and updates where the back gesture leads each time the website's
     * history changes.
     */
    private inner class WebsiteClient : WebViewClient() {
        override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
            val path = url?.let { Uri.parse(it).path }
            backInWebsite.isEnabled = goesBackInWebsite(path, view.canGoBack())
        }

        // Called for each navigation the page starts, including window.open, which the website's
        // sign-in buttons use and which the WebView turns into a navigation of its one window.
        // Returning true cancels it. Images, scripts and other resources never pass through here.
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (!opensInDefaultBrowser(request.url.host, request.isForMainFrame)) return false
            openInDefaultBrowser(request.url)
            return true
        }
    }

    private fun openInDefaultBrowser(url: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url))
        } catch (noBrowser: ActivityNotFoundException) {
            // No app on the phone opens web pages. The learner stays on the current page and is told
            // why nothing opened and what to do.
            Toast.makeText(this, R.string.no_browser, Toast.LENGTH_LONG).show()
        }
    }
}

/** The WebView fills the window inside the system bars and the keyboard, in the system's colours. */
@Composable
private fun WebsiteScreen(webView: WebView) {
    val colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme) {
        // The surface colours the strips behind the status and navigation bars.
        Surface(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            )
        }
    }
}
