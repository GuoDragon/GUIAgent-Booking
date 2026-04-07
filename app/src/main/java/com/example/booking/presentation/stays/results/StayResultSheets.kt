package com.example.booking.presentation.stays.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.common.format.BookingFormatters
import com.example.booking.presentation.stays.common.StayFilterChip
import com.example.booking.presentation.stays.common.StayFilterState
import com.example.booking.presentation.stays.common.StaySortOption
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaySortSheet(
    onDismissRequest: () -> Unit
) {
    var uiState by remember {
        mutableStateOf(
            StaySortUiState(
                selectedOption = StaySortOption.TopPicks,
                options = StaySortOption.entries
            )
        )
    }

    val view = remember {
        object : StaySortContract.View {
            override fun showState(state: StaySortUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { StaySortPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 24.dp)) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            uiState.options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.applySort(option)
                            onDismissRequest()
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.title,
                        modifier = Modifier.weight(1f),
                        color = BookingTextPrimary
                    )
                    RadioButton(
                        selected = option == uiState.selectedOption,
                        onClick = {
                            presenter.applySort(option)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StayFilterScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(StayFilterUiState()) }

    val view = remember {
        object : StayFilterContract.View {
            override fun showState(state: StayFilterUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { StayFilterPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    var selectedBudgetRange by remember(uiState.minimumBudget, uiState.maximumBudget, uiState.currentFilter) {
        mutableStateOf(
            (uiState.currentFilter.minBudget?.toFloat() ?: uiState.minimumBudget)..(
                uiState.currentFilter.maxBudget?.toFloat() ?: uiState.maximumBudget
            )
        )
    }
    var selectedStarRatings by remember(uiState.currentFilter.selectedStarRatings) {
        mutableStateOf(uiState.currentFilter.selectedStarRatings)
    }
    var selectedReviewScore by remember(uiState.currentFilter.minimumReviewScore) {
        mutableStateOf(uiState.currentFilter.minimumReviewScore)
    }
    var selectedAmenities by remember(uiState.currentFilter.selectedAmenities) {
        mutableStateOf(uiState.currentFilter.selectedAmenities)
    }
    var selectedBrands by remember(uiState.currentFilter.selectedBrands) {
        mutableStateOf(uiState.currentFilter.selectedBrands)
    }
    var accessibleByElevator by remember(uiState.currentFilter.accessibleByElevator) {
        mutableStateOf(uiState.currentFilter.accessibleByElevator)
    }

    val previewCount = remember(
        uiState.hotels,
        selectedBudgetRange,
        selectedStarRatings,
        selectedReviewScore,
        selectedAmenities,
        selectedBrands,
        accessibleByElevator
    ) {
        uiState.hotels.count { hotel ->
            val priceMatch = hotel.pricePerNight in selectedBudgetRange.start.toDouble()..selectedBudgetRange.endInclusive.toDouble()
            val starMatch = selectedStarRatings.isEmpty() || hotel.starRating in selectedStarRatings
            val reviewMatch = selectedReviewScore == null || hotel.rating >= selectedReviewScore!!
            val amenityMatch = selectedAmenities.isEmpty() || selectedAmenities.all { it in hotel.amenities }
            val brandMatch = selectedBrands.isEmpty() || hotel.brand in selectedBrands
            val accessibilityMatch = !accessibleByElevator || hotel.amenities.any { amenity -> amenity.contains("Elevator", ignoreCase = true) || amenity.contains("Lift", ignoreCase = true) }
            priceMatch && starMatch && reviewMatch && amenityMatch && brandMatch && accessibilityMatch
        }
    }

    Scaffold(
        containerColor = BookingWhite,
        bottomBar = {
            Surface(shadowElevation = 10.dp, color = BookingWhite) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "$previewCount matching properties",
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    BookingPrimaryButton(
                        text = "Show results",
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            presenter.applyFilter(
                                StayFilterState(
                                    minBudget = if (selectedBudgetRange.start <= uiState.minimumBudget) null else selectedBudgetRange.start.toDouble(),
                                    maxBudget = if (selectedBudgetRange.endInclusive >= uiState.maximumBudget) null else selectedBudgetRange.endInclusive.toDouble(),
                                    selectedStarRatings = selectedStarRatings,
                                    minimumReviewScore = selectedReviewScore,
                                    selectedAmenities = selectedAmenities,
                                    selectedBrands = selectedBrands,
                                    accessibleByElevator = accessibleByElevator
                                )
                            )
                            onApplyClick()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
                Text(
                    text = "Filter by",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reset",
                    color = BookingBlueLight,
                    modifier = Modifier.clickable {
                        selectedBudgetRange = uiState.minimumBudget..uiState.maximumBudget
                        selectedStarRatings = emptySet()
                        selectedReviewScore = null
                        selectedAmenities = emptySet()
                        selectedBrands = emptySet()
                        accessibleByElevator = false
                    }
                )
            }

            FilterSectionTitle("Your budget (for 1 night)")
            Text(
                text = "${BookingFormatters.formatCurrency(selectedBudgetRange.start.toDouble(), "USD")} - ${
                    BookingFormatters.formatCurrency(selectedBudgetRange.endInclusive.toDouble(), "USD")
                }",
                color = BookingTextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
            )
            RangeSlider(
                value = selectedBudgetRange,
                onValueChange = { selectedBudgetRange = it },
                valueRange = uiState.minimumBudget..uiState.maximumBudget
            )

            FilterSectionTitle("Popular filters")
            FilterCheckboxRow(
                label = "Very good: 8+",
                checked = selectedReviewScore == 8.0,
                onCheckedChange = { checked -> selectedReviewScore = if (checked) 8.0 else null }
            )
            uiState.amenityOptions.take(5).forEach { amenity ->
                FilterCheckboxRow(
                    label = amenity,
                    checked = amenity in selectedAmenities,
                    onCheckedChange = { checked ->
                        selectedAmenities = if (checked) selectedAmenities + amenity else selectedAmenities - amenity
                    }
                )
            }

            FilterSectionTitle("Property rating")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 4, 5).forEach { stars ->
                    StayFilterChip(
                        text = "$stars stars",
                        selected = stars in selectedStarRatings,
                        onClick = {
                            selectedStarRatings = if (stars in selectedStarRatings) {
                                selectedStarRatings - stars
                            } else {
                                selectedStarRatings + stars
                            }
                        }
                    )
                }
            }

            FilterSectionTitle("Brands")
            uiState.brandOptions.forEach { brand ->
                FilterCheckboxRow(
                    label = brand,
                    checked = brand in selectedBrands,
                    onCheckedChange = { checked ->
                        selectedBrands = if (checked) selectedBrands + brand else selectedBrands - brand
                    }
                )
            }

            FilterSectionTitle("Room accessibility")
            FilterCheckboxRow(
                label = "Upper floors accessible by elevator",
                checked = accessibleByElevator,
                onCheckedChange = { accessibleByElevator = it }
            )

            Box(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
    )
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = BookingTextPrimary
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

