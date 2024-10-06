@file:OptIn(ExperimentalFoundationApi::class)

package com.taxi.taxist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.taxi.taxist.network.GooglePlacesApi
import com.taxi.taxist.network.Location
import com.taxi.taxist.network.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Map : AppCompatActivity() {

    companion object {
        private var isFineLocationPermissionGranted = false
        private var isCoarseLocationPermissionGranted = false

        private var isDeparturePointField = true

        val currentRoadPath: MutableStateFlow<MutableList<LatLng>?> = MutableStateFlow(null)
        val foundPlaces: MutableStateFlow<List<Prediction>> = MutableStateFlow(listOf())
        private var foundPlacesPreviously: List<Prediction> = listOf()
        val isNetworkConnected = MutableStateFlow(false)
    }

    private val transportTypes by lazy {
        listOf(
            getString(R.string.all_types),
            getString(R.string.sedan),
            getString(R.string.station_wagon),
            getString(R.string.hatchback),
            getString(R.string.coupe),
            getString(R.string.crossover),
        )
    }
    private val searchForList by lazy {
        listOf(
            getString(R.string.name),
            getString(R.string.car),
            getString(R.string.price)
        )
    }

    private var allDrivers = ArrayList<DriverInfo>()
    private var listOfDrivers = MutableStateFlow(ArrayList<DriverInfo>())
    private var textOfSearchDriver = MutableStateFlow("")

    private val isExplanationOfConstructionVisible = MutableStateFlow(false)

    private var placeOfDeparture: MutableStateFlow<Location?> = MutableStateFlow(null)
    private var placeOfArrival: MutableStateFlow<Location?> = MutableStateFlow(null)
    private var textOfDeparturePoint = MutableStateFlow("")
    private var textOfArrivalPoint = MutableStateFlow("")

    private var currentDriver: MutableStateFlow<DriverInfo?> = MutableStateFlow(null)
    private var priceOfTrip: MutableStateFlow<Int?> = MutableStateFlow(null)
    private var distanceOfTrip: MutableStateFlow<Long?> = MutableStateFlow(null)
    private var durationOfTrip: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val  coroutineScope = CoroutineScope(Dispatchers.Default)

    private val googlePlacesApiClass = GooglePlacesApi()
    private val mapControlClass = MapControl()
    private val database = FakeData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Taxist)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        realizeVariables()

        setContent {
            animationClass.Realize()

            MainPreview()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
        } else {
            getUserLocation()
        }
    }

    private var locationPermissionRequest: ActivityResultLauncher<Array<String>>? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun realizeVariables() {
        //initializeMapAPI()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
            when {
                permissions.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false
                ) -> {
                    // Precise location access granted.
                    isFineLocationPermissionGranted = true

                    getUserLocation()
                }

                permissions.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    false
                ) -> {
                    // Only approximate location access granted.
                    isCoarseLocationPermissionGranted = true

                    getUserLocation()
                }

                else -> {
                    // No location access granted.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                        Utils.message(
                            shortDescription = getString(R.string.problem),
                            buttonDescription = getString(R.string.confirm),
                            description = getString(R.string.we_need_location_permission_description),
                            onButtonClick = {
                                requestLocationPermissions()
                            }
                        )
                    }
                }
            }
        }

        loadDrivers()

        // checking network connection
        isNetworkConnected.value = Utils.isNetworkConnected(this@Map)
    }

    private fun requestLocationPermissions(){
        if (locationPermissionRequest != null) {
            locationPermissionRequest!!.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            throw RuntimeException("Location permission request wasn't initialized")
        }
    }

    var userLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    private fun getUserLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: android.location.Location? ->
                    if (location != null)
                        userLocation.value = Location(
                            lat = location.latitude,
                            lng = location.longitude
                        )
                }
        } else {
            Utils.message(
                shortDescription = getString(R.string.problem),
                description = getString(R.string.we_cannot_get_your_location),
                buttonDescription = getString(R.string.ok)
            )
        }
    }

    private fun loadDrivers(){
        if (allDrivers.isEmpty()) {
            coroutineScope.launch {
                allDrivers = database.getDrivers(this@Map)

                updateDrivers(allDrivers)
            }
        }
    }

    private fun makeRoad(from: Location, to: Location){
        val fromLL = LatLng(
            from.lat,
            from.lng,
        )
        val toLL = LatLng(
            to.lat,
            to.lng,
        )

        mapControlClass.putRoad(
            this,
            fromLL,
            toLL,
            onResult = { result ->
                currentRoadPath.value = mapControlClass.getPolyline(result)

                distanceOfTrip.value = result.routes[0].legs[0].distance.inMeters
                durationOfTrip.value = result.routes[0].legs[0].duration.inSeconds

                setPrice(distanceOfTrip.value!!)
            },
            onFailure = {
                currentRoadPath.value = null
            }
        )
    }

    private fun setPrice(distanceInMeters: Long){
        val distanceInKilometers = distanceInMeters / 1000

        if (currentDriver.value != null) {
            if (distanceInKilometers in 0..<5) {
                priceOfTrip.value = currentDriver.value!!.tariffS
            } else if (distanceInKilometers in 5..<10) {
                priceOfTrip.value = currentDriver.value!!.tariffM
            } else if (distanceInKilometers in 10..15) {
                priceOfTrip.value = currentDriver.value!!.tariffH
            } else if (distanceInKilometers > 15) {
                priceOfTrip.value = null
            }
        }
    }

    private fun selectDriver(driverInfo: DriverInfo){
        currentDriver.value = driverInfo

        if (distanceOfTrip.value != null) {
            setPrice(distanceOfTrip.value!!)
        }
    }

    private fun updateDrivers(drivers: List<DriverInfo>){
        listOfDrivers.value = drivers as ArrayList<DriverInfo>
    }

    private fun searchForDrivers(request: String, typeOfSearch: String, typeOfCarBody: String, drivers: List<DriverInfo>){
        var searchForBodyesList = ArrayList<DriverInfo>()
        var finalList = ArrayList<DriverInfo>()

        // remove not compatible elementsf
        if (typeOfCarBody != getString(R.string.all_types)) {
            drivers.forEach {
                if (it.typeOfCar == typeOfCarBody) {
                    searchForBodyesList.add(it)
                }
            }
        } else {
            searchForBodyesList = drivers.toList() as ArrayList<DriverInfo>
        }

        when (typeOfSearch) {
            getString(R.string.name) -> {
                searchForBodyesList.forEach { driver ->
                    if (driver.name.lowercase().contains(request.lowercase())) {
                        finalList.add(driver)
                    }
                }
            }

            getString(R.string.car) -> {
                searchForBodyesList.forEach { driver ->
                    if (driver.car.lowercase().contains(request.lowercase())) {
                        finalList.add(driver)
                    }
                }
            }

            getString(R.string.price) -> {
                if (request.all { it.isDigit() }){
                    finalList = searchForBodyesList

                    finalList.sortBy { driver ->
                        Math.abs(request.toInt() - driver.tariffM)
                    }
                } else {
                    Utils.message(
                        shortDescription = getString(R.string.error),
                        buttonDescription = getString(R.string.ok),
                        description = getString(R.string.error_text_only_numbers),
                        onButtonClick = {
                            textOfSearchDriver.value = ""
                        }
                    )
                }
            }
        }

        updateDrivers(finalList)
    }

    @Composable
    private fun MainPreview() {
        val isNetworkConnectedState by isNetworkConnected.collectAsState()
        val isMassageVisibleState by isMessageVisible.collectAsState()

        val isExplanationOfConstructionVisibleState by isExplanationOfConstructionVisible.collectAsState()
        val blurAnimation by animateDpAsState(
            targetValue = if (isExplanationOfConstructionVisibleState) 7.dp else 0.dp,
            animationSpec = tween(
                durationMillis = 1000,
                easing = Ease
            )
        )

        GoogleMap(modifier = Modifier.fillMaxSize()) {

        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurAnimation)
        ) {
            MakeDeliverRequestMenu()
        }

        animationClass.AppearView(isExplanationOfConstructionVisibleState) {
            ExplanationOfConstruction()
        }

        animationClass.AppearView(!isNetworkConnectedState){
          InternetConnectionError()
        }

        animationClass.AppearView(
            isVisible = isMassageVisibleState
        ) {
            Box(Modifier.fillMaxSize().background(colorResource(R.color.whiteF2B20).copy(alpha = 0.5f)))
        }
        animationClass.SelfFromRight(
            modifier = Modifier.fillMaxSize().clickable(enabled = false, onClick = {}),
            isVisible = isMassageVisibleState
        ){
            Message(Modifier.align(Alignment.Center))
        }
    }

    @Composable
    private fun MakeDeliverRequestMenu() {
        Box(
            modifier = Modifier
                .background(colorResource(R.color.black15WF2))
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)

                ) {
                    val userLocationState by userLocation.collectAsState()
                    val cameraPositionState = rememberCameraPositionState()

                    val roadPath by currentRoadPath.collectAsState()
                    val departurePoint by placeOfDeparture.collectAsState()
                    val arrivalPoint by placeOfArrival.collectAsState()

                    LaunchedEffect(userLocationState) {
                        userLocationState?.let { location ->
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.lat,
                                        location.lng
                                    ), 12f
                                )
                            )
                        }
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            indoorLevelPickerEnabled = false,
                            mapToolbarEnabled = false,
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false,
                        ),
                        mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
                        ) {

                        // if roadPath != null show the road
                        roadPath?.let {
                            Polyline(
                                points = it,
                                color = colorResource(R.color.taxi)
                            )
                        }

                        departurePoint?.let {
                            Marker(
                                state = MarkerState(LatLng(it.lat, it.lng)),
                                icon = Utils.bitmapDescriptorFromVector(
                                    this@Map,
                                    R.drawable.map_marker_from
                                )
                            )
                        }

                        arrivalPoint?.let {
                            Marker(
                                state = MarkerState(LatLng(it.lat, it.lng)),
                                icon = Utils.bitmapDescriptorFromVector(
                                    this@Map,
                                    R.drawable.map_marker_to
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    ) {
                        val priceOfTrip by priceOfTrip.collectAsState()
                        animationClass.SelfFromLeft(isVisible = priceOfTrip != null) {
                            styleClass.DecorationBackground(
                                color = colorResource(R.color.whiteF2B20)
                            )
                            {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                    text = "${stringResource(R.string.price)}: $priceOfTrip$",
                                    fontFamily = unbounded_regular,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.black15WF2),
                                )
                            }

                        }

                        Spacer(Modifier.weight(1f))

                        styleClass.DecorationBackground(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    // put the road
                                    if (placeOfDeparture.value != null && placeOfArrival.value != null) {
                                        makeRoad(placeOfDeparture.value!!, placeOfArrival.value!!)
                                    } else {
                                        // massage about empty of one field
                                        if (placeOfDeparture.value == null) {
                                            Utils.message(
                                                shortDescription = getString(R.string.problem),
                                                buttonDescription = getString(R.string.ok),
                                                description = getString(R.string.enter_departure_point),
                                            )
                                        } else {
                                            Utils.message(
                                                shortDescription = getString(R.string.problem),
                                                buttonDescription = getString(R.string.ok),
                                                description = getString(R.string.enter_point_of_arrival),
                                            )
                                        }
                                    }
                                }
                            ),
                            color = colorResource(R.color.whiteF2B20)
                        )
                        {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                text = stringResource(R.string.make_a_route),
                                fontFamily = unbounded_regular,
                                fontSize = 12.sp,
                                color = colorResource(R.color.black15WF2),
                            )
                        }
                    }

                    val foundPlacesState by foundPlaces.collectAsState()


                    animationClass.SelfFromBottom(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        duration = 800,
                        isVisible = foundPlacesState.isNotEmpty()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 40.dp)
                                .background(colorResource(R.color.whiteDB30))
                                .padding(vertical = 15.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val listOfPlaces: List<Prediction> =
                                if (foundPlacesState.isNotEmpty()) {
                                    foundPlacesPreviously = foundPlacesState

                                    foundPlacesState
                                } else {
                                    foundPlacesPreviously
                                }

                            items(listOfPlaces) { place ->
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            // select place
                                            coroutineScope.launch {
                                                // hide places list
                                                foundPlaces.value = listOf()
                                                // clear road
                                                currentRoadPath.value = null

                                                withContext(Dispatchers.IO) {
                                                    val placeResponse =
                                                        googlePlacesApiClass.makePlaceRequest(
                                                            this@Map,
                                                            place.place_id
                                                        )

                                                    if (isDeparturePointField) {
                                                        placeOfDeparture.value =
                                                            placeResponse!!.result.geometry.location

                                                        textOfDeparturePoint.value =
                                                            place.description

                                                    } else {
                                                        placeOfArrival.value =
                                                            placeResponse!!.result.geometry.location

                                                        textOfArrivalPoint.value = place.description
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        currentFocus?.clearFocus()

                                                        cameraPositionState.animate(
                                                            CameraUpdateFactory.newLatLngZoom(
                                                                LatLng(
                                                                    placeResponse.result.geometry.location.lat,
                                                                    placeResponse.result.geometry.location.lng,
                                                                ),
                                                                12f
                                                            ),
                                                            1000
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        .padding(horizontal = 20.dp),
                                    text = place.description,
                                    fontFamily = unbounded_regular,
                                    fontSize = 14.sp,
                                    color = colorResource(R.color.black15WF2)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .background(colorResource(R.color.whiteF2B20))
                        .padding(horizontal = 40.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    styleClass.DecorationBackground(
                        color = colorResource(R.color.black20WF2)
                    ) {
                        val textOfField by textOfDeparturePoint.collectAsState()
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 20.dp, vertical = 15.dp)
                                .padding(end = 10.dp),
                            value = textOfField,
                            onValueChange = { text ->
                                textOfDeparturePoint.value = text

                                isDeparturePointField = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    if (userLocation.value != null){
                                        foundPlaces.value = googlePlacesApiClass.makeSearchRequest(this@Map, text, userLocation.value!!)
                                    } else {
                                        foundPlaces.value = googlePlacesApiClass.makeSearchRequest(this@Map, text)
                                    }
                                }
                            },
                            cursorBrush = SolidColor(colorResource(R.color.whiteF2B15)),
                            textStyle = TextStyle(
                                fontFamily = unbounded_regular,
                                fontSize = 14.sp,
                                color = colorResource(R.color.whiteF2B15),
                            ),
                            maxLines = 3,
                            decorationBox = { TextField ->
                                Box {
                                    TextField()

                                    if (textOfField.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.enter_departure_point),
                                            fontFamily = unbounded_regular,
                                            fontSize = 14.sp,
                                            color = colorResource(R.color.whiteF2B15)
                                        )
                                    }
                                }
                            }
                        )

                        Image(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                                .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    getUserLocation()

                                    placeOfDeparture = userLocation
                                    textOfDeparturePoint.value = getString(R.string.your_location)
                                }
                            ),
                            painter = painterResource(R.drawable.your_location),
                            contentDescription = null
                        )
                    }

                    Spacer(Modifier.height(25.dp))

                    styleClass.DecorationBackground(
                        color = colorResource(R.color.black20WF2)
                    ) {
                        val textOfField by textOfArrivalPoint.collectAsState()
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 20.dp, vertical = 15.dp),
                            value = textOfField,
                            onValueChange = { text ->
                                textOfArrivalPoint.value = text

                                isDeparturePointField = false

                                coroutineScope.launch(Dispatchers.IO) {
                                    if (userLocation.value != null){
                                        foundPlaces.value = googlePlacesApiClass.makeSearchRequest(this@Map, text, userLocation.value!!)
                                    } else {
                                        foundPlaces.value = googlePlacesApiClass.makeSearchRequest(this@Map, text)
                                    }
                                }
                            },
                            cursorBrush = SolidColor(colorResource(R.color.whiteF2B15)),
                            textStyle = TextStyle(
                                fontFamily = unbounded_regular,
                                fontSize = 14.sp,
                                color = colorResource(R.color.whiteF2B15),
                            ),
                            maxLines = 3,
                            decorationBox = { TextField ->
                                Box {
                                    TextField()

                                    if (textOfField.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.enter_point_of_arrival),
                                            fontFamily = unbounded_regular,
                                            fontSize = 14.sp,
                                            color = colorResource(R.color.whiteF2B15)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

            var isVisibleChangeDriver by remember { mutableStateOf(false) }
            animationClass.AppearView(isVisibleChangeDriver) {
                ChangeDriver()
            }

            styleClass.DecorationBackgroundWithFixedCorners(
                modifier = Modifier
                    .padding(horizontal = 50.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 40.dp),
                color = colorResource(R.color.whiteF2B20),
                widthOfCorners = 20.dp
            ) {
                // taxi bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val driverInfo by currentDriver.collectAsState()
                    if (driverInfo != null) {
                        Column {
                            Text(
                                modifier = Modifier
                                    .widthIn(100.dp, 150.dp)
                                    .fadingEdgeHorizontal(5.dp,5.dp)
                                    .basicMarquee()
                                    .padding(horizontal = 5.dp),
                                text = stringResource(R.string.driver) + " : " + driverInfo?.name, // driver
                                fontFamily = unbounded_regular,
                                fontSize = 14.sp,
                                color = colorResource(R.color.black15WF2),
                                maxLines = 1
                            )

                            Spacer(Modifier.height(3.dp))

                            Text(
                                text = stringResource(R.string.tariff) + " : " + driverInfo?.tariffM + "$", // tariff
                                fontFamily = unbounded_regular,
                                fontSize = 14.sp,
                                color = colorResource(R.color.black15WF2),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.width(10.dp))

                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                isVisibleChangeDriver = !isVisibleChangeDriver
                            }
                        ),
                        text =
                        if (isVisibleChangeDriver)
                            stringResource(R.string.close)
                        else if (driverInfo != null)
                            stringResource(R.string.change)
                        else
                            stringResource(R.string.choose_a_driver),
                        fontFamily = unbounded_black,
                        fontSize = 14.sp,
                        color = colorResource(R.color.black15WF2),
                        textDecoration = TextDecoration.Underline,
                        maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    private fun ChangeDriver() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {})
                .background(colorResource(R.color.whiteF2B20).copy(alpha = 0.5f))
                .padding(top = 40.dp)
        ) {

            val listOfDriversState by listOfDrivers.collectAsState()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 100.dp)
            ){
                Box(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 50.dp, end = 30.dp)
                        .background(colorResource(R.color.whiteDB15))
                )

                val currentDriverState by currentDriver.collectAsState()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 55.dp, end = 40.dp)
                        .fadingEdgeVertical(20.dp, 30.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    if (listOfDriversState.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(10.dp))
                        }

                        items(listOfDriversState) { driver ->
                            DriverCard(
                                modifier = Modifier.combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        selectDriver(driver)
                                    },
                                    onLongClick = {
                                        driver.isExpended.value = !driver.isExpended.value
                                    },
                                    onDoubleClick = {
                                        driver.isExpended.value = !driver.isExpended.value
                                    }
                                ),
                                driverInfo = driver,
                                isSelected = driver == currentDriverState,
                                isExpended = driver.isExpended.value
                            )

                        }

                        item {
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }

                if (listOfDriversState.isEmpty()){
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 50.dp),
                        text = stringResource(R.string.sorry_we_didnt_find_driver),
                        fontFamily = unbounded_bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.black15WF2),
                        textAlign = TextAlign.Center
                    )
                }
            }


            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .background(colorResource(R.color.whiteF2B20))
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
                        text = "${stringResource(R.string.transport_type)} :",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    var typeOfCarSelected by remember { mutableStateOf(getString(R.string.all_types)) }
                    Row(
                        modifier = Modifier
                            .fadingEdgeHorizontal(10.dp, 10.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Spacer(Modifier.width(20.dp))

                        transportTypes.forEach {
                            ChoosingItem(
                                text = it,
                                isActivated = it == typeOfCarSelected,
                                onClick = {
                                    typeOfCarSelected = it

                                    textOfSearchDriver.value = ""
                                }
                            )
                        }

                        Spacer(Modifier.width(20.dp))
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
                        text = "${stringResource(R.string.search_for)} :",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    var searchForSelected by remember { mutableStateOf(getString(R.string.name)) }
                    Row(
                        modifier = Modifier
                            .fadingEdgeHorizontal(10.dp, 10.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Spacer(Modifier.width(20.dp))

                        searchForList.forEach {
                            ChoosingItem(
                                text = it,
                                isActivated = it == searchForSelected,
                                onClick = {
                                    searchForSelected = it

                                    textOfSearchDriver.value = ""
                                }
                            )
                        }

                        Spacer(Modifier.width(20.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    val textOfSearchDriverState by textOfSearchDriver.collectAsState()
                    BasicTextField(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(start = 40.dp, bottom = 20.dp,),
                        value = textOfSearchDriverState,
                        onValueChange = {
                            textOfSearchDriver.value = it

                            searchForDrivers(it, searchForSelected, typeOfCarSelected, allDrivers)

                                        },
                        textStyle = TextStyle(
                            fontFamily = unbounded_medium,
                            fontSize = 14.sp,
                            color = colorResource(R.color.black15WF2)
                        ),
                        cursorBrush = SolidColor(colorResource(R.color.black15WF2)),
                        decorationBox = { TextField ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Box {
                                    TextField()

                                    if (textOfSearchDriverState.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.enter_text),
                                            fontFamily = unbounded_regular,
                                            fontSize = 14.sp,
                                            color = colorResource(R.color.grey76)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(2.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(
                                            colorResource(R.color.black15WF2),
                                            CircleShape
                                        )
                                )
                            }
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                ) {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = 25.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    isExplanationOfConstructionVisible.value = true
                                }
                            ),
                        painter = painterResource(R.drawable.attention_icon),
                        contentDescription = null,
                    )
                }
            }
        }
    }

    @Composable
    private fun ExplanationOfConstruction() {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.whiteF2B15).copy(alpha = 0.5f))
                    .clickable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .blur(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentHeight()
                        .background(colorResource(R.color.whiteF2B20))
                        .padding(horizontal = 20.dp, vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box {
                        Text(
                            modifier = Modifier.padding(end = 10.dp),
                            text = "· ${stringResource(R.string.fare_explanation)}",
                            fontFamily = unbounded_regular,
                            fontSize = 10.sp,
                            color = colorResource(R.color.black15WF2)
                        )

                        Image(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        isExplanationOfConstructionVisible.value = false
                                    }
                                )
                                .offset(x = 15.dp, y = (-10).dp),
                            painter = painterResource(R.drawable.close_button),
                            contentDescription = null
                        )
                    }

                    Text(
                        text = "· ${stringResource(R.string.card_explanation)}",
                        fontFamily = unbounded_regular,
                        fontSize = 10.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    Text(
                        text = "· ${stringResource(R.string.price_explanation)}",
                        fontFamily = unbounded_regular,
                        fontSize = 10.sp,
                        color = colorResource(R.color.black15WF2)
                    )
                }

                Spacer(Modifier.height(20.dp))

                var animationStep by remember { mutableIntStateOf(-1) }
                val alphaIcon by animateFloatAsState(
                    targetValue = if (animationStep == 0) 0.3f else 1f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = EaseInOut
                    ), label = "",
                    finishedListener = {
                        if (animationStep == 0) {
                            animationStep = 1
                        }
                    }
                )

                val alphaName by animateFloatAsState(
                    targetValue = if (animationStep == 1) 0.3f else 1f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = EaseInOut
                    ), label = "",
                    finishedListener = {
                        if (animationStep == 1) {
                            animationStep = 2
                        }
                    }
                )

                val alphaAuto by animateFloatAsState(
                    targetValue = if (animationStep == 2) 0.3f else 1f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = EaseInOut
                    ), label = "",
                    finishedListener = {
                        if (animationStep == 2) {
                            animationStep = 3
                        }
                    }
                )

                val alphaPrice by animateFloatAsState(
                    targetValue = if (animationStep == 3) 0.3f else 1f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = EaseInOut
                    ), label = "",
                    finishedListener = {
                        if (animationStep == 3) {
                            animationStep = 0
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    animationStep = 0
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(horizontal = 10.dp)
                ) {
                    styleClass.DecorationBackgroundWithFixedCorners(
                        modifier = Modifier
                            .padding(horizontal = 30.dp),
                        color = colorResource(R.color.whiteF2B20),
                        widthOfCorners = 20.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 3.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .graphicsLayer(alpha = alphaIcon),
                                painter = painterResource(R.drawable.unknow_icon),
                                contentDescription = null,
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 10.dp),
                                verticalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 20.dp, bottom = 10.dp)
                                        .graphicsLayer(alpha = alphaName),
                                    text = "Taras",
                                    fontFamily = unbounded_medium,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.black15WF2)
                                )
                                Text(
                                    modifier = Modifier
                                        .width(170.dp)
                                        .padding(start = 15.dp)
                                        .graphicsLayer(alpha = alphaAuto),
                                    text = "Mercedes-Benz C300 2023",
                                    fontFamily = unbounded_regular,
                                    fontSize = 10.sp,
                                    color = colorResource(R.color.black15WF2),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            Text(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .graphicsLayer(alpha = alphaPrice),
                                text = "29$",
                                fontFamily = unbounded_regular,
                                fontSize = 12.sp,
                                color = colorResource(R.color.black15WF2)
                            )
                        }

                        Image(
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .offset(x = 23.dp),
                            painter = painterResource(R.drawable.die_to_driver_card),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight

                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentHeight()
                        .background(colorResource(R.color.whiteF2B20))
                        .padding(horizontal = 20.dp, vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        modifier = Modifier.graphicsLayer(alpha = alphaIcon),
                        text = "· ${stringResource(R.string.image_of_driver)}",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    Text(
                        modifier = Modifier.graphicsLayer(alpha = alphaName),
                        text = "· ${stringResource(R.string.name_of_driver)}",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    Text(
                        modifier = Modifier.graphicsLayer(alpha = alphaAuto),
                        text = "· ${stringResource(R.string.drivers_car)}",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    Text(
                        modifier = Modifier.graphicsLayer(alpha = alphaPrice),
                        text = "· ${stringResource(R.string.driver_fare)}",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )
                }
            }

            Column (
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ){
                Text(
                    text = stringResource(R.string.created_by),
                    fontFamily = unbounded_bold,
                    fontSize = 12.sp,
                    color = colorResource(R.color.black15WF2)
                )

                Text(
                    text = stringResource(R.string.created_for),
                    fontFamily = unbounded_bold,
                    fontSize = 12.sp,
                    color = colorResource(R.color.black15WF2)
                )
            }
        }
    }

    @Composable
    fun InternetConnectionError() {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.whiteF2B20))
                .padding(top = 70.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(1.5f))

            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(R.drawable.no_internet_connection_error),
                contentDescription = null
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.internet_error),
                fontFamily = unbounded_bold,
                fontSize = 16.sp,
                color = colorResource(R.color.black15WF2)
            )

            Spacer(Modifier.weight(1f))

            styleClass.DecorationBackground(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            isNetworkConnected.value = Utils.isNetworkConnected(this@Map)
                        }
                    ),
                color = colorResource(R.color.black20WF2)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
                    text = stringResource(R.string.try_again),
                    fontFamily = unbounded_bold,
                    fontSize = 16.sp,
                    color = colorResource(R.color.whiteF2B15)
                )
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

