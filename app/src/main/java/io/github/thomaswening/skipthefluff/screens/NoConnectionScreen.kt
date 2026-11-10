package io.github.thomaswening.skipthefluff.screens

import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.thomaswening.skipthefluff.R
import kotlinx.coroutines.delay

/** The space around the screen's content and between its lines. */
private val SPACING = 24.dp

/** The size of the spinner that replaces the button's label while the page reloads. */
private val SPINNER_SIZE = 20.dp

/**
 * The app's own screen when the website cannot be reached
 * ([REQ-044](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-044)).
 * It covers the whole window, and with it Android's raw error page in the WebView beneath, in the
 * system's colours. Its texts name what the learner can check and what the button does.
 *
 * The menu button ([REQ-035](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-035))
 * does not exist yet. Phase 4 draws it above both the website and this screen, so that it stays
 * available here.
 *
 * @param state the shown screen: what the button reloads and whether it is reloading.
 * @param onReload called when the learner taps the button.
 */
@Composable
internal fun NoConnectionScreen(state: NoConnectionState.Shown, onReload: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(SPACING),
            verticalArrangement = Arrangement.spacedBy(SPACING, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.no_connection_title), style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(stringResource(R.string.no_connection_text), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            ReloadButton(state.isReloading, onReload)
        }
    }
}

/**
 * The button that reloads the page. While the page reloads, and for at least [MIN_SPINNER_MILLIS] after
 * the tap, it shows a spinner and takes no tap, so that the learner sees the tap was taken and one tap
 * starts one load.
 */
@Composable
private fun ReloadButton(isReloading: Boolean, onReload: () -> Unit) {
    // remember keeps a value across redraws, like a field of a WPF control.
    var spinnerStartedAtMillis by remember { mutableLongStateOf(0L) }
    var keepsSpinner by remember { mutableStateOf(false) }
    // Runs each time isReloading changes. After a failed reload, it waits out the rest of the minimum.
    LaunchedEffect(isReloading) {
        if (isReloading) {
            spinnerStartedAtMillis = SystemClock.elapsedRealtime()
            keepsSpinner = true
            return@LaunchedEffect
        }
        delay(remainingSpinnerMillis(SystemClock.elapsedRealtime() - spinnerStartedAtMillis))
        keepsSpinner = false
    }
    val showsSpinner = isReloading || keepsSpinner
    Button(onClick = onReload, enabled = !showsSpinner) {
        if (showsSpinner) {
            CircularProgressIndicator(modifier = Modifier.size(SPINNER_SIZE))
        } else {
            Text(stringResource(R.string.no_connection_reload))
        }
    }
}
