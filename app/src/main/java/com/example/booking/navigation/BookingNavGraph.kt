package com.example.booking.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.booking.presentation.account.AccountScreen
import com.example.booking.presentation.addcompanion.AddTravelCompanionScreen
import com.example.booking.presentation.carrentals.booking.CarRentalBookingSuccessScreen
import com.example.booking.presentation.carrentals.booking.CarRentalBookingSummaryScreen
import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import com.example.booking.presentation.carrentals.results.CarRentalDetailsScreen
import com.example.booking.presentation.carrentals.results.CarRentalFilterScreen
import com.example.booking.presentation.carrentals.results.CarRentalResultsScreen
import com.example.booking.presentation.carrentals.search.CarRentalDateScreen
import com.example.booking.presentation.flightplushotel.FlightPlusHotelHubScreen
import com.example.booking.presentation.flights.booking.FlightBookingSuccessScreen
import com.example.booking.presentation.flights.booking.FlightFareScreen
import com.example.booking.presentation.flights.booking.FlightLuggageScreen
import com.example.booking.presentation.flights.booking.FlightMealChoiceScreen
import com.example.booking.presentation.flights.booking.FlightSeatSelectionScreen
import com.example.booking.presentation.flights.booking.FlightTravelerContactScreen
import com.example.booking.presentation.flights.booking.FlightTravelerDetailsScreen
import com.example.booking.presentation.flights.common.FlightDraftStore
import com.example.booking.presentation.flights.results.FlightDetailsScreen
import com.example.booking.presentation.flights.results.FlightFilterScreen
import com.example.booking.presentation.flights.results.FlightResultsScreen
import com.example.booking.presentation.flights.search.FlightDateScreen
import com.example.booking.presentation.orders.OrdersScreen
import com.example.booking.presentation.personalinfo.PersonalInfoScreen
import com.example.booking.presentation.saved.SavedScreen
import com.example.booking.presentation.search.SearchScreen
import com.example.booking.presentation.stays.booking.StayBookingOverviewScreen
import com.example.booking.presentation.stays.booking.StayBookingSuccessScreen
import com.example.booking.presentation.stays.booking.StayPersonalInfoScreen
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.details.StayDetailsScreen
import com.example.booking.presentation.stays.details.StayRoomTypeScreen
import com.example.booking.presentation.stays.input.StayDateScreen
import com.example.booking.presentation.stays.input.StayDestinationScreen
import com.example.booking.presentation.stays.results.StayFilterScreen
import com.example.booking.presentation.stays.results.StayResultsScreen
import com.example.booking.presentation.travelcompanions.TravelCompanionsScreen

