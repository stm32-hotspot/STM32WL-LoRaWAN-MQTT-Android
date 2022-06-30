package com.example.mqttdemo.view.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.mqttdemo.R
import com.example.mqttdemo.databinding.ActivityDashboardBinding
import com.example.mqttdemo.extension.decodeHex
import com.example.mqttdemo.extension.showToast
import com.example.mqttdemo.network.MqttClientManager
import com.example.mqttdemo.network.response.toCallbackUpResponse
import com.example.mqttdemo.view.dashboard.sensor.SensorItem
import timber.log.Timber

class DashboardActivity : AppCompatActivity(), DeviceAdapter.Delegate {

	private lateinit var binding: ActivityDashboardBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = DataBindingUtil.setContentView<ActivityDashboardBinding>(
			this@DashboardActivity,
			R.layout.activity_dashboard
		).apply {
			deviceAdapter = DeviceAdapter(this@DashboardActivity)

			// Add Device Button Clicked
			deviceIdButton.setOnClickListener { addNewDevice() }

			// Change Activity Name
			title = "MQTT Dashboard"
		}
	}

	// Add a new device.
	private fun addNewDevice() {
		binding.run {
			if (deviceIdInput.text.isNotEmpty()) {
				// Device IDs: "eui-0080e115000a9446" & "eui-0080e1aaaaaaaaaa"
				val deviceId = deviceIdInput.text.toString()
				val deviceItem = DeviceItem(deviceId, SensorItem.itemList())

				// Block Duplicate Item (Device ID) from being added
				if (deviceAdapter?.checkItemExists(deviceItem) == false) {
					MqttClientManager.addEndNode(deviceId)
					deviceAdapter?.addItem(deviceItem)
					subscribeMqttCallback()
				} else {
					showToast("Can't Add Duplicate Device")
				}
				deviceIdInput.setText("")
			}
		}
	}

	private fun subscribeMqttCallback() {
		MqttClientManager.setSubscribeCallbackUP { deviceId, message ->
			Timber.d("Subscribe Topic = up, device id = $deviceId, message = $message")

			val response = message?.toCallbackUpResponse() ?: return@setSubscribeCallbackUP
			if (deviceId != null) {
				binding.deviceAdapter?.updateData(deviceId, response.dashboardData)
			}

			showToast("UP Message Received from $deviceId")
		}

		MqttClientManager.setSubscribeCallbackJoin { deviceId, message ->
            showToast("JOIN Message Received from $deviceId")
		}

		MqttClientManager.setSubscribeCallbackDownFailed { deviceId, message ->
		}

		MqttClientManager.setSubscribeCallbackServiceData { deviceId, message ->
		}
	}

	// Send Pub Message Button Clicked
	override fun onButtonClick(deviceId: String, message: String) {
		MqttClientManager.sendDownlinkMessage(message.decodeHex(), deviceId)
	}

	override fun onDestroy() {
		super.onDestroy()

		// Disconnect and release all resources.
		MqttClientManager.disconnect()
	}
}
