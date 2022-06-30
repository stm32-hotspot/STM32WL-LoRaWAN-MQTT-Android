package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class DataRate(
	@SerializedName("lora") val lora: Lora
)