val animationClass = Animation()
val styleClass: Style = Style()

val unbounded_regular = FontFamily(
    Font(R.font.unbounded_regular)
)

val unbounded_medium = FontFamily(
    Font(R.font.unbounded_medium)
)

val unbounded_bold = FontFamily(
    Font(R.font.unbounded_bold)
)

val unbounded_black = FontFamily(
    Font(R.font.unbounded_black)
)

@Composable
private fun DriverCard(
    modifier: Modifier = Modifier,
    driverInfo: DriverInfo,
    isSelected: Boolean,
    isExpended: Boolean = false
) {

    Box (modifier = modifier) {
        animationClass.AnimateHeight(isExpended, 220.dp) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp)
                    .background(colorResource(R.color.whiteE9B2C))
                    .padding(top = 70.dp)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Row {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Text(
                            text = "0-5 ${stringResource(R.string.km)}: ${driverInfo.tariffS}$",
                            fontFamily = unbounded_medium,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black15WF2)
                        )

                        Text(
                            text = "5-10 ${stringResource(R.string.km)}: ${driverInfo.tariffM}$",
                            fontFamily = unbounded_medium,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black15WF2)
                        )

                        Text(
                            text = "10-15 ${stringResource(R.string.km)}: ${driverInfo.tariffH}$",
                            fontFamily = unbounded_medium,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black15WF2)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Text(
                            text = "${stringResource(R.string.experience)}: ${driverInfo.experience} ${stringResource(R.string.years)}",
                            fontFamily = unbounded_medium,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black15WF2)
                        )

                        Text(
                            text = "${stringResource(R.string.rating)}: ${driverInfo.rate}",
                            fontFamily = unbounded_medium,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black15WF2)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ){
                    Text(
                        text = "${stringResource(R.string.type_of_body)}: ${driverInfo.typeOfCar}",
                        fontFamily = unbounded_medium,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )

                    Text(
                        text = "${stringResource(R.string.phone_number)}: ${driverInfo.phoneNumber}",
                        fontFamily = unbounded_medium,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .height(IntrinsicSize.Min)
        ) {
            styleClass.DecorationBackgroundWithFixedCorners(
                modifier = Modifier
                    .padding(start = 20.dp, end = 23.dp),
                color = colorResource(R.color.whiteF2B20),
                widthOfCorners = 20.dp
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (driverInfo.image != null) {
                        Image(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(3.dp)
                                .clip(CircleShape),
                            bitmap = driverInfo.image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxHeight()
                                .padding(vertical = 3.dp),
                            painter = painterResource (R.drawable.unknow_icon),
                            contentDescription = null
                        )
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ){
                        Text(
                            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
                            text = driverInfo.name,
                            fontFamily = unbounded_medium,
                            fontSize = 12.sp,
                            color = colorResource(R.color.black15WF2)
                        )
                        Text(
                            modifier = Modifier
                                .width(150.dp)
                                .padding(start = 12.dp)
                                .fadingEdgeHorizontal(3.dp, 3.dp)
                                .basicMarquee()
                                .padding(start = 3.dp),
                            text = driverInfo.car,
                            fontFamily = unbounded_regular,
                            fontSize = 10.sp,
                            color = colorResource(R.color.black15WF2),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        modifier = Modifier.padding(end = 5.dp),
                        text = "${driverInfo.tariffM}$",
                        fontFamily = unbounded_regular,
                        fontSize = 12.sp,
                        color = colorResource(R.color.black15WF2)
                    )
                }
                val colorOfEndIcon by animateColorAsState(
                    targetValue = if (isSelected) colorResource(R.color.taxi) else colorResource(R.color.black20WF2),
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = EaseInOutCubic
                    )
                )

                Image(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .offset(x = 23.dp),
                    painter = painterResource(R.drawable.die_to_driver_card),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorOfEndIcon),
                    contentScale = ContentScale.FillHeight

                )
            }
        }
    }
}

