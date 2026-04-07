package com.example.booking.presentation.stays.common

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun StaySelectionRow(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }

    Row(
        modifier = rowModifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = BookingTextPrimary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = BookingTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = BookingTextSecondary
        )
    }
}

@Composable
fun StayTopTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StayTabChip(title = "Stays", selected = true)
        StayTabChip(title = "Flights", selected = false)
        StayTabChip(title = "Flight + Hotel", selected = false)
    }
}

@Composable
private fun StayTabChip(
    title: String,
    selected: Boolean
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) BookingWhite.copy(alpha = 0.12f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = BookingWhite.copy(alpha = if (selected) 0.42f else 0.18f)
        )
    ) {
        Text(
            text = title,
            color = BookingWhite.copy(alpha = if (selected) 1f else 0.72f),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun StayFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) BookingBlueLight.copy(alpha = 0.12f) else BookingWhite,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) BookingBlueLight else BookingGray
        )
    ) {
        Text(
            text = text,
            color = if (selected) BookingBlue else BookingTextPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun StayFooterBar(
    priceLine: String,
    subLine: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 10.dp,
        color = BookingWhite
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = priceLine,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subLine,
                color = BookingTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp)
            )
            BookingPrimaryButton(
                text = buttonText,
                enabled = enabled,
                onClick = onClick,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun StayCounterRow(
    title: String,
    subtitle: String? = null,
    value: Int,
    minValue: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = BookingTextPrimary, fontWeight = FontWeight.Medium)
            subtitle?.let {
                Text(
                    text = it,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        CounterButton(
            icon = Icons.Filled.Remove,
            enabled = value > minValue,
            onClick = onDecrement
        )
        Text(
            text = value.toString(),
            modifier = Modifier.padding(horizontal = 18.dp),
            color = BookingTextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        CounterButton(
            icon = Icons.Filled.Add,
            enabled = true,
            onClick = onIncrement
        )
    }
}

@Composable
private fun CounterButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(38.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) BookingWhite else BookingGray,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (enabled) BookingBlueLight else BookingGray
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) BookingBlueLight else BookingTextSecondary
            )
        }
    }
}

@Composable
fun StaySwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = BookingTextPrimary, fontWeight = FontWeight.Medium)
            Text(
                text = subtitle,
                color = BookingTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun StayPhotoPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val cornerSize = if (compact) 14.dp else 20.dp
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8EC5FF), Color(0xFFE0F0FF))
                ),
                shape = RoundedCornerShape(cornerSize)
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(if (compact) 34.dp else 52.dp)
                .background(Color.White.copy(alpha = 0.22f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.take(1).uppercase(),
                color = BookingWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StaySummaryInfoCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = BookingWhite,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = BookingTextPrimary, fontWeight = FontWeight.Bold)
            Text(
                text = description,
                color = BookingTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun StayDividerSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BookingGray)
    )
}
