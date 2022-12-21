package test.myPackage.mfa.routee.location

import test.myPackage.mfa.routee.database.LocationDataClass

data class LocationObject(
    val deviceId: String?,
    val version: String?,
    val data: List<LocationDataClass>?
)
