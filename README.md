# Load Monitoring Android Client

An Android application for finding train journeys and viewing current passenger-load information. It is the presentation client in a wider load-monitoring system: this repository handles user input, REST API access, application state, and visualizing train and carriage occupancy. It does not contain data collection, occupancy calculation, or server-side code.

## Repository overview

This is a single-module Android application (`:app`) written in Kotlin and built with XML layouts. `MainActivity` hosts three Fragments:

- **Train search** loads station suggestions and lets the user choose origin, destination, date, and time.
- **Train list** requests matching journeys and displays route, time, platform, and color-coded overall occupancy information.
- **Train details** displays carriages horizontally, supports pull-to-refresh, and opens a dialog with occupancy, capacity, last-update, camera-count, and IR-count values.

Each screen observes state exposed by an AndroidX `ViewModel`. ViewModels call a shared `TrainRepository`, `RemoteTrainRepository` invokes Retrofit endpoints and converts network DTOs into UI-facing models. `RepositoryProvider` supplies the repository without a dependency-injection framework.

## Screenshots


| Search screen | Trains list  | Train details | carriage details |
|---------------|--------------|--------------|------------------|
| ![App Screenshot](images/Search.jpeg)  | ![App Screenshot](images/TrainsList.jpeg) | ![App Screenshot](images/TrainDetails.jpeg) | ![App Screenshot](images/CarriageDetails.jpeg)     |




## Main technologies

- **Kotlin 2.2.21** and **Java 11 bytecode target**
- **Android SDK**: minimum API 26, target API 35, compile API 36
- **Android Views** with XML layouts, Fragments, AppCompat, Material Components, ConstraintLayout, and RecyclerView
- **AndroidX Lifecycle ViewModel**, Kotlin coroutines, and `StateFlow`
- **Retrofit 3** with Gson conversion
- **OkHttp 5** with an HTTP body logging interceptor
- **SwipeRefreshLayout** for manual train-detail refresh
- **Gradle 8.13**, Android Gradle Plugin 8.13.2, Kotlin DSL, and a version catalog



## Main data flow

1. A Fragment forwards user actions to its ViewModel.
2. The ViewModel launches a coroutine and calls the `TrainRepository` abstraction.
3. `RemoteTrainRepository` uses `PassengerApi` to request stations, journeys, train details, or a carriage occupancy log.
4. Converter classes map response DTOs to `StationModel`, `TrainModel`, `CarriageModel`, and `OccupancyLogModel`.
5. The ViewModel publishes the result through `StateFlow`; the active Fragment collects it and updates RecyclerView adapters and other Views.

Updates are request-driven. Train details can be refreshed manually; there is no continuous stream or polling service in this client.

## Setup and execution



### Requirements

- Android Studio compatible with Android Gradle Plugin 8.13.2 and Kotlin 2.2.21
- JDK 17 for the Android Gradle Plugin (the app itself targets Java 11 bytecode)
- Android SDK Platform 36 and the associated build tools
- An emulator or physical device running Android 8.0 (API 26) or newer



### Install and build

```text
git clone https://github.com/Tomerlevy104/Load_Monitoring_Client
cd Load_Monitoring_Client
```

Open the root directory in Android Studio and allow Gradle to sync.

Build or install a debug APK:

```text
# macOS/Linux
./gradlew assembleDebug
./gradlew installDebug

# Windows PowerShell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

`installDebug` requires a running emulator or connected device.

### API configuration

The intended shared configuration uses a `.env` file in the project root. Create the file locally with:

```text
BASE_URL=http://<host>:<port>/
```

Gradle reads this value and generates `BuildConfig.BASE_URL`. The `.env` file is ignored by Git and must not contain credentials intended for version control. Retrofit base URLs must end with `/`.



The manifest grants only `android.permission.INTERNET`. No Bluetooth, location, camera, or other hardware permission is requested. Cleartext HTTP traffic is currently enabled.

## Important code references

- `[RemoteTrainRepository](app/src/main/java/com/finalproject/load_monitoring/repositories/RemoteTrainRepository.kt)` — Centralizes the client's REST calls and maps transport DTOs into application models used by the UI.
- `[TrainDetailsViewModel](app/src/main/java/com/finalproject/load_monitoring/ui/traindetails/TrainDetailsViewModel.kt)` — Loads train and carriage-log details and exposes both results as `StateFlow` for the details screen.



## Repository structure

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/com/finalproject/load_monitoring/
│       │   │   ├── di/             # Repository provider
│       │   │   ├── dto/            # Network response types
│       │   │   ├── models/         # UI-facing data models
│       │   │   ├── network/        # Retrofit client and API contract
│       │   │   ├── repositories/   # Data-access abstraction and implementation
│       │   │   ├── ui/             # Search, list, and details screens
│       │   │   └── utils/          # Date and DTO/model converters
│       │   ├── res/                 # XML layouts, themes, strings, and drawables
│       │   └── AndroidManifest.xml
│       ├── test/                    # Local JVM tests
│       └── androidTest/             # On-device instrumentation tests
├── gradle/libs.versions.toml        # Dependency and plugin versions
├── build.gradle.kts
└── settings.gradle.kts
│       └── androidTest/             # On-device instrumentation tests
├── gradle/libs.versions.toml        # Dependency and plugin versions
├── build.gradle.kts
└── settings.gradle.kts

```


