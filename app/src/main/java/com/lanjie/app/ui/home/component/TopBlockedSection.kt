package com.lanjie.app.ui.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lanjie.app.R
import com.lanjie.app.data.entities.TopBlockedDomain
import com.lanjie.app.ui.theme.DangerRed
import androidx.compose.foundation.clickable
import com.lanjie.app.ui.theme.TextSecondary
import com.lanjie.app.utils.formatCount

@Composable
fun TopBlockedSection(
    topBlockedDomains: List<TopBlockedDomain>,
    onDomainClick: (TopBlockedDomain) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (topBlockedDomains.isEmpty()) return

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.home_top_blocked),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                topBlockedDomains.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDomainClick(entry) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            text = entry.domain,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatCount(entry.count),
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
