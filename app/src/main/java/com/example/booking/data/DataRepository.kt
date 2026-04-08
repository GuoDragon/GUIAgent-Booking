package com.example.booking.data

import android.content.Context
import com.example.booking.model.Airline
import com.example.booking.model.Airport
import com.example.booking.model.Attraction
import com.example.booking.model.AttractionTicket
import com.example.booking.model.BookingSignal
import com.example.booking.model.CarRental
import com.example.booking.model.Cruise
import com.example.booking.model.Flight
import com.example.booking.model.Hotel
import com.example.booking.model.HotelRoom
import com.example.booking.model.HotelReviewSignal
import com.example.booking.model.Order
import com.example.booking.model.SearchSignal
import com.example.booking.model.TaxiRoute
import com.example.booking.model.TravelCompanion
import com.example.booking.model.User
import com.example.booking.model.WishlistItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

object DataRepository {

    private const val ORDERS_FILE_NAME = "orders.json"
    private const val RUNTIME_SEARCH_SIGNALS_FILE_NAME = "runtime_search_signals.json"
    private const val RUNTIME_BOOKING_SIGNALS_FILE_NAME = "runtime_booking_signals.json"
    private const val RUNTIME_HOTEL_REVIEW_SIGNALS_FILE_NAME = "runtime_hotel_review_signals.json"

    private val gson = Gson()
    private val runtimeDataVersion = MutableStateFlow(0)
    private val writeLock = Any()

    fun initializeRuntimeFiles(context: Context) {
        ensureSeededAssetFile(context, ORDERS_FILE_NAME)
        ensureListFile(context, RUNTIME_SEARCH_SIGNALS_FILE_NAME)
        ensureListFile(context, RUNTIME_BOOKING_SIGNALS_FILE_NAME)
        ensureListFile(context, RUNTIME_HOTEL_REVIEW_SIGNALS_FILE_NAME)
    }

    fun observeRuntimeDataVersion(): StateFlow<Int> = runtimeDataVersion

    fun getOrdersFilePath(context: Context): String = appFile(context, ORDERS_FILE_NAME).absolutePath

    fun getRuntimeSearchSignalFilePath(context: Context): String {
        initializeRuntimeFiles(context)
        return appFile(context, RUNTIME_SEARCH_SIGNALS_FILE_NAME).absolutePath
    }

    fun getRuntimeBookingSignalFilePath(context: Context): String {
        initializeRuntimeFiles(context)
        return appFile(context, RUNTIME_BOOKING_SIGNALS_FILE_NAME).absolutePath
    }

    fun loadUsers(context: Context): List<User> {
        val json = loadJsonFromAssets(context, "users.json")
        return gson.fromJson(json, object : TypeToken<List<User>>() {}.type)
    }

    fun loadHotels(context: Context): List<Hotel> {
        val json = loadJsonFromAssets(context, "hotels.json")
        return gson.fromJson(json, object : TypeToken<List<Hotel>>() {}.type)
    }

    fun loadHotelRooms(context: Context): List<HotelRoom> {
        val json = loadJsonFromAssets(context, "hotel_rooms.json")
        return gson.fromJson(json, object : TypeToken<List<HotelRoom>>() {}.type)
    }

    fun loadFlights(context: Context): List<Flight> {
        val json = loadJsonFromAssets(context, "flights.json")
        return gson.fromJson(json, object : TypeToken<List<Flight>>() {}.type)
    }

    fun loadAirports(context: Context): List<Airport> {
        val json = loadJsonFromAssets(context, "airports.json")
        return gson.fromJson(json, object : TypeToken<List<Airport>>() {}.type)
    }

    fun loadAirlines(context: Context): List<Airline> {
        val json = loadJsonFromAssets(context, "airlines.json")
        return gson.fromJson(json, object : TypeToken<List<Airline>>() {}.type)
    }

    fun loadCarRentals(context: Context): List<CarRental> {
        val json = loadJsonFromAssets(context, "car_rentals.json")
        return gson.fromJson(json, object : TypeToken<List<CarRental>>() {}.type)
    }

    fun loadAttractions(context: Context): List<Attraction> {
        val json = loadJsonFromAssets(context, "attractions.json")
        return gson.fromJson(json, object : TypeToken<List<Attraction>>() {}.type)
    }

    fun loadAttractionTickets(context: Context): List<AttractionTicket> {
        val json = loadJsonFromAssets(context, "attraction_tickets.json")
        return gson.fromJson(json, object : TypeToken<List<AttractionTicket>>() {}.type)
    }

    fun loadTaxiRoutes(context: Context): List<TaxiRoute> {
        val json = loadJsonFromAssets(context, "taxi_routes.json")
        return gson.fromJson(json, object : TypeToken<List<TaxiRoute>>() {}.type)
    }

