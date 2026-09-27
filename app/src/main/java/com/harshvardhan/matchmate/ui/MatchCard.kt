package com.harshvardhan.matchmate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.harshvardhan.matchmate.data.local.MatchEntity
import com.harshvardhan.matchmate.domain.MatchStatus

@Composable
fun MatchCard(
    match: MatchEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            AsyncImage(
                model = match.imageUrl,
                contentDescription =
                    "${match.firstName} ${match.lastName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "${match.firstName} ${match.lastName}",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "${match.age} • ${match.city}, ${match.country}"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            when (match.status) {

                MatchStatus.ACCEPTED -> {

                    StatusBadge(
                        text = "Member Accepted"
                    )
                }

                MatchStatus.DECLINED -> {

                    StatusBadge(
                        text = "Member Declined"
                    )
                }

                MatchStatus.PENDING -> {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onDecline
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Close,
                                contentDescription = null
                            )

                            Spacer(
                                Modifier.size(6.dp)
                            )

                            Text("Decline")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onAccept
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Check,
                                contentDescription = null
                            )

                            Spacer(
                                Modifier.size(6.dp)
                            )

                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String
) {

    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = androidx.compose.material3.MaterialTheme
                    .colorScheme
                    .secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp),
        fontWeight = FontWeight.SemiBold
    )
}