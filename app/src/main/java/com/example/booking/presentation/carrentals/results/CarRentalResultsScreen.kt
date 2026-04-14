package com.example.booking.presentation.carrentals.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.carrentals.common.CarRentalFilterState
import com.example.booking.presentation.carrentals.common.CarRentalSortOption
import com.example.booking.presentation.stays.common.StayFilterChip
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingMapNoticeDialog
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingReferenceImage
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun CarRentalResultsScreen(
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCarClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(CarRentalResultsUiState()) }
    var showSortSheet by rememberSaveable { mutableStateOf(false) }
    var showMapDialog by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : CarRentalResultsContract.View {
            override fun showState(state: CarRentalResultsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { CarRentalResultsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(containerColor = BookingWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BookingWhite)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = BookingWhite,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.headerTitle,
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = uiState.headerSubtitle,
                                color = BookingTextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CarRentalActionChip("Sort", Icons.Filled.SwapVert, { showSortSheet = true }, modifier = Modifier.weight(1f))
                CarRentalActionChip(
                    text = if (uiState.hasActiveFilters) "Filter on" else "Filter",
                    icon = Icons.Filled.FilterList,
                    onClick = onFilterClick,
                    modifier = Modifier.weight(1f)
                )
                CarRentalActionChip(
                    text = "Map",
                    icon = Icons.Filled.Map,
                    onClick = {
                        presenter.recordMapOpened(context)
                        showMapDialog = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "${uiState.resultsCount} results",
                color = BookingTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            if (uiState.cards.isEmpty()) {
                BookingEmptyState(
                    icon = Icons.Filled.Map,
                    title = "No cars match this search",
                    description = "Try a different location or remove some filters.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(uiState.cards, key = { it.cardId }) { card ->
                        CarRentalResultCard(
                            card = card,
                            onClick = {
                                presenter.selectCar(context, card.carId)
                                onCarClick()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        CarRentalSortSheet(
            onDismissRequest = { showSortSheet = false }
        )
    }

    if (showMapDialog) {
        BookingMapNoticeDialog(
            onDismissRequest = { showMapDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarRentalSortSheet(
    onDismissRequest: () -> Unit
) {
    var uiState by remember {
        mutableStateOf(
            CarRentalSortUiState(
                selectedOption = CarRentalSortOption.Recommended,
                options = CarRentalSortOption.entries
            )
        )
    }

    val view = remember {
        object : CarRentalSortContract.View {
            override fun showState(state: CarRentalSortUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { CarRentalSortPresenter(view) }

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
fun CarRentalFilterScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(CarRentalFilterUiState()) }

    val view = remember {
        object : CarRentalFilterContract.View {
            override fun showState(state: CarRentalFilterUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { CarRentalFilterPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    var selectedLocations by remember(uiState.currentFilter.selectedLocations) {
        mutableStateOf(uiState.currentFilter.selectedLocations)
    }
    var selectedCategories by remember(uiState.currentFilter.selectedCategories) {
        mutableStateOf(uiState.currentFilter.selectedCategories)
    }
    var freeCancellationOnly by remember(uiState.currentFilter.freeCancellationOnly) {
        mutableStateOf(uiState.currentFilter.freeCancellationOnly)
    }
    var unlimitedMileageOnly by remember(uiState.currentFilter.unlimitedMileageOnly) {
        mutableStateOf(uiState.currentFilter.unlimitedMileageOnly)
    }

    Scaffold(
        containerColor = BookingWhite,
        bottomBar = {
            Surface(shadowElevation = 10.dp, color = BookingWhite) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedLocations.size + selectedCategories.size + if (freeCancellationOnly) 1 else 0 + if (unlimitedMileageOnly) 1 else 0} filters",
                        modifier = Modifier.weight(1f),
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    BookingPrimaryButton(
                        text = "Show results",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            presenter.applyFilter(
                                CarRentalFilterState(
                                    selectedLocations = selectedLocations,
                                    selectedCategories = selectedCategories,
                                    freeCancellationOnly = freeCancellationOnly,
                                    unlimitedMileageOnly = unlimitedMileageOnly
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
                        selectedLocations = emptySet()
                        selectedCategories = emptySet()
                        freeCancellationOnly = false
                        unlimitedMileageOnly = false
                    }
                )
            }

            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
            )
            FlowingChips(uiState.locationOptions, selectedLocations) { value ->
                selectedLocations = if (value in selectedLocations) selectedLocations - value else selectedLocations + value
            }

            Text(
                text = "Car Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )
            FlowingChips(uiState.categoryOptions, selectedCategories) { value ->
                selectedCategories = if (value in selectedCategories) selectedCategories - value else selectedCategories + value
            }

            Text(
                text = "Extras",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { freeCancellationOnly = !freeCancellationOnly }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Free cancellation", modifier = Modifier.weight(1f), color = BookingTextPrimary)
                Checkbox(checked = freeCancellationOnly, onCheckedChange = { freeCancellationOnly = it })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { unlimitedMileageOnly = !unlimitedMileageOnly }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Unlimited mileage", modifier = Modifier.weight(1f), color = BookingTextPrimary)
                Checkbox(checked = unlimitedMileageOnly, onCheckedChange = { unlimitedMileageOnly = it })
            }
            Box(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CarRentalDetailsScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(CarRentalDetailsUiState()) }

    val view = remember {
        object : CarRentalDetailsContract.View {
            override fun showState(state: CarRentalDetailsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { CarRentalDetailsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Car rental", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = uiState.totalLabel,
                    buttonText = "Continue to booking",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canContinue) {
            BookingEmptyState(
                icon = Icons.Filled.Map,
                title = "Select a car first",
                description = "Choose a result card to open the car-rental details page.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiState.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.subtitle,
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = uiState.location,
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = uiState.companyName,
                            color = BookingBlueLight,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BookingStatusChip(
                                text = uiState.ratingText,
                                containerColor = Color(0xFFE3F0FF),
                                contentColor = BookingBlue
                            )
                            Text(
                                text = uiState.reviewText,
                                color = BookingTextSecondary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Car details",
                            style = MaterialTheme.typography.titleLarge,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.featureLines.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEach { feature ->
                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        color = Color(0xFFF8FAFF),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text(
                                            text = feature,
                                            color = BookingTextPrimary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "What's included",
                            style = MaterialTheme.typography.titleLarge,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.includedLines.forEach { line ->
                            Text(
                                text = line,
                                color = BookingTextPrimary,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarRentalActionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = BookingWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BookingGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BookingTextPrimary)
            Text(
                text = text,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun CarRentalResultCard(
    card: CarRentalCardUiModel,
    onClick: () -> Unit
) {
    BookingRoundedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            card.tagLabels.forEach { tag ->
                BookingStatusChip(
                    text = tag,
                    containerColor = if ("Free" in tag) Color(0xFFDDF4DE) else Color(0xFFFFE2E0),
                    contentColor = if ("Free" in tag) Color(0xFF1C7C35) else Color(0xFFD11C32)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(text = card.detailLine, color = BookingTextPrimary, modifier = Modifier.padding(top = 10.dp))
                Text(text = card.transmissionLine, color = BookingTextPrimary, modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = card.locationLine,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = card.pickupNote,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = card.companyName,
                    color = BookingBlueLight,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            BookingReferenceImage(
                assetPath = card.imageAssetPath,
                modifier = Modifier.size(width = 112.dp, height = 86.dp),
                compact = true,
                contentDescription = card.title
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BookingStatusChip(
                    text = card.ratingText,
                    containerColor = Color(0xFFE3F0FF),
                    contentColor = BookingBlue
                )
                Text(
                    text = card.reviewText,
                    color = BookingTextSecondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Price for 1 day",
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = card.originalPriceText,
                    color = Color(0xFFD11C32),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = card.priceText,
                    style = MaterialTheme.typography.titleLarge,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FlowingChips(
    values: List<String>,
    selectedValues: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.chunked(3).forEach { rowValues ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowValues.forEach { value ->
                    StayFilterChip(
                        text = value,
                        selected = value in selectedValues,
                        onClick = { onToggle(value) }
                    )
                }
            }
        }
    }
}