@Composable
fun BookingNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BookingRoutes.Search,
        modifier = modifier
    ) {
        composable(BookingRoutes.Search) {
            SearchScreen(
                onStayDestinationClick = { navController.navigate(BookingRoutes.StayDestination) },
                onStayDateClick = { navController.navigate(BookingRoutes.StayDate) },
                onStaySearchClick = { navController.navigate(BookingRoutes.StayResults) },
                onFlightDateClick = { navController.navigate(BookingRoutes.FlightDate) },
                onFlightSearchClick = { navController.navigate(BookingRoutes.FlightResults) },
                onFlightHotelSearchClick = { navController.navigate(BookingRoutes.FlightPlusHotelHub) },
                onCarDateClick = { navController.navigate(BookingRoutes.CarRentalDate) },
                onCarSearchClick = { navController.navigate(BookingRoutes.CarRentalResults) }
            )
        }
        composable(BookingRoutes.Saved) { SavedScreen() }
        composable(BookingRoutes.Orders) { OrdersScreen() }
        composable(BookingRoutes.Account) {
            AccountScreen(
                onPersonalInfoClick = { navController.navigate(BookingRoutes.PersonalInfo) },
                onTravelCompanionsClick = { navController.navigate(BookingRoutes.TravelCompanions) }
            )
        }
        composable(BookingRoutes.PersonalInfo) {
            PersonalInfoScreen(onBackClick = { navController.popBackStack() })
        }
        composable(BookingRoutes.TravelCompanions) {
            TravelCompanionsScreen(
                onBackClick = { navController.popBackStack() },
                onAddTravelCompanionClick = { navController.navigate(BookingRoutes.AddTravelCompanion) }
            )
        }
        composable(BookingRoutes.AddTravelCompanion) {
            AddTravelCompanionScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.StayDestination) {
            StayDestinationScreen(onBackClick = { navController.popBackStack() })
        }
        composable(BookingRoutes.StayDate) {
            StayDateScreen(
                onBackClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.StayResults) {
            StayResultsScreen(
                onBackClick = { navController.popBackStack() },
                onFilterClick = { navController.navigate(BookingRoutes.StayFilter) },
                onHotelClick = { hotelId ->
                    StayDraftStore.selectHotel(hotelId)
                    navController.navigate(BookingRoutes.StayDetails)
                }
            )
        }
        composable(BookingRoutes.StayFilter) {
            StayFilterScreen(
                onBackClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.StayDetails) {
            StayDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onSelectRoomsClick = { navController.navigate(BookingRoutes.StayRoomType) }
            )
        }
        composable(BookingRoutes.StayRoomType) {
            StayRoomTypeScreen(
                onBackClick = { navController.popBackStack() },
                onRoomSelected = { roomId ->
                    StayDraftStore.selectRoom(roomId)
                    navController.navigate(BookingRoutes.StayPersonalInfo)
                }
            )
        }
        composable(BookingRoutes.StayPersonalInfo) {
            StayPersonalInfoScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { navController.navigate(BookingRoutes.StayBookingOverview) }
            )
        }
        composable(BookingRoutes.StayBookingOverview) {
            StayBookingOverviewScreen(
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { orderId ->
                    navController.navigate(BookingRoutes.stayBookingSuccess(orderId)) {
                        popUpTo(BookingRoutes.Search) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = BookingRoutes.StayBookingSuccessWithOrderId,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            StayBookingSuccessScreen(
                orderId = backStackEntry.arguments?.getString("orderId").orEmpty(),
                onBackClick = { navController.popBackStack() },
                onViewTripsClick = {
                    navController.navigate(BookingRoutes.Orders) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSearchAgainClick = {
                    navController.navigate(BookingRoutes.Search) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(BookingRoutes.FlightDate) {
            FlightDateScreen(
                onBackClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.FlightResults) {
            FlightResultsScreen(
                onBackClick = { navController.popBackStack() },
                onFilterClick = { navController.navigate(BookingRoutes.FlightFilter) },
                onFlightClick = { outboundId, returnId ->
                    FlightDraftStore.selectItinerary(outboundId, returnId)
                    navController.navigate(BookingRoutes.FlightDetails)
                }
            )
        }
        composable(BookingRoutes.FlightFilter) {
            FlightFilterScreen(
                onBackClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.FlightDetails) {
            FlightDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(BookingRoutes.FlightFare) }
            )
        }
        composable(BookingRoutes.FlightFare) {
            FlightFareScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(BookingRoutes.FlightLuggage) }
            )
        }
        composable(BookingRoutes.FlightLuggage) {
            FlightLuggageScreen(
                onBackClick = { navController.popBackStack() },
                onMealChoiceClick = { navController.navigate(BookingRoutes.FlightMealChoice) },
                onContinueClick = { navController.navigate(BookingRoutes.FlightCustomPreferences) }
            )
        }
        composable(BookingRoutes.FlightMealChoice) {
            FlightMealChoiceScreen(onBackClick = { navController.popBackStack() })
        }
        composable(BookingRoutes.FlightCustomPreferences) {
            FlightSeatSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(BookingRoutes.FlightTravelerDetails) }
            )
        }
        composable(BookingRoutes.FlightTravelerDetails) {
            FlightTravelerDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onDoneClick = { navController.navigate(BookingRoutes.FlightTravelerContact) }
            )
        }
        composable(BookingRoutes.FlightTravelerContact) {
            FlightTravelerContactScreen(
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { orderId ->
                    navController.navigate(BookingRoutes.flightBookingSuccess(orderId)) {
                        popUpTo(BookingRoutes.Search) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = BookingRoutes.FlightBookingSuccessWithOrderId,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            FlightBookingSuccessScreen(
                orderId = backStackEntry.arguments?.getString("orderId").orEmpty(),
                onBackClick = { navController.popBackStack() },
                onViewTripsClick = {
                    navController.navigate(BookingRoutes.Orders) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSearchAgainClick = {
                    navController.navigate(BookingRoutes.Search) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(BookingRoutes.FlightPlusHotelHub) {
            FlightPlusHotelHubScreen(
                onBackClick = { navController.popBackStack() },
                onViewFlightsClick = { navController.navigate(BookingRoutes.FlightResults) },
                onViewStaysClick = { navController.navigate(BookingRoutes.StayResults) }
            )
        }

        composable(BookingRoutes.CarRentalDate) {
            CarRentalDateScreen(
                onBackClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.CarRentalResults) {
            CarRentalResultsScreen(
                onBackClick = { navController.popBackStack() },
                onFilterClick = { navController.navigate(BookingRoutes.CarRentalFilter) },
                onCarClick = { carId ->
                    CarRentalDraftStore.selectCar(carId)
                    navController.navigate(BookingRoutes.CarRentalDetails)
                }
            )
        }
        composable(BookingRoutes.CarRentalFilter) {
            CarRentalFilterScreen(
                onBackClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }
        composable(BookingRoutes.CarRentalDetails) {
            CarRentalDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(BookingRoutes.CarRentalBookingSummary) }
            )
        }
        composable(BookingRoutes.CarRentalBookingSummary) {
            CarRentalBookingSummaryScreen(
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { orderId ->
                    navController.navigate(BookingRoutes.carRentalBookingSuccess(orderId)) {
                        popUpTo(BookingRoutes.Search) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = BookingRoutes.CarRentalBookingSuccessWithOrderId,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            CarRentalBookingSuccessScreen(
                orderId = backStackEntry.arguments?.getString("orderId").orEmpty(),
                onBackClick = { navController.popBackStack() },
                onViewTripsClick = {
                    navController.navigate(BookingRoutes.Orders) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSearchAgainClick = {
                    navController.navigate(BookingRoutes.Search) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
