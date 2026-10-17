package com.xxmrk888ytxx.storagescreen.MediaFileItem

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.coreandroid.toDateString
import com.xxmrk888ytxx.coreandroid.toTimeString
import com.xxmrk888ytxx.storagescreen.MediaFileItem.models.MediaFileButton
import com.xxmrk888ytxx.storagescreen.R
import kotlinx.collections.immutable.ImmutableList

@SuppressLint("ResourceType")
@Composable
fun LazyItemScope.MediaFileItem(
    duration: Long,
    created: Long,
    name: String,
    buttons: ImmutableList<MediaFileButton>,
    isDualRecord: Boolean = false,
    isVideo: Boolean = false
) {
    val context = LocalContext.current

    // Buttons mapping by index:
    // index 0: Play
    // index 1: Delete
    // index 2: Rename
    // index 3: Export
    val playButton = buttons.getOrNull(0)
    val deleteButton = buttons.getOrNull(1)
    val renameButton = buttons.getOrNull(2)
    val exportButton = buttons.getOrNull(3)

    val onCardClick = { playButton?.onClick?.invoke() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF131926))
            .border(1.dp, Color(0xFF263346), RoundedCornerShape(22.dp))
            .clickable { onCardClick() }
            .padding(16.dp)
            .animateItem()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Content Row: Media Badge + Details + Play Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Media Icon / Thumbnail Badge
                val badgeGradient = when {
                    isDualRecord -> Brush.linearGradient(
                        listOf(Color(0xFF6366F1), Color(0xFF06B6D4))
                    )
                    isVideo -> Brush.linearGradient(
                        listOf(Color(0xFF3B82F6), Color(0xFF6366F1))
                    )
                    else -> Brush.linearGradient(
                        listOf(Color(0xFF059669), Color(0xFF10B981))
                    )
                }
                val mediaIconRes = when {
                    isVideo -> R.drawable.video
                    else -> R.drawable.music
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(badgeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(mediaIconRes),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title, Date, Badges
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    val displayTitle = if (name.isNotBlank()) name else {
                        if (isVideo) stringResource(R.string.video_record_default_title)
                        else stringResource(R.string.audio_record_default_title)
                    }

                    Text(
                        text = displayTitle,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${created.toDateString(context)}, ${created.toTimeString()}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chips row: Duration and Dual Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Duration Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2838))
                                .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = duration.milliSecondToString(),
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Dual Camera Chip
                        if (isDualRecord) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x336366F1))
                                    .border(0.5.dp, Color(0x66818CF8), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.dual_camera_badge),
                                    color = Color(0xFFA5B4FC),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Primary Play Action (Prominent Round Accent Button)
                if (playButton != null) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
                                )
                            )
                            .border(1.dp, Color(0xFF818CF8), CircleShape)
                            .clickable { playButton.onClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_play_arrow_24),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Thin Divider between Info and Secondary Actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x1F94A3B8))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Actions Bar (Export, Rename on the Left; Delete safely on the Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left secondary actions: Export & Rename
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (exportButton != null) {
                        ActionButton(
                            iconRes = exportButton.icon,
                            tint = Color(0xFF94A3B8),
                            background = Color(0xFF1A2232),
                            borderColor = Color(0xFF263346),
                            onClick = exportButton.onClick
                        )
                    }

                    if (renameButton != null) {
                        ActionButton(
                            iconRes = renameButton.icon,
                            tint = Color(0xFF94A3B8),
                            background = Color(0xFF1A2232),
                            borderColor = Color(0xFF263346),
                            onClick = renameButton.onClick
                        )
                    }
                }

                // Right danger action: Delete (Visually separated with subtle danger red)
                if (deleteButton != null) {
                    ActionButton(
                        iconRes = deleteButton.icon,
                        tint = Color(0xFFF87171),
                        background = Color(0x22EF4444),
                        borderColor = Color(0x44EF4444),
                        onClick = deleteButton.onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    iconRes: Int,
    tint: Color,
    background: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}