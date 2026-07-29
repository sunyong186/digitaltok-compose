package com.yourcompany.digitaltok

import android.app.Application
import com.yourcompany.digitaltok.data.network.RetrofitClient

class DigitalTokApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // RetrofitClient에 Application Context 주입
        RetrofitClient.init(this)
    }
}
