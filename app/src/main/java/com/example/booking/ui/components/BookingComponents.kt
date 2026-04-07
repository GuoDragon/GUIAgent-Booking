package com.example.booking.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHomeTopBar(
    title: String,
    actions: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = BookingWhite,
                fontWeight = FontWeight.Bold
            )
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BookingBlue,
            titleContentColor = BookingWhite,
            actionIconContentColor = BookingWhite
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBackTopBar(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = BookingWhite,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            TextButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = "Back", color = BookingWhite)
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BookingBlue,
            titleContentColor = BookingWhite,
            navigationIconContentColor = BookingWhite,
            actionIconContentColor = BookingWhite
        )
    )
}

@Composable
fun BookingFloatingBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = BookingTextPrimary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .statusBarsPadding()
            .padding(start = 8.dp, top = 8.dp)
            .size(48.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = tint
        )
    }
}

@Composable
fun BookingSheetHandle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 54.dp, height = 4.dp)
            .background(BookingGray, RoundedCornerShape(999.dp))
    )
}

@Composable
fun BookingRoundedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = BookingWhite,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun BookingSectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = BookingTextPrimary,
            fontWeight = FontWeight.Bold
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = BookingTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun BookingSettingRow(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingText: String? = null,
    badgeText: String? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        leadingIcon?.let {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(BookingGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = BookingTextPrimary
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Medium
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        trailingText?.let {
            Text(
                text = it,
                color = BookingTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        badgeText?.let {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFDDF4DE)
            ) {
                Text(
                    text = it,
                    color = Color(0xFF1C7C35),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        if (showChevron) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = BookingTextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun BookingCardDivider() {
    HorizontalDivider(color = BookingGray)
}

@Composable
fun BookingInitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Int = 52
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8DC63F), Color(0xFF4CAF50))
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = BookingWhite,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BookingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BookingBlueLight,
            contentColor = BookingWhite
        )
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun BookingEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BookingBlueLight.copy(alpha = 0.18f),
                            Color(0xFFFFE7A0),
                            BookingWhite
                        )
                    ),
                    shape = RoundedCornerShape(40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BookingBlue,
                modifier = Modifier.size(54.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BookingTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = BookingTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun BookingStatusChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun BookingTopBarAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit = {}
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = BookingWhite
        )
    }
}
