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
- Taxi tab on the Search page
- Taxi pickup location page
- Taxi destination page
- Taxi time page
- Taxi passengers page
- Taxi search results page
- Taxi add flight tracking page
- Taxi choose flight page
- Taxi choose airlines flight sheet
- Taxi choose flight-times sheet
- Taxi contact details page
- Taxi booking overview page
- Taxi booking success page
- Attractions tab on the Search page
- Attractions destination page
- Attractions date picker page
- Attractions search results page
- Attraction preview page
- Attraction details page
- Available tickets page
- Attraction ticket details page
- Attraction personal info page
- Attraction payment page
- Attraction booking success page
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
- The Search tab is now a multi-product hub for `Stays`, `Flights`, `Flight + Hotel`, `Car rental`, `Taxi`, and `Attractions`.
- The `Flight + Hotel` search first shows a combined hub, then branches into the reused dedicated Flights and Stays result flows.
- All page canvases now use white backgrounds; blue is reserved for accents such as top bars, primary buttons, selected chips, and highlight cards.
- Taxi search now keeps input editing on the search card itself: pick-up/destination opens the same route-planner style overlay, time is chosen in calendar bottom sheets, and passengers are adjusted in a dedicated passenger bottom sheet.
- Stay and car-rental result cards now pull packaged reference photos from `app/src/main/assets/reference_images`; if the demo list is longer than the available images, the extra cards intentionally fall back to blank image tiles.
- Attractions result cards now load local reference photos from `app/src/main/assets/reference_images/attractions` (sourced from `image/Attractions`), with the same safe blank fallback when an image cannot be loaded.
- The Car Rentals search card now edits the driver's age as a direct numeric input with the system keyboard instead of cycling through preset age bands.
- Stay result cards now show an additional synthetic review score badge on a 0-10 scale for each card, calculated as the average of multiple stable virtual score samples.
- Taxi and attraction flows keep the same MVP split and reuse local JSON assets instead of adding remote dependencies or placeholder backend calls.
- The Search home opens the Stays room-and-guests selector as a bottom sheet, and the `Sort` entry points on result pages now open bottom sheets instead of separate pages.
- The old standalone Guests and Sort routes have been removed from navigation; those interactions are now sheet-only entry points.
- Screenshot structure is the visual guide, but rendered content prefers the current local JSON data from `app/src/main/assets/data`.
- MVP boundaries were tightened in this round: `Screen` and `BookingNavGraph` no longer read or mutate `DraftStore`/`DataRepository` directly; those operations are now routed through Presenter intents.
- Search, Flights booking meal selection, and result-to-detail selection flows (Stay/Flight/Car/Attraction) now use Presenter-owned selection/update methods so navigation remains UI-only.

## Interaction rules in this round

- The map action on stay results does not open a new page; it writes a detectable local search signal instead.
- The map actions on flight and car-rental results follow the same rule and only write detectable local search signals.
- The date pickers let the user restart a range by tapping the current start date again, and the Stays date flexibility chips now scroll horizontally in one row.
- The stay booking flow is wired end to end: search -> results -> details -> room type -> personal info -> booking overview -> booking success.
- From stay booking success, `Search again` now opens the stay results page directly instead of returning to the Search hub.
- All booking success pages now send `Search again` to the matching product results entry page (`StayResults`, `FlightResults`, `CarRentalResults`, `TaxiResults`, `AttractionResults`) instead of the Search hub.
- The flight booking flow is wired end to end: search -> date -> results -> details -> fare -> luggage -> meal choice -> custom preferences -> traveler details -> traveler contact -> booking success.
- The car-rental flow is wired end to end: search -> date -> results -> details -> booking summary -> booking success.
- The taxi flow is wired end to end: search tab (route planner + time/passenger sheets) -> results -> add flight tracking -> choose flight (with airline/time filters) -> contact details -> booking overview -> booking success.
- In Taxi round-trip mode, the search card now shows an extra return-time row (`Add a return time`) that opens its own scheduling sheet.
- In Trips `History`, completed stay orders now support `Write review` / `Edit review` through a bottom-sheet form (rating + comment).
- The attractions flow is wired end to end: search tab -> destination/date -> results -> preview -> details -> tickets -> ticket details -> personal info -> payment -> booking success.
- Stay, flight, and car-rental result pages intentionally expand the local data into denser demo lists so the same search can show multiple rows.
- Stay details now reuse six assigned reference images from the same stay image pool, and the room-type cards reuse that same six-image set as thumbnails.
- Stay details `Property highlights` is now rendered as a horizontally scrollable single row.
- Stay details adds multiple hotel-specific mock guest reviews under `You searched for`.
- Taxi and attraction result lists also expand the local asset data into richer demo rows while keeping selection and booking state in dedicated draft stores.
- Detail and booking screens now explicitly stretch their top summary cards to the full content width wherever the same right-edge gap issue was found.
- Stay booking overview cards now also stretch to full content width to remove right-edge gaps on rectangle sections.
- Fields that do not have dedicated in-scope picker pages stay inline on the search cards instead of opening extra placeholder pages.
- Other rows and toolbar buttons that are outside the requested flow remain visual-only placeholders.
- The personal info and booking overview steps use real Compose text fields so the system keyboard is shown when the user types.

