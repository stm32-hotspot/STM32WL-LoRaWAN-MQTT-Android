package com.example.mqttdemo.view.connection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.mqttdemo.R
import com.example.mqttdemo.databinding.ActivityConnectionBinding
import com.example.mqttdemo.extension.showToast
import com.example.mqttdemo.network.MqttClientManager
import com.example.mqttdemo.view.dashboard.DashboardActivity

class ConnectionActivity : AppCompatActivity() {

	private lateinit var binding: ActivityConnectionBinding
	private val sharedPref by lazy { getSharedPreferences("conInput", MODE_PRIVATE) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = DataBindingUtil.setContentView<ActivityConnectionBinding>(
			this,
			R.layout.activity_connection
		).apply {
			// Load & Set Connection Details if they exist
			userNameInput.setText(sharedPref.getString("userName", ""))
			passwordInput.setText(sharedPref.getString("password", ""))

			// Connect Button Clicked
			connectButton.setOnClickListener { connectMqttServer() }
		}
	}

	private fun connectMqttServer() {
		// if already connected, return.
		if (MqttClientManager.isConnected()) return

		// Show a loading progressbar
		binding.progressbar.isVisible = true

		// Save Connection Input
		sharedPref.edit {
			putString("userName", binding.userNameInput.text.toString())
			putString("password", binding.passwordInput.text.toString())
		}

		MqttClientManager.connect(
			context = applicationContext,
			name = binding.userNameInput.text.toString(),
			pw = binding.passwordInput.text.toString(),
			uri = "tcp://${binding.uriInput.selectedItem}",
			callback = object : MqttClientManager.ConnectionCallback {
				override fun onSuccess() {
					// Create an intent to start Dashboard Activity
					val intent = Intent(this@ConnectionActivity, DashboardActivity::class.java)
					startActivity(intent)
					showToast("Connected!")
				}

				override fun onFailure() {
					binding.progressbar.isVisible = false
					showToast("Connection failed..")
				}
			}
		)
	}
}
