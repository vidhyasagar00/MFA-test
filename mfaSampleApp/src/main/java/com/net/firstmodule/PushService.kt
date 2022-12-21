package com.net.firstmodule

import android.content.Intent
import com.net.routee.firebaseMessage.FCMAction
import com.net.routee.firebaseMessage.FCMMessageService
import com.google.firebase.messaging.RemoteMessage


class PushService: FCMMessageService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP

        super.handleMessage(
            remoteMessage = message, notificationIntent = intent,
            actions = arrayListOf(
                FCMAction(
                    actionName = "Accept",
                ),
                FCMAction(
                    actionName = "Deny"
                )
            )
        )
    }
}