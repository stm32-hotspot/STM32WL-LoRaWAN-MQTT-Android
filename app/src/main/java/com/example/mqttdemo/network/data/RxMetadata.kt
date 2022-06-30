package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class RxMetadata(
	@SerializedName("gateway_ids") val gatewayIds: GatewayIds,
	@SerializedName("timestamp") val timestamp: ULong, // This value can exceed 2^31
	@SerializedName("rssi") val rssi: Int,
	@SerializedName("channel_rssi") val channelRssi: Int,
	@SerializedName("snr") val snr: Double,
	@SerializedName("uplink_token") val uplinkToken: String,
	@SerializedName("channel_index") val channelIndex: Int
)
