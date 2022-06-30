package com.example.mqttdemo.extension

import android.content.Context
import android.widget.Toast

// Shows a toast message
fun Context.showToast(text: String) {
	Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
