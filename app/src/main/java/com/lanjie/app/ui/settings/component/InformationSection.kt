package com.lanjie.app.ui.settings.component

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.lanjie.app.R

@Composable
fun InformationSection(
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)

    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.settings_category_info),
            description = stringResource(R.string.settings_category_info_desc)
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingsCard {
            SettingItem(
                icon = Icons.Default.Info,
                iconTint = Color(0xFF64748B),
                title = stringResource(R.string.settings_about),
                desc = stringResource(R.string.settings_about_desc),
                onClick = onNavigateToAbout
            )
        }
    }
}
