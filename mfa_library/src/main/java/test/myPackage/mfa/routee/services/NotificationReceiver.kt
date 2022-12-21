package test.myPackage.mfa.routee.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import test.myPackage.mfa.routee.preference.SharedPreference
import test.myPackage.mfa.routee.retrofit.APISupport
import test.myPackage.mfa.routee.setUp.ApplicationDetails
import test.myPackage.mfa.routee.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.v("received -- ", intent.toString())
        val manager = NotificationManagerCompat.from(context)
        manager.cancelAll()

        val preference = SharedPreference(context)
        val configurationObject = JSONObject(preference.getInitialConfiguration())
        if (intent.hasExtra("accessToken")) {
            intent.getStringExtra("assessToken")?.let { preference.setNotificationAccessToken(it) }
        }
        val applicationDetails = ApplicationDetails(deviceUUID = preference.getDeviceUUID(), applicationUUID = configurationObject.getString("applicationUUID"), userId = preference.getUserId(), actionToken = preference.getNotificationAccessToken(), actionChoice = "2")
        val callback = APISupport.postAPI(Constants.API_URL_FOR_AUTH_PERMISSIONS, applicationDetails)
        callback?.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
//                if (response.isSuccessful) {
//                    // success response
//                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // failure response
            }

        })
    }
}