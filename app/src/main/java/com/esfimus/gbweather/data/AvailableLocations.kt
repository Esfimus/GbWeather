package com.esfimus.gbweather.data

import com.esfimus.gbweather.domain.CustomLocation

class AvailableLocations {

    private val availableLocations = listOf(
        CustomLocation("MOSCOW", 55.735069, 37.406704),
        CustomLocation("BEIJING", 39.901850, 116.391441),
        CustomLocation("TOKYO", 35.681729, 139.753927),
        CustomLocation("NAIROBI", -1.272749, 36.827872),
        CustomLocation("NEW DELHI", 28.614243, 77.202788),
        CustomLocation("PRAGUE", 50.080345, 14.428974),
        CustomLocation("TEHRAN", 35.699706, 51.337425),
        CustomLocation("ATHENS", 37.975534, 23.734855),
        CustomLocation("WASHINGTON", 38.899513, -77.036536),
        CustomLocation("MEXICO", 19.432595, -99.133313),
        CustomLocation("STOCKHOLM", 59.335530, 18.057745),
        CustomLocation("SANTIAGO", -33.445255, -70.653422),
        )

    fun getLocation(locationName: String): CustomLocation? {
        for (customLocation in availableLocations) {
            if (customLocation.name.lowercase() == locationName.lowercase()) return customLocation
        }
        return null
    }
}