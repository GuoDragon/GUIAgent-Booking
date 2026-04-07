package com.example.booking.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BookingReferenceImage(
    assetPath: String?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    contentDescription: String? = null
) {
    val context = LocalContext.current.applicationContext
    val shape = RoundedCornerShape(if (compact) 14.dp else 18.dp)
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, assetPath) {
        value = loadReferenceBitmap(context, assetPath)
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .background(BookingWhite, shape)
                .border(width = 1.dp, color = BookingGray, shape = shape)
        )
    }
}

private suspend fun loadReferenceBitmap(
    context: Context,
    assetPath: String?
): ImageBitmap? {
    if (assetPath.isNullOrBlank()) return null
    return withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open(assetPath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        }.getOrNull()
    }
}