@Composable
private fun ChoosingItem(
    modifier: Modifier = Modifier,
    text: String,
    isActivated: Boolean,
    onClick: () -> Unit
) {
    val colorOfCell by animateColorAsState(
        targetValue = if (isActivated) colorResource(R.color.taxi) else colorResource(R.color.black20WF2),
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOut
        )
    )

    styleClass.DecorationBackground(
        modifier = modifier.clickable(onClick = onClick),
        color = colorOfCell
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            text = text,
            fontFamily = unbounded_medium,
            fontSize = 12.sp,
            color = colorResource(R.color.whiteF2B15)
        )
    }
}


val isMessageVisible = MutableStateFlow(false)
val messageShortDes = MutableStateFlow("")
val messageDes = MutableStateFlow("")
val messageButtonDes = MutableStateFlow("")
val messageOnOk = MutableStateFlow {}

@Composable
fun Message(modifier: Modifier = Modifier) {
    val shortDescription by messageShortDes.collectAsState()
    val buttonDescription by messageButtonDes.collectAsState()
    val description by messageDes.collectAsState()
    val onOk by messageOnOk.collectAsState()

    Box(
        modifier = modifier.widthIn(min = 220.dp, max = 350.dp)
            .padding(horizontal = 50.dp)
    ){
         Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .background(colorResource(R.color.black20WF2))
                    .padding(horizontal = 25.dp, vertical = 35.dp),
                text = description,
                fontFamily = unbounded_regular,
                fontSize = 14.sp,
                color = colorResource(R.color.whiteF2B15)
         )


        styleClass.DecorationBackground(
            modifier = Modifier
                .widthIn(80.dp, 180.dp)
                .align(Alignment.TopStart),
            color = colorResource(R.color.whiteF2B20)
        ){
            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                text = shortDescription,
                fontFamily = unbounded_regular,
                fontSize = 14.sp,
                color = colorResource(R.color.black15WF2)
            )
        }

        styleClass.DecorationBackground(
            modifier = Modifier
                .widthIn(80.dp, 180.dp)
                .align(Alignment.BottomEnd)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        onOk()

                        isMessageVisible.value = false
                    }
                ),
            color = colorResource(R.color.whiteF2B20)
        ){
            Text(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 10.dp),
                text = buttonDescription,
                fontFamily = unbounded_regular,
                fontSize = 14.sp,
                color = colorResource(R.color.black15WF2)
            )
        }
    }
}

@Composable
fun Modifier.fadingEdgeVertical(topEdge: Dp, bottomEdge: Dp) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startY = 0f,
                endY = topEdge.toPx(),
            ),
            blendMode = BlendMode.DstIn,
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startY = size.height - bottomEdge.toPx(),
                endY = size.height,
            ),
            blendMode = BlendMode.DstIn,
        )
    }

@Composable
fun Modifier.fadingEdgeHorizontal(startEdge: Dp, endEdge: Dp) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startX = 0f,
                endX = startEdge.toPx(),
            ),
            blendMode = BlendMode.DstIn,
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startX = size.width - endEdge.toPx(),
                endX = size.width,
            ),
            blendMode = BlendMode.DstIn,
        )
    }