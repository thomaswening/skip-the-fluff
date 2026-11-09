package io.github.thomaswening.skipthefluff.shell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.thomaswening.skipthefluff.BuildConfig
import io.github.thomaswening.skipthefluff.R

/**
 * The app's one window. In phase 0 it shows only the app's name and version; phase 1 puts the
 * WebView here (https://github.com/thomaswening/skip-the-fluff/blob/main/docs/roadmap.md#phase-1-the-shell).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlaceholderScreen() }
    }
}

@Composable
private fun PlaceholderScreen() {
    val colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.placeholder, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
