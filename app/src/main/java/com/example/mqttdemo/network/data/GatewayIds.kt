package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class GatewayIds(
	@SerializedName("gateway_id") val gatewayId: String,
	@SerializedName("eui") val eui: String
)
