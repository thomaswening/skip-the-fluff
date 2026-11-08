// Throwaway spike, round 2, for the ADR on keeping the lesson across an app switch (REQ-014).
// Same as the WebView spike of ADR-0001 (docs/adr/0001-android-webview.md), plus:
// the page process keeps the app's priority in the background, the page is paused but its
// JavaScript keeps running, and a short message says which process Android ended, if any.
package io.github.thomaswening.skipthefluff.spike

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

private const val START_URL = "https://www.duolingo.com/"
private const val WEBSITE_HOST = "duolingo.com"
private const val MICROPHONE_REQUEST_CODE = 1

class MainActivity : Activity() {

    private lateinit var webView: WebView

    // A microphone request from the website that waits for Android's permission dialog.
    private var pendingMicrophoneRequest: PermissionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = createWebView()
        setContentView(webView)

        if (savedInstanceState == null) {
            webView.loadUrl(START_URL)
        } else {
            // Android ended the whole app in the background and has now started it again.
            say("Spike: the app was restarted by Android")
            webView.restoreState(savedInstanceState)
        }
    }

    private fun createWebView(): WebView {
        val view = WebView(this)
        view.settings.javaScriptEnabled = true
        view.settings.domStorageEnabled = true
        view.settings.mediaPlaybackRequiresUserGesture = false
        // New in round 2: the page process stays as important as the app when the app is not visible.
        view.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        CookieManager.getInstance().setAcceptCookie(true)
        view.webViewClient = WebsiteOnlyClient()
        view.webChromeClient = MicrophoneClient()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onPause() {
        super.onPause()
        // Pauses media and animations; the page's JavaScript, and with it the lesson, keeps running.
        webView.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    @Deprecated("Fine for a spike; the real app uses OnBackPressedDispatcher.")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode != MICROPHONE_REQUEST_CODE) return
        val request = pendingMicrophoneRequest ?: return
        pendingMicrophoneRequest = null
        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
        } else {
            request.deny()
        }
    }

    private fun say(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Replaces a WebView whose page process Android ended, instead of letting the app crash.
    private fun replaceWebView(lastUrl: String?) {
        webView.destroy()
        webView = createWebView()
        setContentView(webView)
        webView.loadUrl(lastUrl ?: START_URL)
    }

    private inner class WebsiteOnlyClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val host = request.url.host ?: return false
            if (host == WEBSITE_HOST || host.endsWith(".$WEBSITE_HOST")) return false
            try {
                startActivity(Intent(Intent.ACTION_VIEW, request.url))
            } catch (noBrowser: ActivityNotFoundException) {
                // Nothing to open it with; stay on the current page.
            }
            return true
        }

        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            val cause = if (detail.didCrash()) "crashed" else "was ended by Android"
            say("Spike: the page process $cause")
            replaceWebView(view.url)
            return true
        }
    }

    private inner class MicrophoneClient : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest) {
            val wantsMicrophone = PermissionRequest.RESOURCE_AUDIO_CAPTURE in request.resources
            if (!wantsMicrophone) {
                request.deny()
                return
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
            } else {
                pendingMicrophoneRequest = request
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), MICROPHONE_REQUEST_CODE)
            }
        }
    }
}
