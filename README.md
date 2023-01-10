# KmmBeacons
A Beacon scanner for KMM projects.

## Adding a dependency
Include **KmmBeacons** in your project as a dependency from GitHub Packages.

In root `build.gradle` add:
```kotlin
 repositories {
        [...]
    
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Nextome/KmmBeacons")
            credentials {
                username = GITHUB_USERNAME
                password = GITHUB_TOKEN
            }
        }
    }
```

In your module `build.gradle` add:
```kotlin
    implementation("com.nextome.kmmbeacons:kmmbeacons:$latestVersion")
```

## Basic usage
Get a scanner instance with:
```kotlin
    val scanner = KmmBeacons()
```

Then start scanning:
```kotlin
    scanner.startScan()
    scanner.observeResults().collect { results ->
        println("Found ${results.size} beacons:")
        results.forEach { 
            println("""
                UUID: ${it.uuid}
                Major: ${it.major}
                Minor: ${it.minor}
                RSSI: ${it.rssi}
            """.trimIndent())
        }
    }
```

Finally, stop scanning with:
```kotlin
    scanner.stopScan()
```

## Customization
### Regions
On Android, KmmBeacons will automatically detect all available beacons.
If you want to filter for region, you can use:
```kotlin
    scanner.setAndroidRegions(
        listOf(KScanRegion(uuid = "B39A8A15-1A82-4D24-A311-EBF122BA6AF9"))
    )
```

On iOS, it is mandatory to provide at least one region. If no regions are specified, KmmBeacons will not detect beacons.
```kotlin
    scanner.setIosRegions(
        listOf(KScanRegion(uuid = "B39A8A15-1A82-4D24-A311-EBF122BA6AF9"))
    )
```

Eventually, you can set a region on iOS only. In this way, Android will detect all beacons while iOS will return all the beacons in the given regions.

### Customize intervals
It possible to customize the intervals of the bluetooth scans. Use `setScanPeriod(millis)` and `setBetweenScanPeriod(millis)` to adjust them.

## Other libs used
On Android, KmmBeacons uses [AltBeacon](https://github.com/AltBeacon/android-beacon-library) to perform beacon scanning. On iOS, it uses [CLLocationManager](https://developer.apple.com/documentation/corelocation/cllocationmanager).

## Licence
Developed at [Nextome](https://nextome.com). Shared under Apache License 2.0.