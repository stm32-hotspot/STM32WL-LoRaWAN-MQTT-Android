package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class Settings(
	@SerializedName("data_rate") val dataRate: DataRate,
	@SerializedName("coding_rate") val codingRate: String,
	@SerializedName("frequency") val frequency: Int,
	@SerializedName("timestamp") val timestamp: ULong
)
