package com.example.mqttdemo.view.dashboard

import com.example.mqttdemo.view.dashboard.sensor.SensorData

data class DashboardData(
	var rssi: Float,
	var snr: Float,
	var sensorData: SensorData
)
