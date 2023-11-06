package com.dreamteam.sharedream.model

import com.naver.maps.geometry.LatLng

data class LocationData (
    val latLng : LatLng,
    val address : String,
    val cityInfo : List<String>,
)
