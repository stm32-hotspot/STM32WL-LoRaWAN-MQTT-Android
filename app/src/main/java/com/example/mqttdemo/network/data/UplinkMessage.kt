package com.example.mqttdemo.network.data

import com.google.gson.annotations.SerializedName

data class UplinkMessage(
	@SerializedName("session_session_key_id_id") val session_session_key_id_id: String,
	@SerializedName("f_port") val f_port: Int,
	@SerializedName("f_cnt") val f_cnt: Int,
	@SerializedName("frm_payload") val frmPayload: String,
	@SerializedName("rx_metadata") val rxMetadata: List<RxMetadata>,
	@SerializedName("settings") val settings: Settings,
	@SerializedName("received_at") val receivedAt: String,
	@SerializedName("consumed_airtime") val consumedAirtime: String,
	@SerializedName("network_ids") val networkIds: NetworkIds
)
