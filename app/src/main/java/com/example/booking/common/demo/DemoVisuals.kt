package com.example.booking.common.demo

import android.content.Context

object DemoVisuals {

    private const val StayDirectory = "reference_images/stays"
    private const val CarRentalDirectory = "reference_images/car_rentals"

    fun stayImageAssignments(
        context: Context,
        seed: String,
        cardCount: Int
    ): List<String?> = buildAssignments(context, StayDirectory, seed, cardCount)

    fun carRentalImageAssignments(
        context: Context,
        seed: String,
        cardCount: Int
    ): List<String?> = buildAssignments(context, CarRentalDirectory, seed, cardCount)

    fun stableIndex(
        key: String,
        size: Int
    ): Int {
        if (size <= 0) return 0
        return stableHash(key) % size
    }

    private fun buildAssignments(
        context: Context,
        directory: String,
        seed: String,
        cardCount: Int
    ): List<String?> {
        if (cardCount <= 0) return emptyList()

        val fileNames = context.assets
            .list(directory)
            ?.filter { fileName ->
                fileName.endsWith(".jpg", ignoreCase = true) ||
                    fileName.endsWith(".jpeg", ignoreCase = true) ||
                    fileName.endsWith(".png", ignoreCase = true)
            }
            .orEmpty()
            .sortedWith(
                compareBy<String> { stableHash("$seed:$it") }
                    .thenBy { it }
            )

        if (fileNames.isEmpty()) return List(cardCount) { null }

        val assetPaths = fileNames.map { "$directory/$it" }
        return List(cardCount) { index ->
            assetPaths.getOrNull(index)
        }
    }

    private fun stableHash(value: String): Int {
        var result = 17L
        value.forEach { character ->
            result = (result * 131L + character.code) and 0x7fffffffL
        }
        return result.toInt()
    }
}
