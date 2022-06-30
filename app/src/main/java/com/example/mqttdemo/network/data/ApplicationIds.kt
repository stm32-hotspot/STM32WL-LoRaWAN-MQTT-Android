package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class ApplicationIds(
	@SerializedName("application_id") val applicationId: String
)