    fun loadCruises(context: Context): List<Cruise> {
        val json = loadJsonFromAssets(context, "cruises.json")
        return gson.fromJson(json, object : TypeToken<List<Cruise>>() {}.type)
    }

    fun loadOrders(context: Context): List<Order> {
        initializeRuntimeFiles(context)
        val json = readText(appFile(context, ORDERS_FILE_NAME))
        return gson.fromJson(json, object : TypeToken<List<Order>>() {}.type)
    }

    fun loadOrderById(context: Context, orderId: String): Order? {
        return loadOrders(context).firstOrNull { it.orderId == orderId }
    }

    fun loadTravelCompanions(context: Context): List<TravelCompanion> {
        val json = loadJsonFromAssets(context, "travel_companions.json")
        return gson.fromJson(json, object : TypeToken<List<TravelCompanion>>() {}.type)
    }

    fun loadWishlistItems(context: Context): List<WishlistItem> {
        val json = loadJsonFromAssets(context, "wishlist.json")
        return gson.fromJson(json, object : TypeToken<List<WishlistItem>>() {}.type)
    }

    fun loadSearchSignals(context: Context): List<SearchSignal> {
        initializeRuntimeFiles(context)
        val json = readText(appFile(context, RUNTIME_SEARCH_SIGNALS_FILE_NAME))
        return gson.fromJson(json, object : TypeToken<List<SearchSignal>>() {}.type)
    }

    fun loadBookingSignals(context: Context): List<BookingSignal> {
        initializeRuntimeFiles(context)
        val json = readText(appFile(context, RUNTIME_BOOKING_SIGNALS_FILE_NAME))
        return gson.fromJson(json, object : TypeToken<List<BookingSignal>>() {}.type)
    }

    fun loadHotelReviewSignals(context: Context): List<HotelReviewSignal> {
        initializeRuntimeFiles(context)
        val json = readText(appFile(context, RUNTIME_HOTEL_REVIEW_SIGNALS_FILE_NAME))
        return gson.fromJson(json, object : TypeToken<List<HotelReviewSignal>>() {}.type)
    }

    fun appendOrder(context: Context, order: Order) {
        synchronized(writeLock) {
            val updatedOrders = loadOrders(context).toMutableList().apply { add(order) }
            writeText(appFile(context, ORDERS_FILE_NAME), gson.toJson(updatedOrders))
            bumpRuntimeVersion()
        }
    }

    fun appendSearchSignal(context: Context, signal: SearchSignal) {
        synchronized(writeLock) {
            val updatedSignals = loadSearchSignals(context).toMutableList().apply { add(signal) }
            writeText(appFile(context, RUNTIME_SEARCH_SIGNALS_FILE_NAME), gson.toJson(updatedSignals))
            bumpRuntimeVersion()
        }
    }

    fun appendBookingSignal(context: Context, signal: BookingSignal) {
        synchronized(writeLock) {
            val updatedSignals = loadBookingSignals(context).toMutableList().apply { add(signal) }
            writeText(appFile(context, RUNTIME_BOOKING_SIGNALS_FILE_NAME), gson.toJson(updatedSignals))
            bumpRuntimeVersion()
        }
    }

    fun upsertHotelReviewSignal(context: Context, signal: HotelReviewSignal) {
        synchronized(writeLock) {
            val updatedSignals = loadHotelReviewSignals(context).toMutableList()
            val existingIndex = updatedSignals.indexOfFirst { it.orderId == signal.orderId }
            if (existingIndex >= 0) {
                updatedSignals[existingIndex] = signal
            } else {
                updatedSignals.add(signal)
            }
            writeText(appFile(context, RUNTIME_HOTEL_REVIEW_SIGNALS_FILE_NAME), gson.toJson(updatedSignals))
            bumpRuntimeVersion()
        }
    }

    private fun bumpRuntimeVersion() {
        runtimeDataVersion.value = runtimeDataVersion.value + 1
    }

    private fun ensureSeededAssetFile(context: Context, fileName: String) {
        val file = appFile(context, fileName)
        if (!file.exists()) {
            writeText(file, loadJsonFromAssets(context, fileName))
        }
    }

    private fun ensureListFile(context: Context, fileName: String) {
        val file = appFile(context, fileName)
        if (!file.exists()) {
            writeText(file, "[]")
        }
    }

    private fun appFile(context: Context, fileName: String): File {
        return File(context.applicationContext.filesDir, fileName)
    }

    private fun writeText(file: File, text: String) {
        file.parentFile?.mkdirs()
        file.writeText(text, Charsets.UTF_8)
    }

    private fun readText(file: File): String {
        return file.readText(Charsets.UTF_8)
    }

    private fun loadJsonFromAssets(context: Context, fileName: String): String {
        return context.applicationContext.assets
            .open("data/$fileName")
            .bufferedReader()
            .use { it.readText() }
    }
}
