package com.okanot.youokanot.nearbyclinicscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProvider
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions

@Composable
fun NearbyClinicsScreen(
    clinicLocations: List<ClinicLocation>, // List of clinics with latitude, longitude, and details
//    userLocation: Location // User's current location
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isUserInteractingWithMap by remember { mutableStateOf(false) }

    // Request location permissions dynamically
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    // Request permissions on first render
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val mapViewportState = rememberMapViewportState()

    // Fetch user location if permissions are granted
    if (hasLocationPermission) {
        LaunchedEffect(Unit) {
            fetchUserLocation { location ->
                if(isUserInteractingWithMap) return@fetchUserLocation
                userLocation = location
                with(mapViewportState) {
                    setCameraOptions {
//                        zoom(2.0)
                        center(Point.fromLngLat(userLocation!!.longitude, userLocation!!.latitude))
//                        pitch(0.0)
//                        bearing(0.0)
                    }
                }
            }
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            onMapClickListener = {
                isUserInteractingWithMap = true
                false
            },
            onMapLongClickListener = {
                isUserInteractingWithMap = true
                false
            },
        ){

            clinicLocations.forEach{ clinic ->
                CircleAnnotation(point = Point.fromLngLat(clinic.longitude, clinic.latitude)) {
                    // Style the circle that will be added to the map.
                    circleRadius = 8.0
                    circleColor = Color(0xffee4e8b)
                    circleStrokeWidth = 2.0
                    circleStrokeColor = Color(0xffffffff)
                }
            }

            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    enabled = true
                    pulsingEnabled = true
                    puckBearingEnabled = true
                }
                mapViewportState.transitionToFollowPuckState(
                    followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                        .bearing(FollowPuckViewportStateBearing.Constant(0.0))
                        .padding(EdgeInsets(200.0 * 1, 0.0, 0.0, 0.0))
                        .build(),
                ) { success ->
                    // the transition has been completed with a flag indicating whether the transition succeeded
                    println()
                }
        }
        }
    }
}


@SuppressLint("MissingPermission")
private fun fetchUserLocation(onLocationFetched: (Location) -> Unit) {

    val locationService : LocationService = LocationServiceFactory.getOrCreate()
    var locationProvider: DeviceLocationProvider? = null

    val request = LocationProviderRequest.Builder()
        .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
        .displacement(0F)
        .accuracy(AccuracyLevel.HIGHEST)
        .build()

    val result = locationService.getDeviceLocationProvider(request)
    if (result.isValue) {
        locationProvider = result.value!!
    } else {
        Log.e(TAG, "Failed to get device location provider")
    }
    val locationObserver = LocationObserver { locations ->
        Log.e(TAG, "Location update received: $locations")
        for (location in locations){
            onLocationFetched(location)
        }
    }
    locationProvider!!.addLocationObserver(locationObserver)
}

// Data class for clinic information
data class ClinicLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val details: String? = null // Optional details like address or phone
)
