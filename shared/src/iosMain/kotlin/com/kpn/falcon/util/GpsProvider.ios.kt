package com.kpn.falcon.util

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.Foundation.*
import kotlin.coroutines.resume

actual class GpsProvider {

    private val locationManager = CLLocationManager()

    actual suspend fun getCurrentLocation(): GpsCoordinates? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    locationManager.delegate = null
                    locationManager.stopUpdatingLocation()
                    if (location != null) {
                        continuation.resume(GpsCoordinates(
                            lat = location.coordinate.useContents { latitude },
                            lng = location.coordinate.useContents { longitude }
                        ))
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    locationManager.delegate = null
                    continuation.resume(null)
                }
            }
            locationManager.delegate = delegate
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.startUpdatingLocation()

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
            }
        }
    }

    actual fun hasLocationPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways
    }
}
