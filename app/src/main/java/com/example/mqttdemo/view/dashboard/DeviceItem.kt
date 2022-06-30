package com.example.mqttdemo.view.dashboard

import com.example.mqttdemo.view.dashboard.sensor.SensorItem

data class DeviceItem(
	var deviceId: String,
	var sensorList: List<SensorItem>,
	var visibility: Boolean = false
)
