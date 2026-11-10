package io.github.thomaswening.skipthefluff.shell

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import io.github.thomaswening.skipthefluff.R
import io.github.thomaswening.skipthefluff.navigation.opensInDefaultBrowser
import io.github.thomaswening.skipthefluff.permissions.DeviceAnswer
import io.github.thomaswening.skipthefluff.permissions.answerAfterDialog
import io.github.thomaswening.skipthefluff.permissions.answerDeviceRequest
import io.github.thomaswening.skipthefluff.permissions.tellsWhereToSwitchMicrophoneOn

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

    // The website's microphone request while Android's dialog asks the learner; null otherwise.
    private var requestWaitingForLearner: PermissionRequest? = null

    // Whether the bar saying where to switch the microphone on has been shown since the app started
    // ([REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)).
    // Kept in memory only, so the next start of the app may show it again.
    private var toldWhereToSwitchMicrophoneOn = false

    // Whether that bar is on screen. Compose state, like an observable property in WPF: the screen
    // redraws when it changes.
    private var showsMicrophoneHint by mutableStateOf(false)

    // Shows Android's permission dialog and calls back with the learner's answer. Registered when the
    // window is created, as Android requires, so the answer arrives even if the window was paused meanwhile.
    // When Android will not show its dialog again, it answers at once without one.
    private val microphoneDialog = registerForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
        val androidShowsDialogAgain = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
        if (tellsWhereToSwitchMicrophoneOn(allowed, androidShowsDialogAgain, toldWhereToSwitchMicrophoneOn)) {
            toldWhereToSwitchMicrophoneOn = true
            showsMicrophoneHint = true
        }
        val request = requestWaitingForLearner ?: return@registerForActivityResult
        requestWaitingForLearner = null
        answerWebsite(request, answerAfterDialog(learnerAllowedInDialog = allowed))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The website is drawn behind the system bars, and the padding in WebsiteScreen leaves room for them.
        enableEdgeToEdge()
        webView = createWebView()
        onBackPressedDispatcher.addCallback(this, backInWebsite)
        webView.loadUrl(START_URL)
        setContent { WebsiteScreen(webView, showsMicrophoneHint, ::onMicrophoneHintClosed) }
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
        // [REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038): a
        // listening exercise plays its audio by itself, as in Chrome, instead of waiting for a tap.
        view.settings.mediaPlaybackRequiresUserGesture = false
        // [REQ-016](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-016):
        // the sign-in cookie keeps the learner signed in across restarts.
        CookieManager.getInstance().setAcceptCookie(true)
        // The renderer process stays as important as the app when the app is not visible ([ADR-0003](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0003-lesson-survives-switch.md), point 2).
        view.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        view.webViewClient = WebsiteClient()
        view.webChromeClient = DeviceRequestClient()
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

    /**
     * Passes the website's requests for a device, such as the microphone in a speaking exercise, to
     * the permission bridge ([REQ-038](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-038)).
     * Without this client the WebView refuses every such request.
     */
    private inner class DeviceRequestClient : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest) {
            val wantsMicrophone = PermissionRequest.RESOURCE_AUDIO_CAPTURE in request.resources
            val appHoldsMicrophonePermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            answerWebsite(request, answerDeviceRequest(wantsMicrophone, appHoldsMicrophonePermission))
        }

        // The website withdrew its request, e.g. because the learner left the exercise while the dialog
        // was open. The learner's later answer then has no request to go to.
        override fun onPermissionRequestCanceled(request: PermissionRequest) {
            if (request == requestWaitingForLearner) requestWaitingForLearner = null
        }
    }

    private fun answerWebsite(request: PermissionRequest, answer: DeviceAnswer) {
        when (answer) {
            DeviceAnswer.GRANT_MICROPHONE -> request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
            DeviceAnswer.DENY -> request.deny()
            DeviceAnswer.ASK_LEARNER -> askLearner(request)
        }
    }

    private fun askLearner(request: PermissionRequest) {
        // Android shows one dialog at a time. A second request while it is open is refused, so that it
        // does not wait forever; the website asks again in its next speaking exercise.
        if (requestWaitingForLearner != null) {
            request.deny()
            return
        }
        requestWaitingForLearner = request
        microphoneDialog.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun onMicrophoneHintClosed(learnerChoseSettings: Boolean) {
        showsMicrophoneHint = false
        if (learnerChoseSettings) openAppSettings()
    }

    // Android's page for this app, where Permissions, then Microphone, switches the microphone on
    // ([REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)).
    // Every Android phone has it, so unlike the browser it needs no fallback.
    private fun openAppSettings() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)))
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

/**
 * The WebView fills the window inside the system bars and the keyboard, in the system's colours.
 *
 * @param showsMicrophoneHint whether to show the bar that says where to switch the microphone on
 *   ([REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)).
 * @param onMicrophoneHintClosed called once the bar is gone, with whether the learner tapped its button.
 */
@Composable
private fun WebsiteScreen(
    webView: WebView,
    showsMicrophoneHint: Boolean,
    onMicrophoneHintClosed: (learnerChoseSettings: Boolean) -> Unit,
) {
    val colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme) {
        // The surface colours the strips behind the status and navigation bars.
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                AndroidView(factory = { webView }, modifier = Modifier.fillMaxSize())
                MicrophoneHint(showsMicrophoneHint, onMicrophoneHintClosed, Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

/**
 * A bar at the bottom of the screen saying that the microphone is off, with a button to the app's page
 * in Android's settings and a close button
 * ([REQ-049](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-049)).
 * It covers the website's lowest buttons until it disappears by itself after about ten seconds or the
 * learner closes it. Thomas accepted that, so that the lesson goes on (10 November 2026).
 */
@Composable
private fun MicrophoneHint(
    isShown: Boolean,
    onClosed: (learnerChoseSettings: Boolean) -> Unit,
    modifier: Modifier,
) {
    val hostState = remember { SnackbarHostState() }
    val message = stringResource(R.string.microphone_off)
    val settingsLabel = stringResource(R.string.microphone_off_settings)
    // Runs each time isShown changes, like an async handler of a property change. showSnackbar waits
    // until the bar is gone and says why.
    LaunchedEffect(isShown) {
        if (!isShown) return@LaunchedEffect
        val result = hostState.showSnackbar(message, settingsLabel, withDismissAction = true, duration = SnackbarDuration.Long)
        onClosed(result == SnackbarResult.ActionPerformed)
    }
    SnackbarHost(hostState, modifier)
}
