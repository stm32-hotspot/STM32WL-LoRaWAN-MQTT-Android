package com.example.mqttdemo

import android.app.Application
import timber.log.Timber

class MQTTApplication : Application() {

	override fun onCreate() {
		super.onCreate()

		// Install Timber for logging.
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
	}
}
