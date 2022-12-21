package test.myPackage.mfa.routee.database

interface DatabaseHelper {
    suspend fun getAllLocation(): List<LocationDataClass>
    suspend fun getLastLocation(): LocationDataClass
    suspend fun addLocation(locationDataClass: LocationDataClass)
    suspend fun deleteLocation(locationDataClass: LocationDataClass)
    suspend fun deleteAllLocations()
}