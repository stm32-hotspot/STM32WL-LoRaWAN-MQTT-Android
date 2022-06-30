package com.example.mqttdemo.extension

import java.math.RoundingMode
import java.text.DecimalFormat

// Round Float Values to 2 Decimal Points
fun Float.roundOffDecimal(): Float {
	return DecimalFormat("#.##").apply {
		roundingMode = RoundingMode.CEILING
	}.format(this).toFloat()
}
