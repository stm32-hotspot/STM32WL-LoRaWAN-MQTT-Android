package com.example.mqttdemo.extension

// Converts Hex String to ByteArray
// https://stackoverflow.com/questions/66613717/kotlin-convert-hex-string-to-bytearray
fun String.decodeHex(): ByteArray {
	return chunked(2)
		.map { it.toInt(16).toByte() }
		.toByteArray()
}
