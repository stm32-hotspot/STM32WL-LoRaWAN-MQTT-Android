package com.example.mqttdemo.network.response

import android.util.Base64
import com.example.mqttdemo.network.MqttClientManager.SubscribeCallbackUP
import com.example.mqttdemo.network.data.MQTTDataUp
import com.example.mqttdemo.view.dashboard.DashboardData
import com.example.mqttdemo.view.dashboard.sensor.SensorData
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
* A wrapper class to gather the response of the [SubscribeCallbackUP] interface.
*/
data class CallbackUpResponse(
	val mqttDataUp: MQTTDataUp
) {
	private val decodedHexString: String = mqttDataUp.uplinkMessage.frmPayload.let { payload ->
		val flag = Base64.NO_WRAP or Base64.URL_SAFE
		val decodedBytes = Base64.decode(payload, flag)
		decodedBytes.joinToString("") {
			"%02x".format(it)
		}
	}


	private val rssi = mqttDataUp.uplinkMessage.rxMetadata[0].rssi.toFloat()
	private val snr = mqttDataUp.uplinkMessage.rxMetadata[0].snr.toFloat()

    private val pressureHigh = decodedHexString.substring(2, 4).toInt(radix = 16)
    private val pressureLow = decodedHexString.substring(4, 6).toInt(radix = 16)
	private val pressure = ((pressureHigh shl 8) or pressureLow).toFloat() / 10

	private val temp = decodedHexString.substring(6, 8).toLong(radix = 16).toFloat()

    private val humidHigh = decodedHexString.substring(8, 10).toInt(radix = 16)
    private val humidLow = decodedHexString.substring(10, 12).toInt(radix = 16)
	private val humidity = ((humidHigh shl 8) or humidLow).toFloat() / 10

	private val sensorData = SensorData(pressure, temp, humidity)
	val dashboardData = DashboardData(rssi, snr, sensorData)
	val timeStamp: Float = mqttDataUp.uplinkMessage.rxMetadata[0].timestamp.toFloat()
}

/**
* An extension function to create the [CallbackUpResponse] from the [MqttMessage].
*/
fun MqttMessage.toCallbackUpResponse(): CallbackUpResponse {
	val gson = Gson()
	val json = String(payload).takeIf { it.isNotEmpty() }
	val mqttDataUp = gson.fromJson(json, MQTTDataUp::class.java)
	return CallbackUpResponse(mqttDataUp = mqttDataUp)
}
