package com.bics.expense.receptionistmodule.videoCall

import android.app.Application
import com.quickblox.auth.session.QBSettings
import com.quickblox.videochat.webrtc.QBRTCConfig


class QuickBloxSdk : Application() {
    private val applicationID = "103585"
    private val authKey = "ak_WvTDPfNMQvZhutL"
    private val authSecret = "as_qaf2fMgQ55aw2ys"
    private val accountKey = "ack_rzPDDpV1MehAimtFhFZb"

    override fun onCreate() {
        super.onCreate()
        initCredentials()
    }

    private fun initCredentials() {
        QBSettings.getInstance().init(applicationContext, applicationID, authKey, authSecret)
        QBSettings.getInstance().accountKey = accountKey
        QBRTCConfig.setDebugEnabled(true)


    }
}