## Data behavior

- Asset-backed reference data still comes from `app/src/main/assets/data`, including hotels, hotel rooms, flights, car rentals, taxi routes, attractions, attraction tickets, users, and the seeded order list.
- Runtime mutable files are initialized in app storage on launch:
  - `orders.json`
  - `runtime_search_signals.json`
  - `runtime_booking_signals.json`
  - `runtime_hotel_review_signals.json`
- Stay search submission writes `STAY_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Flight search submission writes `FLIGHT_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Flight + Hotel search submission writes `FLIGHT_HOTEL_SEARCH_SUBMITTED` into `runtime_search_signals.json` and syncs the combined draft into the dedicated Flights and Stays flows.
- Car-rental search submission writes `CAR_RENTAL_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Taxi search submission writes `TAXI_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Attraction search submission writes `ATTRACTION_SEARCH_SUBMITTED` into `runtime_search_signals.json`.
- Stay results `Map` writes `STAY_MAP_OPENED` into `runtime_search_signals.json`.
- Flight results `Map` writes `FLIGHT_MAP_OPENED` into `runtime_search_signals.json`.
- Car-rental results `Map` writes `CAR_RENTAL_MAP_OPENED` into `runtime_search_signals.json`.
- The three implemented result pages also show a lightweight confirmation dialog after `Map` is tapped so the user gets visual feedback without leaving the current screen.
- Completing a stay booking appends a new `STAY` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Completing a flight booking appends a new `FLIGHT` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Completing a car-rental booking appends a new `CAR_RENTAL` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Completing a taxi booking appends a new `TAXI` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Completing an attraction booking appends a new `ATTRACTION` order into the runtime `orders.json` file and appends a matching booking signal into `runtime_booking_signals.json`.
- Submitting or editing a stay review from Trips `History` upserts one hotel-review signal per stay order into `runtime_hotel_review_signals.json`.
- Trips now observes the runtime data version via `OrdersPresenter.observeRuntimeVersion()` so newly created stay orders appear without restarting the app.
- The same runtime refresh path is reused for newly created flight, car-rental, taxi, and attraction orders.
- Saved lists are grouped from `wishlist.json` using safe fallbacks when referenced content cannot be resolved directly.
- Personal and account data come from `users.json`.
- Travel companions come from `travel_companions.json`.
- Demo-expanded result rows now keep stable IDs while varying supporting labels so repeated cards no longer look completely identical.
- A structure-level MVP audit was re-run for the current implemented feature pages; Contract/Presenter/Screen triads remain in place and this round only adjusted navigation mapping.

## Build note

- Kotlin compilation was rechecked successfully with `./gradlew.bat :app:compileDebugKotlin --no-daemon`.
- If a fresh machine does not already have the required Android and Gradle artifacts cached, Android Studio may still need one initial dependency download outside this repo.
