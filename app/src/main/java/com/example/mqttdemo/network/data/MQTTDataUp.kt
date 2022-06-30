package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class MQTTDataUp(
	@SerializedName("end_device_ids") val endDeviceIds: EndDeviceIds,
	@SerializedName("correlation_ids") val correlationIds: List<String>,
	@SerializedName("received_at") val receivedAt: String,
	@SerializedName("uplink_message") val uplinkMessage: UplinkMessage
)
