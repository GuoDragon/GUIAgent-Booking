# Booking

Compose-based Booking UI prototype built around the local screenshot set and JSON assets in this repo.

## Current page coverage

- Search main page
- Flights main page
- Flight + Hotel main page and combined hub page
- Car rentals main page
- Stays destination input page
- Stays date picker page
- Stays room and guest selection sheet
- Stays search results page
- Stays sort sheet
- Stays filter sheet
- Stays property details page
- Stays room type page
- Stays booking personal info page
- Stays booking overview page
- Stays booking success page
- Flights date picker page
- Flights search results page
- Flights sort sheet
- Flights filter sheet
- Flight details page
- Choose flight fare page
- Flight luggage page
- Flight meal choice page
- Flight custom preferences page
- Flight traveler details page
- Flight traveler contact page
- Flight booking success page
- Car rentals date picker page
- Car rentals search results page
- Car rentals sort sheet
- Car rentals filter sheet
- Car rental details page
- Car rental booking summary page
- Car rental booking success page
- Saved page
- Trips page with `Active`, `History`, and `Cancelled` tabs
- Account page
- Personal information page
- Travel companions page
- Add traveler page

## Implementation notes

- UI text is English-only.
- Navigation is hosted from `app/src/main/java/com/example/booking/navigation`.
- Each feature follows a lightweight MVP split under `app/src/main/java/com/example/booking/presentation/<feature>` with `Contract`, `Presenter`, and `Screen` files.
- Shared cards, top bars, buttons, and list rows live in `app/src/main/java/com/example/booking/ui/components`.
- The Search tab is now a multi-product hub for `Stays`, `Flights`, `Flight + Hotel`, and `Car rental`.
- The `Flight + Hotel` search first shows a combined hub, then branches into the reused dedicated Flights and Stays result flows.
- All page canvases now use white backgrounds; blue is reserved for accents such as top bars, primary buttons, selected chips, and highlight cards.
- Stay and car-rental result cards now pull packaged reference photos from `app/src/main/assets/reference_images`; if the demo list is longer than the available images, the extra cards intentionally fall back to blank image tiles.
- The Search home opens the Stays room-and-guests selector as a bottom sheet, and the `Sort` entry points on result pages now open bottom sheets instead of separate pages.
- The old standalone Guests and Sort routes have been removed from navigation; those interactions are now sheet-only entry points.
- Screenshot structure is the visual guide, but rendered content prefers the current local JSON data from `app/src/main/assets/data`.

## Interaction rules in this round

- The map action on stay results does not open a new page; it writes a detectable local search signal instead.
- The map actions on flight and car-rental results follow the same rule and only write detectable local search signals.
- The date pickers let the user restart a range by tapping the current start date again, and the Stays date flexibility chips now scroll horizontally in one row.
- The stay booking flow is wired end to end: search -> results -> details -> room type -> personal info -> booking overview -> booking success.
- The flight booking flow is wired end to end: search -> date -> results -> details -> fare -> luggage -> meal choice -> custom preferences -> traveler details -> traveler contact -> booking success.
- The car-rental flow is wired end to end: search -> date -> results -> details -> booking summary -> booking success.
- Stay, flight, and car-rental result pages intentionally expand the local data into denser demo lists so the same search can show multiple rows.
- Fields that do not have dedicated in-scope picker pages stay inline on the search cards instead of opening extra placeholder pages.
- Other rows and toolbar buttons that are outside the requested flow remain visual-only placeholders.
- The personal info and booking overview steps use real Compose text fields so the system keyboard is shown when the user types.

## Data behavior

- Asset-backed reference data still comes from `app/src/main/assets/data`, including hotels, hotel rooms, users, and the seeded order list.
- Runtime mutable files are initialized in app storage on launch:
  - `orders.json`
  - `runtime_search_signals.json`
  - `runtime_booking_signals.json`
- Stay search submission writes `STAY_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Flight search submission writes `FLIGHT_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Flight + Hotel search submission writes `FLIGHT_HOTEL_SEARCH_SUBMITTED` into `runtime_search_signals.json` and syncs the combined draft into the dedicated Flights and Stays flows.
- Car-rental search submission writes `CAR_RENTAL_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Stay results `Map` writes `STAY_MAP_OPENED` into `runtime_search_signals.json`.
- Flight results `Map` writes `FLIGHT_MAP_OPENED` into `runtime_search_signals.json`.
- Car-rental results `Map` writes `CAR_RENTAL_MAP_OPENED` into `runtime_search_signals.json`.
- The three implemented result pages also show a lightweight confirmation dialog after `Map` is tapped so the user gets visual feedback without leaving the current screen.
- Completing a stay booking appends a new `STAY` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Completing a flight booking appends a new `FLIGHT` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Completing a car-rental booking appends a new `CAR_RENTAL` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Trips now observes the runtime data version so newly created stay orders appear without restarting the app.
- The same runtime refresh path is reused for newly created flight and car-rental orders.
- Saved lists are grouped from `wishlist.json` using safe fallbacks when referenced content cannot be resolved directly.
- Personal and account data come from `users.json`.
- Travel companions come from `travel_companions.json`.
- Demo-expanded result rows now keep stable IDs while varying supporting labels so repeated cards no longer look completely identical.

## Build note

- Kotlin compilation was rechecked successfully with `./gradlew.bat :app:compileDebugKotlin --no-daemon`.
- If a fresh machine does not already have the required Android and Gradle artifacts cached, Android Studio may still need one initial dependency download outside this repo.
