package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class Lora(
	@SerializedName("bandwidth") val bandwidth: Int,
	@SerializedName("spreading_factor") val spreading_factor: Int
)
