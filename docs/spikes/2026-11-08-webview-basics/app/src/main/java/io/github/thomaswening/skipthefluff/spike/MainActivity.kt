// Throwaway spike for ADR-0001 (docs/adr/0001-android-webview.md): shows duolingo.com in
// Android's WebView with the settings the ADR names, and nothing else. No fixes are applied.
package io.github.thomaswening.skipthefluff.spike

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

private const val START_URL = "https://www.duolingo.com/"
private const val WEBSITE_HOST = "duolingo.com"
private const val MICROPHONE_REQUEST_CODE = 1

class MainActivity : Activity() {

    private lateinit var webView: WebView

    // A microphone request from the website that waits for Android's permission dialog.
    private var pendingMicrophoneRequest: PermissionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        // REQ-038: listening exercises play their audio without a tap.
        webView.settings.mediaPlaybackRequiresUserGesture = false
        // REQ-016: the sign-in cookie survives restarts.
        CookieManager.getInstance().setAcceptCookie(true)

        webView.webViewClient = WebsiteOnlyClient()
        webView.webChromeClient = MicrophoneClient()

        if (savedInstanceState == null) {
            webView.loadUrl(START_URL)
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
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

    // REQ-015: pages on other hosts open in the default browser.
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
    }

    // REQ-038: the website gets the microphone once Android's permission is granted.
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
