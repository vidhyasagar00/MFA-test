package test.myPackage.mfa.routee.interfaces

import test.myPackage.mfa.routee.location.LocationObject
import test.myPackage.mfa.routee.location.SingleLocationObject
import test.myPackage.mfa.routee.otpdetection.MessageRequestObject
import test.myPackage.mfa.routee.setUp.ApplicationDetails
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface API {
    companion object {
        var BASE_URL = ""
        var baseUrlChanged = false
    }

    @POST
    fun postConfiguration(
        @Url url: String,
        @Body applicationDetails: ApplicationDetails,
    ): Call<Any>?

    @Headers("content-type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun postAPIForAccessToken(
        @Header("Authorization") token: String,
        @Url url: String,
        @Field("grant_type") grantType: String,
    ): Call<Any>?

    @POST
    fun postAPIForLocationUpdate(
        @Url url: String,
        @Body locationObject: LocationObject
    ): Call<Any>?

    @POST
    fun postAPIForLocationUpdate(
        @Url url: String,
        @Body singleLocationObject: SingleLocationObject
    ): Call<ResponseBody>?

    @POST
    fun sendOTP(
        @Url url: String,
        @Header("Authorization") accessToken: String,
        @Body messageRequestObject: MessageRequestObject,
    ): Call<Any>?

    @FormUrlEncoded
    @POST("/2step/{trackingId}")
    fun verifyOTP(
        @Header("Authorization") accessToken: String,
        @Path("trackingId") trackingId: String,
        @Field("answer") otp: String,
    ): Call<Any>?


}
