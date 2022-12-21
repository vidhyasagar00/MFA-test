package com.net.firstmodule

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.net.routee.biometric.BiometricFingerPrintStatus
import com.net.routee.biometric.BiometricResultCallback
import com.net.routee.firebaseMessage.FCMMessageService
import com.net.routee.firebaseMessage.FCMTokenCallback
import com.net.routee.interfaces.ScreenOpenHelper
import com.net.routee.location.LocationClient
import com.net.routee.otpdetection.AppSignatureHelper
import com.net.routee.preference.SharedPreference
import com.net.routee.services.FCMServices
import com.net.routee.services.MyLocationService
import com.net.routee.setUp.ApplicationInteractionClass
import com.net.routee.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ScreenOpenHelper {
    private lateinit var locationClient: LocationClient
    private lateinit var fcmMessageService: FCMMessageService
    val biometricResultCallback = object : BiometricResultCallback {
        override fun permissionGranted(biometricFingerPrintStatus: BiometricFingerPrintStatus) {
            if (biometricFingerPrintStatus == BiometricFingerPrintStatus.SUCCESS) {
                startActivity(Intent(this@MainActivity, SecretActivity::class.java))
            }
        }
    }
    private lateinit var sharedPreference: SharedPreference
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        startAuthentication(intent)
    }

    private fun startAuthentication(intent: Intent?) {
        val fcmServices = FCMServices(this)
        fcmServices.startAuthentication(intent)
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreference = SharedPreference(applicationContext)

        configureReceiver(this)
        startAuthentication(intent)

        //Checking whether the location permission granted ore not
        locationClient = LocationClient(this, null)

        seeDBLocation.setOnClickListener {

            if (sharedPreference.isLocationServiceOn() &&
                isServiceRunning(MyLocationService::class.java as Class<Any>)) {
                val intent = Intent(this, LocationListActivity::class.java)
                startActivity(intent)
            }
            else {
                MyLocationService.settings = MyLocationService.Settings("Location Tracking",
                    "tracking location enable",
                    R.drawable.notification,
                    MainActivity::class.java)
                MyLocationService.startService(this)

            }

        }

        viewFCMBtn.setOnClickListener {
            sharedPreference.getFCMToken().let {
                Log.d("TOKEN_TAG", "token : $it")
                if (it.isNotEmpty())
                    openFCMPopup(it)
            }
        }
        viewAccessBtn.setOnClickListener {
            sharedPreference.getAccessToken().let {
                if (it.isNotEmpty())
                    openFCMPopup(it)
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fcmMessageService = FCMMessageService()
            fcmMessageService.registerChannel(
                this,
                "CHANNEL_NAME",
                "CHANNEL_ID",
                "",
                R.drawable.notification,
                R.drawable.play
            )

            fcmMessageService.registerChannel(
                this,
                MyLocationService.locationChannel,
                MyLocationService.locationChannel,
                "",
                R.drawable.notification,
                R.drawable.notification
            )

            fcmMessageService.startReceiver(
                object : FCMTokenCallback {
                    override fun getToken(token: String) {
                        Log.d("TOKEN_TAG", "getToken: $token")
//                        openFCMPopup(token)
                    }

                    override fun passIntentExtras(intent: Intent) {
                        val fcmServices = FCMServices(this@MainActivity)
                        fcmServices.startAuthentication(intent)
                    }
                },
                this
            )
        }






        val keyHash = AppSignatureHelper(this).getAppSignatures()[0]
        Log.v("OTP keyHash", keyHash)



        val applicationInteractionClass = ApplicationInteractionClass(this)
        applicationInteractionClass.setInitialConfiguration(
            """{"version":"001","applicationName":"r-mfa","applicationUUID":"841b7452-cc15-4d6c-9585-98b42972026d","configurationUrl":"https:\/\/ksms.amdtelecom.net\/mfa\/config.php","configurationCallRetryDelay":5,"configurationCallRetries":3,"smsOtpType":"simple","fireBaseConsumer":"https:\/\/ksms.amdtelecom.net\/mfa\/fireConsumer.php","fireBaseConsumerRetryDelay":5,"fireBaseConsumerCallRetries":3,"mainystem":"https:\/\/ksms.amdtelecom.net\/mfa\/main.php","mainystemRetryDelay":5,"mainSystemCallRetries":3,"hostedSystem":"https:\/\/ksms.amdtelecom.net\/mfa\/hosted.php","hostedMFASystemRetryDelay":5,"hostedMFASystemCallRetries":3}"""
        )

        applicationInteractionClass.initialize()

        applicationInteractionClass.setUserId("6540")

        if (sharedPreference.getAccessToken().isNotEmpty()) {
            logIn.text = "Logout ${sharedPreference.getUserId()}"
        }

        logIn.setOnClickListener {
            if (logIn.text.toString() == "Login") {

                getDetailsForAccessToken(applicationInteractionClass)
            }
            else {
//                sharedPreference.deletePreference()
                Constants.applicationSecret = ""
                Constants.applicationId = ""
                logIn.text = "Login"
                sharedPreference.setAccessToken("")
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(this)
        // destroying the broadcast receiver
        if (::fcmMessageService.isInitialized)
            fcmMessageService.destroyReceiver(this)
    }

    @SuppressLint("ResourceType")
    private fun openFCMPopup(token: String) {
        val inputView = EditText(this)
        val layout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        with(inputView) {
            layoutParams = layout
            keyListener = null
            setTextIsSelectable(true)
            setText(token)
            setPadding(50)
            setBackgroundResource(Color.TRANSPARENT)
        }
        AlertDialog.Builder(this)
            .setTitle("FCM token")
//            .setMessage(token)
            .setView(inputView)
            .setPositiveButton("Copy") { dialog, _ ->
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Copied Text", token)
                clipboard.setPrimaryClip(clip)
                dialog.dismiss()
                Toast.makeText(this, "Token copied successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun getDetailsForAccessToken(applicationInteractionClass: ApplicationInteractionClass) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(com.net.routee.R.layout.pop_up_for_access_token).show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val submitBtn = dialog.findViewById<Button>(R.id.positiveButton)
        val cancelBtn = dialog.findViewById<Button>(R.id.negativeButton)
        val applicationId = dialog.findViewById<EditText>(R.id.applicationId)
        val applicationSecret = dialog.findViewById<EditText>(R.id.applicationSecret)
        submitBtn.setOnClickListener {
            Constants.applicationId = applicationId.text.toString()
            Constants.applicationSecret = applicationSecret.text.toString()
            dialog.dismiss()
            applicationInteractionClass.getAccessToken(
                object: ApplicationInteractionClass.CheckForAccessToken {
                @SuppressLint("SetTextI18n")
                override fun isObtained(boolean: Boolean) {
                    if (boolean)
                        logIn.text = "Logout ${sharedPreference.getUserId()}"
                }

            })
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onOpenScreen(cls: Class<*>?, intent: Intent) {
        startActivity(Intent(this, cls).putExtras(intent))
    }

    private fun isServiceRunning(serviceClass: Class<Any>): Boolean {
        val activityManager: ActivityManager =
            this.application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services: List<ActivityManager.RunningServiceInfo> = activityManager.getRunningServices(Int.MAX_VALUE)
        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.className == serviceClass.name) {
                return true
            }
        }
        return false
    }

}
