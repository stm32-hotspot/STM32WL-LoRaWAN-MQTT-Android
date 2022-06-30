package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class EndDeviceIds(
	@SerializedName("device_id") val deviceId: String,
	@SerializedName("application_ids") val applicationIds: ApplicationIds,
	@SerializedName("dev_eui") val devEui: String,
	@SerializedName("join_eui") val joinEui: String,
	@SerializedName("dev_addr") val devAddress: String
)
