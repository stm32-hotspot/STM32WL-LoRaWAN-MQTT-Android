package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class NetworkIds(
	@SerializedName("net_id") val netId: Int,
	@SerializedName("tenant_id") val tenantId: String,
	@SerializedName("cluster_id") val clusterId: String
)
