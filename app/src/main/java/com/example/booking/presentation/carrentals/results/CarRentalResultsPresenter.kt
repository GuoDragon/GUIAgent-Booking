package com.example.booking.presentation.carrentals.results

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.SearchSignal
import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import com.example.booking.presentation.carrentals.common.CarRentalFilterState
import com.example.booking.presentation.carrentals.common.CarRentalFlowMapper
import com.example.booking.presentation.carrentals.common.CarRentalSortOption
import com.example.booking.model.CarRental
import java.util.UUID

class CarRentalResultsPresenter(
    private val view: CarRentalResultsContract.View
) : CarRentalResultsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = CarRentalDraftStore.snapshot()
        val cars = CarRentalFlowMapper.sortCars(
            CarRentalFlowMapper.filterCars(DataRepository.loadCarRentals(context), draft),
            draft.sortOption
        )
        val cards = expandCards(cars)

        view.showState(
            CarRentalResultsUiState(
                headerTitle = draft.pickupLocation,
                headerSubtitle = "${BookingFormatters.formatLocalDateTime(draft.pickupDateTime)} - ${BookingFormatters.formatLocalDateTime(draft.returnDateTime)}",
                resultsCount = cards.size,
                cards = cards,
                hasActiveFilters = draft.filterState != CarRentalFilterState()
            )
        )
    }

    override fun recordMapOpened(context: Context) {
        val draft = CarRentalDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "car_rental_map_${UUID.randomUUID()}",
                searchType = "CAR_RENTAL_MAP_OPENED",
                destination = draft.pickupLocation,
                checkInDate = BookingFormatters.localDateTimeToEpochMillis(draft.pickupDateTime),
                checkOutDate = BookingFormatters.localDateTimeToEpochMillis(draft.returnDateTime),
                guestCount = 1,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    private fun expandCards(cars: List<CarRental>): List<CarRentalCardUiModel> {
        if (cars.isEmpty()) return emptyList()
        val targetCount = maxOf(6, cars.size * 2)
        return List(targetCount) { index ->
            carToCard(cars[index % cars.size], index)
        }
    }

    private fun carToCard(car: CarRental, index: Int): CarRentalCardUiModel {
        return CarRentalCardUiModel(
            cardId = "${car.carId}_$index",
            carId = car.carId,
            title = "${car.carModel} or similar ${car.category.lowercase()}",
            detailLine = "${car.doors} doors | ${car.seats} seats",
            transmissionLine = "${car.transmission} | ${if (car.unlimitedMileage) "Unlimited km" else "Limited mileage"}",
            locationLine = car.pickupLocation,
            companyName = car.companyName,
            ratingText = String.format("%.1f", CarRentalFlowMapper.reviewScore(car)),
            reviewText = "${CarRentalFlowMapper.reviewCount(car)} reviews",
            priceText = BookingFormatters.formatCurrency(car.pricePerDay, car.currency),
            originalPriceText = BookingFormatters.formatCurrency(CarRentalFlowMapper.originalPrice(car), car.currency),
            tagLabels = buildList {
                add("12% discount applied")
                if (car.freeCancellation) add("Free cancellation")
            }
        )
    }
}

class CarRentalSortPresenter(
    private val view: CarRentalSortContract.View
) : CarRentalSortContract.Presenter {

    override fun loadData() {
        val draft = CarRentalDraftStore.snapshot()
        view.showState(
            CarRentalSortUiState(
                selectedOption = draft.sortOption,
                options = CarRentalSortOption.entries
            )
        )
    }

    override fun applySort(option: CarRentalSortOption) {
        CarRentalDraftStore.update { draft -> draft.copy(sortOption = option) }
    }
}

class CarRentalFilterPresenter(
    private val view: CarRentalFilterContract.View
) : CarRentalFilterContract.Presenter {

    override fun loadData(context: Context) {
        val draft = CarRentalDraftStore.snapshot()
        val cars = DataRepository.loadCarRentals(context)
        view.showState(
            CarRentalFilterUiState(
                locationOptions = CarRentalFlowMapper.locationOptions(cars),
                categoryOptions = CarRentalFlowMapper.categoryOptions(cars),
                maxPricePerDay = cars.maxOfOrNull { it.pricePerDay } ?: 0.0,
                currentFilter = draft.filterState
            )
        )
    }

    override fun applyFilter(filterState: CarRentalFilterState) {
        CarRentalDraftStore.update { draft -> draft.copy(filterState = filterState) }
    }
}

class CarRentalDetailsPresenter(
    private val view: CarRentalDetailsContract.View
) : CarRentalDetailsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = CarRentalDraftStore.snapshot()
        val car = DataRepository.loadCarRentals(context).firstOrNull { it.carId == draft.selectedCarId }
        if (car == null) {
            view.showState(CarRentalDetailsUiState())
            return
        }
        val rentalDays = CarRentalFlowMapper.rentalDays(draft)
        val total = car.pricePerDay * rentalDays
        view.showState(
            CarRentalDetailsUiState(
                title = car.carModel,
                subtitle = "or similar ${car.category.lowercase()}",
                location = car.pickupLocation,
                companyName = car.companyName,
                ratingText = String.format("%.1f", CarRentalFlowMapper.reviewScore(car)),
                reviewText = "${CarRentalFlowMapper.reviewCount(car)} reviews",
                featureLines = listOf(
                    car.fuelType,
                    car.transmission,
                    "${car.seats} seats",
                    "${car.doors} doors",
                    if (car.unlimitedMileage) "Unlimited km" else "Limited mileage"
                ),
                includedLines = buildList {
                    if (car.freeCancellation) add("Free cancellation up to 48 hours before pick-up")
                    add("Collision Damage Waiver (CDW)")
                    add("Theft Cover")
                },
                priceText = BookingFormatters.formatCurrency(total, car.currency),
                totalLabel = "Total rental price",
                canContinue = true
            )
        )
    }
}
